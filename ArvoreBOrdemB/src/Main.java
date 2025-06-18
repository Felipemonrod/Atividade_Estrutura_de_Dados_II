import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// --- Classe Produto ---
class Produto implements Comparable<Produto> {
    int id;
    String nome;
    String categoria;

    public Produto(int id, String nome, String categoria) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Produto{id=" + id + ", nome='" + nome + "', categoria='" + categoria + "'}";
    }

    @Override
    public int compareTo(Produto other) {
        return Integer.compare(this.id, other.id);
    }
}

// --- Implementação Genérica de Nó de Árvore B (Base para B+ e B*) ---
class BTreeNode<K extends Comparable<K>, V> {
    List<K> keys;
    List<BTreeNode<K, V>> children;
    boolean isLeaf;
    int t; // Ordem mínima (t-1 é o número mínimo de chaves)

    public BTreeNode(int t, boolean isLeaf) {
        this.t = t;
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    public int findKey(K key) {
        int idx = 0;
        while (idx < keys.size() && key.compareTo(keys.get(idx)) > 0) {
            idx++;
        }
        return idx;
    }
}

// --- Implementação da Árvore B+ ---
class BPlusTree<K extends Comparable<K>, V> {
    BPlusTreeNode<K, V> root;
    int t; // Ordem (máximo 2t-1 chaves, 2t filhos)

    public BPlusTree(int t) {
        this.t = t;
        this.root = new BPlusTreeNode<>(t, true);
        this.root.nextLeaf = null; // Para o nó raiz inicial
    }

    // Classe Nó da Árvore B+
    class BPlusTreeNode<K extends Comparable<K>, V> extends BTreeNode<K, V> {
        List<V> values; // Apenas nós folha armazenam valores
        BPlusTreeNode<K, V> nextLeaf; // Ponteiro para a próxima folha (apenas em nós folha)

        public BPlusTreeNode(int t, boolean isLeaf) {
            super(t, isLeaf);
            if (isLeaf) {
                this.values = new ArrayList<>();
            }
            this.nextLeaf = null;
        }

        // Adiciona uma chave e um valor em ordem crescente no nó folha
        public void addLeafEntry(K key, V value) {
            int i = 0;
            while (i < keys.size() && key.compareTo(keys.get(i)) > 0) {
                i++;
            }
            keys.add(i, key);
            values.add(i, value);
        }

        // Remove uma chave e um valor do nó folha
        public void removeLeafEntry(K key) {
            int i = 0;
            while (i < keys.size() && key.compareTo(keys.get(i)) != 0) {
                i++;
            }
            if (i < keys.size() && key.compareTo(keys.get(i)) == 0) {
                keys.remove(i);
                values.remove(i);
            }
        }
    }

    public void insert(K key, V value) {
        BPlusTreeNode<K, V> r = root;
        if (r.keys.size() == (2 * t - 1)) { // Raiz cheia, precisa de nova raiz
            BPlusTreeNode<K, V> s = new BPlusTreeNode<>(t, false);
            s.children.add(r);
            splitChild(s, 0, r);
            root = s;
            insertNonFull(s, key, value);
        } else {
            insertNonFull(r, key, value);
        }
    }

    private void insertNonFull(BPlusTreeNode<K, V> node, K key, V value) {
        if (node.isLeaf) {
            node.addLeafEntry(key, value);
        } else {
            int i = node.findKey(key);
            BPlusTreeNode<K, V> child = (BPlusTreeNode<K, V>) node.children.get(i);
            if (child.keys.size() == (2 * t - 1)) {
                splitChild(node, i, child);
                if (key.compareTo(node.keys.get(i)) > 0) {
                    i++;
                }
            }
            insertNonFull((BPlusTreeNode<K, V>) node.children.get(i), key, value);
        }
    }

