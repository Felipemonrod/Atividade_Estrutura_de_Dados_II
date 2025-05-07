import java.util.*;

class Node {
    int value;
    Node left, right;

    Node(int item) {
        value = item;
        left = right = null;
    }
}

class BinaryTree {
    Node root;

    void insert(int value) {
        root = insertRec(root, value);
    }

    Node insertRec(Node root, int value) {
        if (root == null) {
            root = new Node(value);
            return root;
        }

        if (value < root.value)
            root.left = insertRec(root.left, value);
        else if (value > root.value)
            root.right = insertRec(root.right, value);

        return root;
    }

    Node deleteRec(Node root, int value) {
        if (root == null)
            return root;

        if (value < root.value)
            root.left = deleteRec(root.left, value);
        else if (value > root.value)
            root.right = deleteRec(root.right, value);
        else {
            if (root.left == null)
                return root.right;
            else if (root.right == null)
                return root.left;

            root.value = minValue(root.right);
            root.right = deleteRec(root.right, root.value);
        }

        return root;
    }

    int minValue(Node root) {
        int min = root.value;
        while (root.left != null) {
            min = root.left.value;
            root = root.left;
        }
        return min;
    }

    void delete(int value) {
        root = deleteRec(root, value);
    }

    void preOrder(Node node) {
        if (node == null) return;
        System.out.print(node.value + " ");
        preOrder(node.left);
        preOrder(node.right);
    }

    void inOrder(Node node) {
        if (node == null) return;
        inOrder(node.left);
        System.out.print(node.value + " ");
        inOrder(node.right);
    }

    void postOrder(Node node) {
        if (node == null) return;
        postOrder(node.left);
        postOrder(node.right);
        System.out.print(node.value + " ");
    }

    void levelOrder() {
        if (root == null) return;
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            System.out.print(node.value + " ");
            if (node.left != null) queue.add(node.left);
            if (node.right != null) queue.add(node.right);
        }
    }
}

public class ArvoreBinaria {
    public static void main(String[] args) {
        BinaryTree tree = new BinaryTree();
        Random rand = new Random();
        Set<Integer> numeros = new LinkedHashSet<>();

        while (numeros.size() < 20) {
            numeros.add(rand.nextInt(101)); 
        }

        System.out.println("Números sorteados: " + numeros);
        for (int num : numeros) {
            tree.insert(num);
        }
        System.out.print("\nPré-Ordem: ");
        tree.preOrder(tree.root);
        System.out.print("\nIn-Ordem: ");
        tree.inOrder(tree.root);
        System.out.print("\nPós-Ordem: ");
        tree.postOrder(tree.root);
        System.out.print("\nEm Nível: ");
        tree.levelOrder();
        // Remove 5 elementos
        List<Integer> listaNumeros = new ArrayList<>(numeros);
        Collections.shuffle(listaNumeros);
        List<Integer> removidos = listaNumeros.subList(0, 5);
        for (int num : removidos) {
            tree.delete(num);
        }
        System.out.println("\n\nRemovidos: " + removidos);
        System.out.print("\nPré-Ordem após remoções: ");
        tree.preOrder(tree.root);
        System.out.print("\nIn-Ordem após remoções: ");
        tree.inOrder(tree.root);
        System.out.print("\nPós-Ordem após remoções: ");
        tree.postOrder(tree.root);
        System.out.print("\nEm Nível após remoções: ");
        tree.levelOrder();
    }
}
