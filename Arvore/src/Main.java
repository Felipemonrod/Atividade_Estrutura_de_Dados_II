import java.util.*;

class AVLTree {

    class Node {
        int key, height;
        Node left, right;

        Node(int d) {
            key = d;
            height = 1;
        }

        int balanceFactor() {
            return height(left) - height(right);
        }
    }

    private Node root;

    // ALTURA
    private int height(Node N) {
        return (N == null) ? 0 : N.height;
    }

    // ROTACOES
    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        // Rotação
        x.right = y;
        y.left = T2;

        // Atualizar alturas
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        // Rotação
        y.left = x;
        x.right = T2;

        // Atualizar alturas
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    private int getBalance(Node N) {
        return (N == null) ? 0 : height(N.left) - height(N.right);
    }

    // INSERÇÃO
    private Node insert(Node node, int key) {
        if (node == null) return new Node(key);

        if (key < node.key)
            node.left = insert(node.left, key);
        else if (key > node.key)
            node.right = insert(node.right, key);
        else // duplicado
            return node;

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        // Casos de desbalanceamento
        if (balance > 1 && key < node.left.key)
            return rightRotate(node);
        if (balance < -1 && key > node.right.key)
            return leftRotate(node);
        if (balance > 1 && key > node.left.key) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        if (balance < -1 && key < node.right.key) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public void insert(int key) {
        root = insert(root, key);
    }

    // REMOÇÃO
    private Node minValueNode(Node node) {
        Node current = node;
        while (current.left != null)
            current = current.left;
        return current;
    }

    private Node delete(Node root, int key) {
        if (root == null) return root;

        if (key < root.key)
            root.left = delete(root.left, key);
        else if (key > root.key)
            root.right = delete(root.right, key);
        else {
            if ((root.left == null) || (root.right == null)) {
                Node temp = (root.left != null) ? root.left : root.right;
                if (temp == null) {
                    temp = root;
                    root = null;
                } else root = temp;
            } else {
                Node temp = minValueNode(root.right);
                root.key = temp.key;
                root.right = delete(root.right, temp.key);
            }
        }

        if (root == null)
            return root;

        root.height = Math.max(height(root.left), height(root.right)) + 1;

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

    public void delete(int key) {
        root = delete(root, key);
    }

    // IMPRESSÃO EM ORDEM
    public void printInOrder() {
        System.out.println("Valor\tFator Balanceamento");
        printInOrder(root);
    }

    private void printInOrder(Node node) {
        if (node != null) {
            printInOrder(node.left);
            System.out.println(node.key + "\t" + getBalance(node));
            printInOrder(node.right);
        }
    }

    // VERIFICAÇÃO AVL
    public boolean isAVL() {
        return isAVL(root);
    }

    private boolean isAVL(Node node) {
        if (node == null) return true;
        int balance = getBalance(node);
        if (Math.abs(balance) > 1) return false;
        return isAVL(node.left) && isAVL(node.right);
    }
}

public class Main {
    public static void main(String[] args) {
        AVLTree tree = new AVLTree();
        Random rand = new Random();

        Set<Integer> uniqueNumbers = new HashSet<>();
        while (uniqueNumbers.size() < 100) {
            uniqueNumbers.add(rand.nextInt(1001) - 500); // de -500 a 500
        }

        System.out.println("Inserindo 100 números na árvore AVL...");
        for (int num : uniqueNumbers) {
            tree.insert(num);
        }

        System.out.println("\nVerificando se a árvore é AVL após inserções: " + (tree.isAVL() ? "SIM" : "NÃO"));
        tree.printInOrder();

        // Remover 20 números aleatórios da lista original
        List<Integer> list = new ArrayList<>(uniqueNumbers);
        Collections.shuffle(list);
        List<Integer> toRemove = list.subList(0, 20);

        System.out.println("\nRemovendo 20 números...");
        for (int num : toRemove) {
            tree.delete(num);
        }

        System.out.println("\nVerificando se a árvore é AVL após remoções: " + (tree.isAVL() ? "SIM" : "NÃO"));
        tree.printInOrder();
    }
}