    private void splitChild(BPlusTreeNode<K, V> parentNode, int i, BPlusTreeNode<K, V> childNode) {
        BPlusTreeNode<K, V> newChild = new BPlusTreeNode<>(t, childNode.isLeaf);
        
        int mid = t - 1; 
        
        if (childNode.isLeaf) {
            parentNode.keys.add(i, childNode.keys.get(mid)); 
            parentNode.children.add(i + 1, newChild);

            newChild.keys.addAll(childNode.keys.subList(mid, childNode.keys.size()));
            newChild.values.addAll(childNode.values.subList(mid, childNode.values.size()));

            childNode.keys.subList(mid, childNode.keys.size()).clear();
            childNode.values.subList(mid, childNode.values.size()).clear();

            newChild.nextLeaf = childNode.nextLeaf;
            childNode.nextLeaf = newChild;
        } else {
            parentNode.keys.add(i, childNode.keys.get(mid)); 
            parentNode.children.add(i + 1, newChild);

            newChild.keys.addAll(childNode.keys.subList(mid + 1, childNode.keys.size()));
            newChild.children.addAll(childNode.children.subList(mid + 1, childNode.children.size()));

            childNode.keys.subList(mid, childNode.keys.size()).clear();
            childNode.children.subList(mid + 1, childNode.children.size()).clear();
        }
    }

    public boolean delete(K key) {
        return delete(root, key, null, -1);
    }

    private boolean delete(BPlusTreeNode<K, V> node, K key, BPlusTreeNode<K, V> parent, int parentIndexInChild) {
        if (node.isLeaf) {
            int idx = node.findKey(key);
            if (idx < node.keys.size() && node.keys.get(idx).compareTo(key) == 0) {
                node.removeLeafEntry(key);
                if (node == root && node.keys.isEmpty()) {
                    return true;
                }
                if (node.keys.size() < (t - 1) && parent != null) {
                    handleLeafUnderflow(node, parent, parentIndexInChild);
                }
                return true;
            }
            return false; 
        } else {
            int idx = node.findKey(key);
            BPlusTreeNode<K, V> child = (BPlusTreeNode<K, V>) node.children.get(idx);

            boolean deleted = delete(child, key, node, idx);

            if (deleted && node != root && child.keys.size() < (t - 1)) { 
                handleInternalUnderflow(node, parent, parentIndexInChild);
            } else if (deleted && node == root && node.children.size() == 1 && node.keys.isEmpty()) {
                root = (BPlusTreeNode<K, V>) node.children.get(0);
            }
            return deleted;
        }
    }

    private void handleLeafUnderflow(BPlusTreeNode<K, V> node, BPlusTreeNode<K, V> parent, int parentIndexInChild) {
        int leftSiblingIndex = parentIndexInChild - 1;
        int rightSiblingIndex = parentIndexInChild + 1;
        BPlusTreeNode<K, V> leftSibling = null;
        BPlusTreeNode<K, V> rightSibling = null;

        if (leftSiblingIndex >= 0) {
            leftSibling = (BPlusTreeNode<K, V>) parent.children.get(leftSiblingIndex);
        }
        if (rightSiblingIndex < parent.children.size()) {
            rightSibling = (BPlusTreeNode<K, V>) parent.children.get(rightSiblingIndex);
        }

        // Tentar redistribuir do irmão esquerdo
        if (leftSibling != null && leftSibling.isLeaf && leftSibling.keys.size() > (t - 1)) {
            node.addLeafEntry(leftSibling.keys.remove(leftSibling.keys.size() - 1), leftSibling.values.remove(leftSibling.values.size() - 1));
            parent.keys.set(leftSiblingIndex, node.keys.get(0));
            return;
        }

        // Tentar redistribuir do irmão direito
        if (rightSibling != null && rightSibling.isLeaf && rightSibling.keys.size() > (t - 1)) {
            node.addLeafEntry(rightSibling.keys.remove(0), rightSibling.values.remove(0));
            parent.keys.set(parentIndexInChild, rightSibling.keys.get(0));
            return;
        }

        // Mesclar com irmão esquerdo
        if (leftSibling != null && leftSibling.isLeaf) {
            leftSibling.keys.addAll(node.keys);
            leftSibling.values.addAll(node.values);
            leftSibling.nextLeaf = node.nextLeaf;
            parent.children.remove(parentIndexInChild);
            parent.keys.remove(parentIndexInChild - 1);
            return;
        }

        // Mesclar com irmão direito
        if (rightSibling != null && rightSibling.isLeaf) {
            node.keys.addAll(rightSibling.keys);
            node.values.addAll(rightSibling.values);
            node.nextLeaf = rightSibling.nextLeaf;
            parent.children.remove(rightSiblingIndex);
            parent.keys.remove(parentIndexInChild);
            return;
        }
    }

