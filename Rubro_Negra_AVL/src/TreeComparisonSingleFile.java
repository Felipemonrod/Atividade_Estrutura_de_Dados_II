import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

class AVLNode {
    int key;
    int height;
    AVLNode left;
    AVLNode right;

    AVLNode(int d) {
        key = d;
        height = 1; 
        left = null;
        right = null;
    }
}

class AVLTree {
    AVLNode root;

    public AVLTree() {
        this.root = null;
    }

    private int height(AVLNode N) {
        if (N == null)
            return 0;
        return N.height;
    }

    private int max(int a, int b) {
        return (a > b) ? a : b;
    }
    private AVLNode rightRotate(AVLNode y) {
        if (y == null || y.left == null) return y;
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = max(height(y.left), height(y.right)) + 1;
        x.height = max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private AVLNode leftRotate(AVLNode x) {
        if (x == null || x.right == null) return x;
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = max(height(x.left), height(x.right)) + 1;
        y.height = max(height(y.left), height(y.right)) + 1;

        return y;
    }

    private int getBalance(AVLNode N) {
        if (N == null)
            return 0;
        return height(N.left) - height(N.right);
    }

    public void insert(int key) {
        this.root = insertRec(this.root, key);
    }

    private AVLNode insertRec(AVLNode node, int key) {
        if (node == null)
            return (new AVLNode(key));

        if (key < node.key)
            node.left = insertRec(node.left, key);
        else if (key > node.key)
            node.right = insertRec(node.right, key);
        else 
            return node;

        node.height = 1 + max(height(node.left), height(node.right));

        int balance = getBalance(node);

        if (balance > 1 && node.left != null && key < node.left.key)
            return rightRotate(node);

        if (balance < -1 && node.right != null && key > node.right.key)
            return leftRotate(node);

        if (balance > 1 && node.left != null && key > node.left.key) { 
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        if (balance < -1 && node.right != null && key < node.right.key) { 
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        return node;
    }

    private AVLNode minValueNode(AVLNode node) {
        AVLNode current = node;
        while (current.left != null)
            current = current.left;
        return current;
    }

    public void delete(int key) {
        this.root = deleteRec(this.root, key);
    }

    private AVLNode deleteRec(AVLNode root, int key) {
        if (root == null) return root;

        if (key < root.key)
            root.left = deleteRec(root.left, key);
        else if (key > root.key)
            root.right = deleteRec(root.right, key);
        else {
            if ((root.left == null) || (root.right == null)) {
                AVLNode temp = (root.left != null) ? root.left : root.right;
                if (temp == null) { 
                    root = null;
                } else { 
                    root = temp;
                }
            } else { 
                AVLNode temp = minValueNode(root.right);
                root.key = temp.key;
                root.right = deleteRec(root.right, temp.key);
            }
        }

        if (root == null) return root;

        root.height = max(height(root.left), height(root.right)) + 1;
        int balance = getBalance(root);

        if (balance > 1 && getBalance(root.left) >= 0)
            return rightRotate(root);

        if (balance > 1 && getBalance(root.left) < 0) {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }

        if (balance < -1 && getBalance(root.right) <= 0)
            return leftRotate(root);

        if (balance < -1 && getBalance(root.right) > 0) {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }
        return root;
    }

    public int countOccurrences(int key) {
        return searchRec(this.root, key) ? 1 : 0;
    }

    private boolean searchRec(AVLNode root, int key) {
        if (root == null) return false;
        if (root.key == key) return true;
        return (key < root.key) ? searchRec(root.left, key) : searchRec(root.right, key);
    }
    
    public void clear() {
        this.root = null;
    }
}


class RBNode {
    static final boolean RED = true;
    static final boolean BLACK = false;

    int key;
    boolean color;
    RBNode parent;
    RBNode left;
    RBNode right;

    RBNode(int key) {
        this.key = key;
        this.color = RED; 
        this.parent = null;
        this.left = null; 
        this.right = null;
    }
}

class RedBlackTree {
    private RBNode root;
    private final RBNode TNULL; 

    public RedBlackTree() {
        TNULL = new RBNode(0); 
        TNULL.color = RBNode.BLACK;
        TNULL.left = TNULL;
        TNULL.right = TNULL;
        TNULL.parent = TNULL; 
        root = TNULL;
    }

    private void leftRotate(RBNode x) {
        RBNode y = x.right;
        x.right = y.left;
        if (y.left != TNULL) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == TNULL) { 
            this.root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    private void rightRotate(RBNode x) {
        RBNode y = x.left;
        x.left = y.right;
        if (y.right != TNULL) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == TNULL) { 
            this.root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }
        y.right = x;
        x.parent = y;
    }

    public void insert(int key) {
        RBNode node = new RBNode(key);
        node.parent = TNULL; 
        node.left = TNULL;
        node.right = TNULL;
        node.color = RBNode.RED;

        RBNode y = TNULL;
        RBNode x = this.root;

        while (x != TNULL) {
            y = x;
            if (node.key < x.key) {
                x = x.left;
            } else if (node.key > x.key) {
                x = x.right;
            } else {
                return; 
            }
        }

        node.parent = y;
        if (y == TNULL) {
            root = node;
        } else if (node.key < y.key) {
            y.left = node;
        } else {
            y.right = node;
        }

        if (node.parent == TNULL) {
            node.color = RBNode.BLACK;
            return;
        }

        if (node.parent.parent == TNULL) { 
            return;
        }
        insertFixup(node);
    }

    private void insertFixup(RBNode k) {
        RBNode u;
        while (k.parent.color == RBNode.RED) {
            if (k.parent == k.parent.parent.right) {
                u = k.parent.parent.left; 
                if (u.color == RBNode.RED) {
                    u.color = RBNode.BLACK;
                    k.parent.color = RBNode.BLACK;
                    k.parent.parent.color = RBNode.RED;
                    k = k.parent.parent;
                } else { 
                    if (k == k.parent.left) { 
                        k = k.parent;
                        rightRotate(k);
                    }
                    k.parent.color = RBNode.BLACK;
                    k.parent.parent.color = RBNode.RED;
                    leftRotate(k.parent.parent);
                }
            } else { 
                u = k.parent.parent.right; 
                if (u.color == RBNode.RED) { 
                    u.color = RBNode.BLACK;
                    k.parent.color = RBNode.BLACK;
                    k.parent.parent.color = RBNode.RED;
                    k = k.parent.parent;
                } else {
                    if (k == k.parent.right) { 
                        k = k.parent;
                        leftRotate(k);
                    }
                    k.parent.color = RBNode.BLACK;
                    k.parent.parent.color = RBNode.RED;
                    rightRotate(k.parent.parent);
                }
            }
            if (k == root) {
                break;
            }
        }
        root.color = RBNode.BLACK;
    }
    
    private void rbTransplant(RBNode u, RBNode v) {
        if (u.parent == TNULL) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        v.parent = u.parent; 
    }

    private RBNode minimum(RBNode node) {
        while (node.left != TNULL) {
            node = node.left;
        }
        return node;
    }
    
    public void delete(int key) {
        deleteNodeHelper(this.root, key);
    }

    private void deleteNodeHelper(RBNode node, int key) {
        RBNode z = TNULL;
        RBNode x, y;

        RBNode current = node;
        while (current != TNULL) {
            if (current.key == key) {
                z = current;
                break;
            }
            current = (key < current.key) ? current.left : current.right;
        }

        if (z == TNULL) return; 

        y = z;
        boolean yOriginalColor = y.color;

        if (z.left == TNULL) {
            x = z.right;
            rbTransplant(z, z.right);
        } else if (z.right == TNULL) {
            x = z.left;
            rbTransplant(z, z.left);
        } else {
            y = minimum(z.right);
            yOriginalColor = y.color;
            x = y.right;
            if (y.parent == z) {
                x.parent = y;
            } else {
                rbTransplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            rbTransplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }
        if (yOriginalColor == RBNode.BLACK) {
            deleteFixup(x);
        }
    }
    
    private void deleteFixup(RBNode x) {
        RBNode s;
        while (x != root && x.color == RBNode.BLACK) {
            if (x == x.parent.left) { 
                s = x.parent.right;
                if (s.color == RBNode.RED) { 
                    s.color = RBNode.BLACK;
                    x.parent.color = RBNode.RED;
                    leftRotate(x.parent);
                    s = x.parent.right; 
                }
                if (s.left.color == RBNode.BLACK && s.right.color == RBNode.BLACK) { 
                    s.color = RBNode.RED;
                    x = x.parent; 
                } else {
                    if (s.right.color == RBNode.BLACK) { 
                        s.left.color = RBNode.BLACK;
                        s.color = RBNode.RED;
                        rightRotate(s);
                        s = x.parent.right; 
                    }
                    s.color = x.parent.color;
                    x.parent.color = RBNode.BLACK;
                    s.right.color = RBNode.BLACK;
                    leftRotate(x.parent);
                    x = root;  
                }
            } else { 
                s = x.parent.left;
                if (s.color == RBNode.RED) { 
                    s.color = RBNode.BLACK;
                    x.parent.color = RBNode.RED;
                    rightRotate(x.parent);
                    s = x.parent.left;
                }
                if (s.right.color == RBNode.BLACK && s.left.color == RBNode.BLACK) { 
                    s.color = RBNode.RED;
                    x = x.parent;
                } else {
                    if (s.left.color == RBNode.BLACK) { 
                        s.right.color = RBNode.BLACK;
                        s.color = RBNode.RED;
                        leftRotate(s);
                        s = x.parent.left;
                    }
                    s.color = x.parent.color;
                    x.parent.color = RBNode.BLACK;
                    s.left.color = RBNode.BLACK;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        x.color = RBNode.BLACK;
    }

    public int countOccurrences(int key) {
        return searchRec(this.root, key) != TNULL ? 1 : 0;
    }

    private RBNode searchRec(RBNode node, int key) {
        if (node == TNULL || key == node.key) {
            return node;
        }
        return (key < node.key) ? searchRec(node.left, key) : searchRec(node.right, key);
    }

    public void clear() {
        this.root = TNULL;
    }
}



public class TreeComparisonSingleFile {

    private static final int OPERATIONS_DATA_SIZE = 50_000;
    private static final int OPERATIONS_VALUE_MIN = -9999;
    private static final int OPERATIONS_VALUE_MAX = 9999;

    public static List<Integer> generateRandomNumbers(int size, int min, int max, boolean unique) {
        List<Integer> numbers = new ArrayList<>();
        Random random = new Random();
        if (unique && (max - min + 1) < size) {
            System.out.println("Não é possível gerar " + size + " números únicos no intervalo [" + min + "," + max + "]");
            for (int i = 0; i < size; i++) {
                numbers.add(random.nextInt(max - min + 1) + min);
            }
            return numbers;
        }

        if (unique) {
            List<Integer> range = new ArrayList<>();
            for (int i = min; i <= max; i++) {
                range.add(i);
            }
            Collections.shuffle(range, random);
            for (int i = 0; i < size; i++) {
                numbers.add(range.get(i));
            }
        } else {
            for (int i = 0; i < size; i++) {
                numbers.add(random.nextInt(max - min + 1) + min);
            }
        }
        return numbers;
    }
    
    public static List<Integer> loadInitialDataFromFile(String filePath) {
        List<Integer> numbers = new ArrayList<>();
        System.out.println("Lendo dados iniciais do arquivo: " + filePath + "...");
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(","); 
                
                for (String token : tokens) {
                    String trimmedToken = token.trim();
                    if (!trimmedToken.isEmpty()) {
                        try {
                            numbers.add(Integer.parseInt(trimmedToken));
                        } catch (NumberFormatException e) {
                            System.err.println("Ignorando token não numérico no arquivo de dados: \"" + trimmedToken + "\" na linha: \"" + line + "\" (" + e.getMessage() + ")");
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Erro: Arquivo não encontrado: " + filePath);
            System.err.println("Por favor, certifique-se de que o arquivo '" + filePath + "' exista no local correto.");
            System.err.println("O programa será encerrado.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Erro de I/O ao ler o arquivo: " + filePath + " (" + e.getMessage() + ")");
            System.err.println("O programa será encerrado.");
            System.exit(1); 
        }
        System.out.println(numbers.size() + " números lidos do arquivo.");
        if (numbers.isEmpty()) {
            System.err.println("Atenção: Nenhum número foi lido do arquivo. Verifique o conteúdo e o formato do arquivo.");
        }
        return numbers;
    }

    public static List<Integer> generateOperationNumbers() {
        System.out.println("Gerando " + OPERATIONS_DATA_SIZE + " números para operações (" + OPERATIONS_VALUE_MIN + " a " + OPERATIONS_VALUE_MAX + ")...");
        return generateRandomNumbers(OPERATIONS_DATA_SIZE, OPERATIONS_VALUE_MIN, OPERATIONS_VALUE_MAX, false);
    }

    public static void main(String[] args) {
        String filePath = "C:\\xampp\\htdocs\\Codes-vs\\IF\\Estrutura de dados\\JAVA\\Rubro_Negra_AVL\\src\\dados100_mil.txt"; 
        


        List<Integer> initialNumbers = loadInitialDataFromFile(filePath);
        
        if (initialNumbers.isEmpty()) {
            System.out.println("Encerrando o programa pois nenhum dado inicial foi carregado.");
            return; 
        }


        List<Integer> operationNumbers = generateOperationNumbers();

        long startTime, endTime;
        long avlInsertionTime, rbInsertionTime;
        long avlOperationTime, rbOperationTime;

        System.out.println("\n--- Comparativo de Árvores AVL e Rubro-Negra (Dados de Arquivo) ---");

        System.out.println("\nProcessando Árvore AVL...");
        AVLTree avlTree = new AVLTree();
        startTime = System.nanoTime();
        for (int number : initialNumbers) {
            avlTree.insert(number);
        }
        endTime = System.nanoTime();
        avlInsertionTime = (endTime - startTime) / 1_000_000;
        System.out.println("Tempo para preencher AVL com " + initialNumbers.size() + " números: " + avlInsertionTime + " ms");

        int avlInsertCount = 0, avlDeleteCount = 0, avlSearchCountOp = 0;
        long totalOccurrencesAvl = 0;
        startTime = System.nanoTime();
        for (int number : operationNumbers) {
            if (number % 3 == 0) { avlTree.insert(number); avlInsertCount++; }
            else if (number % 5 == 0) { avlTree.delete(number); avlDeleteCount++; }
            else { totalOccurrencesAvl += avlTree.countOccurrences(number); avlSearchCountOp++; }
        }
        endTime = System.nanoTime();
        avlOperationTime = (endTime - startTime) / 1_000_000;
        System.out.println("Tempo para " + OPERATIONS_DATA_SIZE + " operações na AVL: " + avlOperationTime + " ms");
        System.out.println("  Inserções: " + avlInsertCount + ", Remoções: " + avlDeleteCount + ", Buscas: " + avlSearchCountOp + " (Ocorrências: " + totalOccurrencesAvl + ")");
        
        avlTree.clear(); avlTree = null; System.gc();

        System.out.println("\nProcessando Árvore Rubro-Negra...");
        RedBlackTree rbTree = new RedBlackTree();
        startTime = System.nanoTime();
        for (int number : initialNumbers) {
            rbTree.insert(number);
        }
        endTime = System.nanoTime();
        rbInsertionTime = (endTime - startTime) / 1_000_000;
        System.out.println("Tempo para preencher Rubro-Negra com " + initialNumbers.size() + " números: " + rbInsertionTime + " ms");

        int rbInsertCount = 0, rbDeleteCount = 0, rbSearchCountOp = 0;
        long totalOccurrencesRb = 0;
        startTime = System.nanoTime();
        for (int number : operationNumbers) {
            if (number % 3 == 0) { rbTree.insert(number); rbInsertCount++; }
            else if (number % 5 == 0) { rbTree.delete(number); rbDeleteCount++; }
            else { totalOccurrencesRb += rbTree.countOccurrences(number); rbSearchCountOp++; }
        }
        endTime = System.nanoTime();
        rbOperationTime = (endTime - startTime) / 1_000_000;
        System.out.println("Tempo para " + OPERATIONS_DATA_SIZE + " operações na Rubro-Negra: " + rbOperationTime + " ms");
        System.out.println("  Inserções: " + rbInsertCount + ", Remoções: " + rbDeleteCount + ", Buscas: " + rbSearchCountOp + " (Ocorrências: " + totalOccurrencesRb + ")");

        rbTree.clear(); rbTree = null; System.gc();

        System.out.println("\n--- Resultados Comparativos ---");
        System.out.println("Tempo de Inserção Inicial (" + initialNumbers.size() + " números do arquivo):");
        System.out.println("  AVL: " + avlInsertionTime + " ms | Rubro-Negra: " + rbInsertionTime + " ms");
        System.out.println("\nTempo de Operações Mistas (" + OPERATIONS_DATA_SIZE + " operações):");
        System.out.println("  AVL: " + avlOperationTime + " ms | Rubro-Negra: " + rbOperationTime + " ms");
        
        System.out.println("\n--- Explicação e Comparativo (para o PDF) ---");
        printComparisonExplanation(avlInsertionTime, rbInsertionTime, avlOperationTime, rbOperationTime, initialNumbers.size());
    }

    public static void printComparisonExplanation(long avlInsertTime, long rbInsertTime, long avlOpTime, long rbOpTime, int initialDataSize) {
        System.out.println("\n**Comparativo de Desempenho: Árvore AVL vs. Árvore Rubro-Negra**\n");
        System.out.println("Este relatório compara o desempenho de Árvores AVL e Árvores Rubro-Negras em duas fases principais: inserção inicial de " + initialDataSize + " dados lidos de um arquivo e uma série de " + OPERATIONS_DATA_SIZE + " operações mistas (inserção, remoção e busca).\n");

        System.out.println("1. **Tempo de Inserção Inicial (" + initialDataSize + " elementos do arquivo):**");
        System.out.println("   - Árvore AVL: " + avlInsertTime + " ms");
        System.out.println("   - Árvore Rubro-Negra: " + rbInsertTime + " ms");
        if (avlInsertTime < rbInsertTime) {
            System.out.println("   *Observação:* A Árvore AVL foi " + (rbInsertTime - avlInsertTime) + " ms mais rápida na inserção inicial.");
        } else if (rbInsertTime < avlInsertTime) {
            System.out.println("   *Observação:* A Árvore Rubro-Negra foi " + (avlInsertTime - rbInsertTime) + " ms mais rápida na inserção inicial.");
        } else {
            System.out.println("   *Observação:* Ambas as árvores tiveram tempos de inserção inicial muito próximos.");
        }
        System.out.println();

        System.out.println("2. **Tempo de Operações Mistas (" + OPERATIONS_DATA_SIZE + " operações):**");
        System.out.println("   - Árvore AVL: " + avlOpTime + " ms");
        System.out.println("   - Árvore Rubro-Negra: " + rbOpTime + " ms");
        if (avlOpTime < rbOpTime) {
            System.out.println("   *Observação:* A Árvore AVL foi " + (rbOpTime - avlOpTime) + " ms mais rápida nas operações mistas.");
        } else if (rbOpTime < avlOpTime) {
            System.out.println("   *Observação:* A Árvore Rubro-Negra foi " + (avlOpTime - rbOpTime) + " ms mais rápida nas operações mistas.");
        } else {
            System.out.println("   *Observação:* Ambas as árvores tiveram tempos de operações mistas muito próximos.");
        }
        System.out.println();

        System.out.println("3. **Análise Teórica e Prática:**\n");
        System.out.println("   **Árvores AVL:**");
        System.out.println("   - **Balanceamento:** Estritamente balanceadas. Fator de balanceamento de qualquer nó é no máximo 1.");
        System.out.println("   - **Busca:** Geralmente mais rápidas devido ao balanceamento rigoroso (altura ~1.0 * log2(N)).");
        System.out.println("   - **Inserção/Remoção:** Podem exigir mais rotações (até O(log N), tipicamente 1-2), tornando-as potencialmente mais lentas que Rubro-Negras para estas operações.\n");

        System.out.println("   **Árvores Rubro-Negras:**");
        System.out.println("   - **Balanceamento:** Menos rígido (altura <= 2 * log2(N+1)). Usam cores (Vermelho/Preto).");
        System.out.println("   - **Busca:** Podem ser ligeiramente mais lentas que AVLs no pior caso.");
        System.out.println("   - **Inserção/Remoção:** Menos rotações em média (máx 2 para inserção, 3 para remoção), podendo ser mais rápidas para estas operações.\n");
        
        System.out.println("   **Observações dos Resultados Empíricos:**");
        System.out.println("   - Os resultados (" + avlInsertTime + "ms vs " + rbInsertTime + "ms para inserção; " + avlOpTime + "ms vs " + rbOpTime + "ms para ops) podem variar dependendo da natureza dos dados, da implementação específica e do ambiente de execução.");
        System.out.println("   - Frequentemente, Rubro-Negras são melhores para inserções/deleções intensivas; AVLs para buscas predominantes.");
        System.out.println("   - Rubro-Negras são mais complexas de implementar.\n");

        System.out.println("4. **Conclusão:**");
        System.out.println("   - **AVL:** Prioridade em buscas rápidas e consistentes, inserções/remoções menos frequentes.");
        System.out.println("   - **Rubro-Negra:** Inserções/remoções frequentes, desempenho de busca O(log N) aceitável. (Padrão em muitas bibliotecas: `TreeMap` Java, `std::map` C++).");
        System.out.println("   - Diferença prática pode não ser drástica; complexidade de implementação também é um fator.");
    }
}