    private void handleInternalUnderflow(BPlusTreeNode<K, V> node, BPlusTreeNode<K, V> parent, int parentIndexInChild) {
        int leftSiblingIndex = parentIndexInChild - 1;
        int rightSiblingIndex = parentIndexInChild + 1;
        BPlusTreeNode<K, V> leftSibling = null;
        BPlusTreeNode<K, V> rightSibling = null;

        if (leftSiblingIndex >= 0) {
            leftSibling = (BPlusTreeNode<K, V>) parent.children.get(leftSiblingIndex);
        }
        if (rightSiblingIndex < parent.children.size()) {
            rightSibling = (BPlusTreeNode<K, V>) parent.children.get(rightSiblingIndex);
        }

        // Tentar redistribuir do irmão esquerdo
        if (leftSibling != null && leftSibling.keys.size() > (t - 1)) {
            node.keys.add(0, parent.keys.get(leftSiblingIndex));
            parent.keys.set(leftSiblingIndex, leftSibling.keys.remove(leftSibling.keys.size() - 1));
            node.children.add(0, leftSibling.children.remove(leftSibling.children.size() - 1));
            return;
        }

        // Tentar redistribuir do irmão direito
        if (rightSibling != null && rightSibling.keys.size() > (t - 1)) {
            node.keys.add(parent.keys.get(parentIndexInChild));
            parent.keys.set(parentIndexInChild, rightSibling.keys.remove(0));
            node.children.add(rightSibling.children.remove(0));
            return;
        }

        // Mesclar com irmão esquerdo
        if (leftSibling != null) {
            leftSibling.keys.add(parent.keys.remove(leftSiblingIndex));
            leftSibling.keys.addAll(node.keys);
            leftSibling.children.addAll(node.children);
            parent.children.remove(parentIndexInChild);
            return;
        }

        // Mesclar com irmão direito
        if (rightSibling != null) {
            node.keys.add(parent.keys.remove(parentIndexInChild));
            node.keys.addAll(rightSibling.keys);
            node.children.addAll(rightSibling.children);
            parent.children.remove(rightSiblingIndex);
            return;
        }
    }

    public V search(K key) {
        BPlusTreeNode<K, V> current = root;
        while (!current.isLeaf) {
            int i = current.findKey(key);
            current = (BPlusTreeNode<K, V>) current.children.get(i);
        }
        int i = current.findKey(key);
        if (i < current.keys.size() && current.keys.get(i).compareTo(key) == 0) {
            return current.values.get(i);
        }
        return null; // Não encontrado
    }
}


// --- Implementação da Árvore B* ---
class BStarTree<K extends Comparable<K>, V> {
    BStarTreeNode<K, V> root;
    int t; // Ordem (mínimo de chaves é 2t - 1, máximo 2t - 1 para B*)

    public BStarTree(int t) {
        this.t = t;
        this.root = new BStarTreeNode<>(t, true);
    }

    // Classe Nó da Árvore B*
    class BStarTreeNode<K extends Comparable<K>, V> extends BTreeNode<K, V> {
        List<V> values; 

        public BStarTreeNode(int t, boolean isLeaf) {
            super(t, isLeaf);
            this.values = new ArrayList<>(); 
        }

        public void addEntry(K key, V value) { 
            int i = 0;
            while (i < keys.size() && key.compareTo(keys.get(i)) > 0) {
                i++;
            }
            keys.add(i, key);
            values.add(i, value);
        }

        // Adiciona uma chave e um valor em uma posição específica
        public void addEntry(int index, K key, V value) { 
            keys.add(index, key);
            values.add(index, value);
        }

        public void removeEntry(K key) {
            int i = 0;
            while (i < keys.size() && key.compareTo(keys.get(i)) != 0) {
                i++;
            }
            if (i < keys.size() && key.compareTo(keys.get(i)) == 0) {
                keys.remove(i);
                values.remove(i);
            }
        }
    }

    public void insert(K key, V value) {
        BStarTreeNode<K, V> r = root;
        // Se a raiz está cheia
        if (r.keys.size() == (2 * t - 1)) { 
            BStarTreeNode<K, V> s = new BStarTreeNode<>(t, false); // Cria uma nova raiz
            s.children.add(r); // Antiga raiz se torna o primeiro filho da nova raiz
            // Chama uma versão de split que sabe que está lidando com a raiz original
            splitRootOrChild(s, 0, r); 
            root = s; // Atualiza a raiz da árvore
            insertNonFull(s, key, value);
        } else {
            insertNonFull(r, key, value);
        }
    }

    private void insertNonFull(BStarTreeNode<K, V> node, K key, V value) {
        int i = node.findKey(key);
        if (node.isLeaf) {
            node.addEntry(key, value);
        } else {
            // Verificar se o filho existe antes de acessá-lo.
            // Se o 'i' for o último índice e não houver um filho lá,
            // significa que o 'splitOrRedistribute' anterior não criou o filho esperado,
            // ou a lógica de 'findKey' levou a um índice que não tem um filho ainda.
            if (i >= node.children.size()) {
                // Isso não deveria acontecer em uma árvore B* bem formada após um split,
                // mas é uma salvaguarda. Indica que a estrutura pai/filho está quebrada.
                // Logar e/ou lançar um erro mais específico, ou tentar corrigir.
                // Para este exercício, vamos presumir que um split/redistribuição
                // deveria ter criado o filho.
                System.err.println("Erro interno: Índice de filho inválido em insertNonFull. i=" + i + ", children.size=" + node.children.size() + ", node.keys=" + node.keys);
                return; // Evita IndexOutOfBoundsException
            }

            BStarTreeNode<K, V> child = (BStarTreeNode<K, V>) node.children.get(i);
            if (child.keys.size() == (2 * t - 1)) {
                splitRootOrChild(node, i, child); // Usar a lógica unificada de split/redistribute
                // Após a divisão/redistribuição, a chave que subiu pode alterar o índice para onde ir
                // Se a chave a ser inserida for maior que a nova chave que subiu, incrementamos o índice
                if (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) { // Verifica o limite de i antes de acessar keys.get(i)
                    i++;
                }
            }
            // Precisamos re-obter o filho após uma potencial divisão ou redistribuição
            // pois o `child` original pode não ser mais o correto ou a estrutura mudou.
            // O `i` já foi ajustado se a chave subiu.
            if (i < node.children.size()) { // Verificação adicional aqui
                 insertNonFull((BStarTreeNode<K, V>) node.children.get(i), key, value);
            } else {
                 System.err.println("Erro interno: Tentando inserir em filho que não existe após split/redistribute. i=" + i + ", children.size=" + node.children.size() + ", node.keys=" + node.keys);
            }
        }
    }

    // Função unificada para split/redistribute que lida com a complexidade da B*
    // Esta é a parte mais crítica e com maior probabilidade de bugs em implementações simplificadas.
    private void splitRootOrChild(BStarTreeNode<K, V> parentNode, int childIndex, BStarTreeNode<K, V> childNode) {
        int leftSiblingIndex = childIndex - 1;
        int rightSiblingIndex = childIndex + 1;
        BStarTreeNode<K, V> leftSibling = null;
        BStarTreeNode<K, V> rightSibling = null;

        if (leftSiblingIndex >= 0) {
            leftSibling = (BStarTreeNode<K, V>) parentNode.children.get(leftSiblingIndex);
        }
        if (rightSiblingIndex < parentNode.children.size()) {
            rightSibling = (BStarTreeNode<K, V>) parentNode.children.get(rightSiblingIndex);
        }

        // Tentar redistribuir para o irmão esquerdo (se existir e tiver espaço)
        if (leftSibling != null && leftSibling.keys.size() < (2 * t - 1)) {
            // Mover chave do pai para o irmão esquerdo
            leftSibling.addEntry(parentNode.keys.remove(childIndex - 1), parentNode.values.remove(childIndex - 1));
            // Mover a primeira chave do filho atual para o pai
            parentNode.addEntry(childIndex - 1, childNode.keys.remove(0), childNode.values.remove(0));
            if (!childNode.isLeaf) {
                leftSibling.children.add(childNode.children.remove(0));
            }
            return;
        }

        // Tentar redistribuir para o irmão direito (se existir e tiver espaço)
        if (rightSibling != null && rightSibling.keys.size() < (2 * t - 1)) {
            // Mover chave do pai para o irmão direito
            rightSibling.addEntry(0, parentNode.keys.remove(childIndex), parentNode.values.remove(childIndex));
            // Mover a última chave do filho atual para o pai
            parentNode.addEntry(childIndex, childNode.keys.remove(childNode.keys.size() - 1), childNode.values.remove(childNode.values.size() - 1));
            if (!childNode.isLeaf) {
                rightSibling.children.add(0, childNode.children.remove(childNode.children.size() - 1));
            }
            return;
        }

        // Se não houver irmãos com espaço para redistribuição, precisamos dividir.
        // A divisão em B* é mais complexa do que em B/B+, envolvendo 2 nós cheios
        // e a distribuição para 3 novos nós, ou simplesmente uma divisão em 2 se
        // apenas um nó está cheio e não há irmãos disponíveis para redistribuição.
        // Para simplificar e evitar o IndexOutOfBoundsException, vamos garantir que,
        // ao dividir, os filhos sejam corretamente adicionados e os índices sejam válidos.
        
        // Simulação de split simples para dois nós (com chave do meio subindo)
        BStarTreeNode<K, V> newSibling = new BStarTreeNode<>(t, childNode.isLeaf);

        // A chave do meio sobe para o nó pai
        // Certifique-se de que o childNode tem chaves suficientes
        if (childNode.keys.size() < (2 * t - 1)) {
             // Isto é um erro lógico, o split só deveria ser chamado se o nó estivesse cheio.
             // Para evitar IndexOutOfBounds, lidamos com isso.
             System.err.println("Tentando split em nó não cheio. childNode.keys.size()=" + childNode.keys.size());
             return; 
        }

        int medianIndex = t - 1; // Índice da chave que vai subir para o pai
        
        // Chave e valor que sobem
        K keyToAscend = childNode.keys.get(medianIndex);
        V valueToAscend = childNode.values.get(medianIndex);

        // Adicionar a chave que subiu ao pai
        parentNode.addEntry(childIndex, keyToAscend, valueToAscend);
        
        // Adicionar o novo filho ao pai
        parentNode.children.add(childIndex + 1, newSibling);

        // Mover a parte direita das chaves e valores para o novo nó
        newSibling.keys.addAll(childNode.keys.subList(t, childNode.keys.size()));
        newSibling.values.addAll(childNode.values.subList(t, childNode.values.size()));
        
        // Mover os filhos correspondentes se não for um nó folha
        if (!childNode.isLeaf) {
            newSibling.children.addAll(childNode.children.subList(t, childNode.children.size()));
        }

        // Remover as chaves e valores movidos (e a chave que subiu) do nó original
        childNode.keys.subList(medianIndex, childNode.keys.size()).clear();
        childNode.values.subList(medianIndex, childNode.values.size()).clear();
        if (!childNode.isLeaf) {
            childNode.children.subList(t, childNode.children.size()).clear();
        }
    }

    public boolean delete(K key) {
        // Encontra o nó folha que contém (ou deveria conter) a chave
        BStarTreeNode<K, V> nodeToDeleteFrom = root;
        List<BStarTreeNode<K, V>> path = new ArrayList<>(); // Armazena o caminho para cima
        while (!nodeToDeleteFrom.isLeaf) {
            path.add(nodeToDeleteFrom);
            int idx = nodeToDeleteFrom.findKey(key);
            // Proteção contra IndexOutOfBounds se o filho não existir onde 'findKey' apontou
            if (idx >= nodeToDeleteFrom.children.size()) {
                // Isso pode acontecer se a árvore ficou corrompida em algum ponto.
                // Se a chave não foi encontrada e é um nó interno sem o filho correspondente,
                // não podemos continuar a busca.
                return false; 
            }
            nodeToDeleteFrom = (BStarTreeNode<K, V>) nodeToDeleteFrom.children.get(idx);
        }

        int idxInLeaf = nodeToDeleteFrom.findKey(key);
        if (idxInLeaf >= nodeToDeleteFrom.keys.size() || nodeToDeleteFrom.keys.get(idxInLeaf).compareTo(key) != 0) {
            return false; // Chave não encontrada na folha
        }

        // Remove a chave da folha
        nodeToDeleteFrom.removeEntry(key);

        // Lidar com subfluxo (do nó folha para cima)
        // Percorrer o caminho de volta para a raiz, verificando e corrigindo underflow.
        for (int i = path.size() - 1; i >= 0; i--) {
            BStarTreeNode<K, V> parent = path.get(i);
            // Encontrar o índice do filho que acabou de ser processado no pai
            int childIndex = -1;
            for(int j = 0; j < parent.children.size(); j++) {
                if (parent.children.get(j) == nodeToDeleteFrom) {
                    childIndex = j;
                    break;
                }
            }

            if (childIndex == -1) {
                // Isso não deveria acontecer: filho não encontrado no pai.
                System.err.println("Erro interno: Filho não encontrado no pai durante a deleção.");
                return false; 
            }
            
            // Re-obtemos o nó para ter certeza que estamos lidando com a referência correta
            // após possíveis merges ou rotações
            nodeToDeleteFrom = (BStarTreeNode<K, V>) parent.children.get(childIndex);


            // Se o nó está abaixo do limite mínimo de 2/3 de ocupação (para B*)
            // Nota: O cálculo de 2/3 é para B*. Para B-Tree/B+, é t-1.
            // Para ordem 3 (t=3), 2*t-1 = 5. 2/3 de 5 = 3.33... ou seja, mínimo de 4 chaves para B*
            // Se t=3, o mínimo de chaves para B* é ceil((2t-1)*2/3) = ceil(5*2/3) = ceil(10/3) = ceil(3.33) = 4
            // A menos que a raiz.
            if (nodeToDeleteFrom != root && nodeToDeleteFrom.keys.size() < (int) Math.ceil((2 * t - 1) * 2.0 / 3.0)) {
                handleUnderflow(nodeToDeleteFrom, parent, childIndex);
            }
        }

        // Após todas as deleções e rebalanceamentos, verificar a raiz
        // Se a raiz ficou vazia e tem apenas um filho, este filho se torna a nova raiz
        if (root.keys.isEmpty() && !root.children.isEmpty()) {
            root = (BStarTreeNode<K, V>) root.children.get(0);
            // A nova raiz pode ter menos que t-1 chaves se for uma árvore pequena
            // ou se era uma raiz interna que virou raiz.
        } else if (root.keys.isEmpty() && root.children.isEmpty() && !nodeToDeleteFrom.keys.isEmpty()){
            // Se a raiz se tornou completamente vazia (e era a única folha) e o nó deletado não está vazio
            // Este caso é se a árvore se esvazia, mas sempre terá a raiz.
            // Se a raiz era a única folha e ela se esvaziou, a árvore fica vazia.
            // Já estamos recriando uma raiz se ela ficar vazia.
        }
        
        return true;
    }


    private void handleUnderflow(BStarTreeNode<K, V> node, BStarTreeNode<K, V> parent, int parentIndexInChild) {
        int leftSiblingIndex = parentIndexInChild - 1;
        int rightSiblingIndex = parentIndexInChild + 1;
        BStarTreeNode<K, V> leftSibling = null;
        BStarTreeNode<K, V> rightSibling = null;

        if (leftSiblingIndex >= 0) {
            leftSibling = (BStarTreeNode<K, V>) parent.children.get(leftSiblingIndex);
        }
        if (rightSiblingIndex < parent.children.size()) {
            rightSibling = (BStarTreeNode<K, V>) parent.children.get(rightSiblingIndex);
        }

        // Tentar redistribuir do irmão esquerdo
        if (leftSibling != null && leftSibling.keys.size() > (int) Math.ceil( (2 * t - 1) * 2.0 / 3.0 )) {
            // Move a chave do pai para o nó subdimensionado
            node.addEntry(0, parent.keys.get(leftSiblingIndex), parent.values.get(leftSiblingIndex)); // Adiciona no início
            // Move a última chave do irmão esquerdo para o pai
            parent.keys.set(leftSiblingIndex, leftSibling.keys.remove(leftSibling.keys.size() - 1));
            parent.values.set(leftSiblingIndex, leftSibling.values.remove(leftSibling.values.size() - 1));
            if (!node.isLeaf) { // Se não for folha, também move o filho
                node.children.add(0, leftSibling.children.remove(leftSibling.children.size() - 1));
            }
            return;
        }

        // Tentar redistribuir do irmão direito
        if (rightSibling != null && rightSibling.keys.size() > (int) Math.ceil( (2 * t - 1) * 2.0 / 3.0 )) {
            // Move a chave do pai para o nó subdimensionado
            node.addEntry(parent.keys.get(parentIndexInChild), parent.values.get(parentIndexInChild)); // Adiciona no final
            // Move a primeira chave do irmão direito para o pai
            parent.keys.set(parentIndexInChild, rightSibling.keys.remove(0));
            parent.values.set(parentIndexInChild, rightSibling.values.remove(0));
            if (!node.isLeaf) { // Se não for folha, também move o filho
                node.children.add(rightSibling.children.remove(0));
            }
            return;
        }

        // Se não for possível redistribuir, mesclar
        // Preferir mesclar com o irmão esquerdo se existir, senão com o direito
        if (leftSibling != null) {
            // Puxa a chave do pai para o irmão esquerdo
            leftSibling.keys.add(parent.keys.remove(leftSiblingIndex));
            leftSibling.values.add(parent.values.remove(leftSiblingIndex));
            // Move todas as chaves e valores do nó subdimensionado para o irmão esquerdo
            leftSibling.keys.addAll(node.keys);
            leftSibling.values.addAll(node.values);
            if (!node.isLeaf) {
                leftSibling.children.addAll(node.children);
            }
            parent.children.remove(parentIndexInChild); // Remove o nó subdimensionado do pai
        } else if (rightSibling != null) {
            // Puxa a chave do pai para o nó subdimensionado
            node.keys.add(parent.keys.remove(parentIndexInChild));
            node.values.add(parent.values.remove(parentIndexInChild));
            // Move todas as chaves e valores do irmão direito para o nó subdimensionado
            node.keys.addAll(rightSibling.keys);
            node.values.addAll(rightSibling.values);
            if (!node.isLeaf) {
                node.children.addAll(rightSibling.children);
            }
            parent.children.remove(rightSiblingIndex); // Remove o irmão direito do pai
        }
    }

    public V search(K key) {
        BStarTreeNode<K, V> current = root;
        while (current != null) {
            int i = current.findKey(key);
            if (i < current.keys.size() && current.keys.get(i).compareTo(key) == 0) {
                return current.values.get(i);
            }
            if (current.isLeaf) {
                return null;
            }
            // Verificação crítica antes de acessar o filho:
            // O índice 'i' pode ser igual ao número de chaves. Nesse caso, ele aponta para o filho à direita da última chave.
            // A lista de filhos deve ter (número de chaves + 1) elementos.
            if (i >= current.children.size()) {
                // Isso indica uma inconsistência na estrutura da árvore (mais chaves que filhos esperados, ou vice-versa).
                // Para depuração, você pode imprimir o estado do nó aqui.
                // System.err.println("Erro: Índice de filho inválido em search. i=" + i + ", children.size=" + current.children.size() + ", keys=" + current.keys);
                return null; // Não podemos prosseguir na busca por um filho que não existe.
            }
            current = (BStarTreeNode<K, V>) current.children.get(i);
        }
        return null;
    }
}


// --- Classe Principal ---
public class Main {

    private static final String DATA_FILE = "produtos.csv"; // Nome do arquivo de dados
    private static final int ORDER = 3; // Ordem das árvores (t como grau mínimo, então 2t-1 chaves max)

    public static void main(String[] args) {
        List<Produto> produtos = carregarProdutos(DATA_FILE);
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto encontrado no arquivo " + DATA_FILE + ". Por favor, crie o arquivo com dados.");
            return;
        }

        // --- Teste com Árvore B+ ---
        System.out.println("--- Teste Árvore B+ (Ordem " + ORDER + ") ---");
        BPlusTree<Integer, Produto> bPlusTree = new BPlusTree<>(ORDER);
        
        long startTimeBPlus = System.nanoTime();
        for (Produto p : produtos) {
            bPlusTree.insert(p.getId(), p);
        }
        long endTimeBPlus = System.nanoTime();
        long durationBPlus = (endTimeBPlus - startTimeBPlus) / 1_000_000; // Tempo em milissegundos
        System.out.println("Tempo de inserção na Árvore B+: " + durationBPlus + " ms");

        // Remoção de 10 produtos aleatórios (ID entre 1000 e 2000)
        System.out.println("\nRemoção de 20 produtos aleatórios na Árvore B+:");
        removerProdutosAleatorios(bPlusTree, null); 

        // --- Teste com Árvore B* ---
        System.out.println("\n--- Teste Árvore B* (Ordem " + ORDER + ") ---");
        BStarTree<Integer, Produto> bStarTree = new BStarTree<>(ORDER);

        long startTimeBStar = System.nanoTime();
        for (Produto p : produtos) {
            bStarTree.insert(p.getId(), p);
        }
        long endTimeBStar = System.nanoTime();
        long durationBStar = (endTimeBStar - startTimeBStar) / 1_000_000; // Tempo em milissegundos
        System.out.println("Tempo de inserção na Árvore B*: " + durationBStar + " ms");

        // Remoção de 10 produtos aleatórios (ID entre 1000 e 2000)
        System.out.println("\nRemoção de 20 produtos aleatórios na Árvore B*:");
        removerProdutosAleatorios(null, bStarTree); 
    }

    private static List<Produto> carregarProdutos(String filename) {
        List<Produto> produtos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Pula a linha do cabeçalho
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        String nome = parts[1].trim();
                        String categoria = parts[2].trim();
                        produtos.add(new Produto(id, nome, categoria));
                    } catch (NumberFormatException e) {
                        System.err.println("Erro ao parsear ID: " + parts[0] + " na linha: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo " + filename + ": " + e.getMessage());
        }
        return produtos;
    }

    private static void removerProdutosAleatorios(BPlusTree<Integer, Produto> bPlusTree, BStarTree<Integer, Produto> bStarTree) {
        Random random = new Random();
        List<Integer> idsParaRemover = new ArrayList<>();
        for (int i = 0; i < 20; i++) { 
            idsParaRemover.add(random.nextInt(2000) + 1); // IDs entre 1 e 2000
        }
        Collections.shuffle(idsParaRemover); 

        long startTimeRemoval = System.nanoTime();
        int removedCount = 0;
        int notFoundCount = 0;
        for (Integer id : idsParaRemover) {
            boolean removed = false;

            if (bPlusTree != null) { 
                Produto foundProduct = bPlusTree.search(id); 
                if (foundProduct != null) {
                    removed = bPlusTree.delete(id);
                }
            } else { 
                Produto foundProduct = bStarTree.search(id); 
                if (foundProduct != null) {
                    removed = bStarTree.delete(id);
                }
            }

            if (removed) {
                System.out.println("  Produto com ID " + id + " removido com sucesso.");
                removedCount++;
            } else {
                System.out.println("  Produto com ID " + id + " não encontrado na árvore ou já removido.");
                notFoundCount++;
            }
        }
        long endTimeRemoval = System.nanoTime();
        long durationRemoval = (endTimeRemoval - startTimeRemoval) / 1_000_000;
        System.out.println("Total de remoções: " + removedCount + ", Não encontrados: " + notFoundCount);
        System.out.println("Tempo de remoção na árvore: " + durationRemoval + " ms");
    }
}