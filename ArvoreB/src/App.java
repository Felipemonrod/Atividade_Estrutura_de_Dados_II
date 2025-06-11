import java.util.Scanner;

// Classe para representar um Livro
class Livro {
    String titulo;
    String autor;
    int isbn;

    public Livro(String t, String a, int i) {
        this.titulo = t;
        this.autor = a;
        this.isbn = i;
    }

    public Livro() {
        this.titulo = "";
        this.autor = "";
        this.isbn = 0;
    }
}

// Classe para representar um nó da Árvore B (ordem 5, 4 chaves por nó)
class NoArvoreB {
    Livro[] chaves = new Livro[4];
    NoArvoreB[] filhos = new NoArvoreB[5];
    int numChaves = 0;
    boolean ehFolha = true;

    public NoArvoreB() {
        for (int i = 0; i < 4; i++)
            chaves[i] = null;
        for (int i = 0; i < 5; i++)
            filhos[i] = null;
    }

    public boolean estaCheio() {
        return numChaves == 4;
    }

    // Insere um livro em um nó folha, ordenando por título
    public void inserirChave(Livro livro) {
        int i = numChaves - 1;
        while (i >= 0 && chaves[i].titulo.compareToIgnoreCase(livro.titulo) > 0) {
            chaves[i + 1] = chaves[i];
            i--;
        }
        chaves[i + 1] = livro;
        numChaves++;
    }

    // Busca um livro por ISBN neste nó
    public Livro buscarNoPorIsbn(int isbn) {
        for (int i = 0; i < numChaves; i++) {
            if (chaves[i].isbn == isbn)
                return chaves[i];
        }
        return null;
    }
}

// Classe principal da Árvore B
class ArvoreB {
    NoArvoreB raiz = new NoArvoreB();

    // Divide um filho cheio
    private void dividirFilho(NoArvoreB noPai, int indice) {
        NoArvoreB filhoCheio = noPai.filhos[indice];
        NoArvoreB novoNo = new NoArvoreB();
        novoNo.ehFolha = filhoCheio.ehFolha;
        novoNo.numChaves = 2;

        // Move as últimas 2 chaves para o novo nó
        for (int i = 0; i < 2; i++) {
            novoNo.chaves[i] = filhoCheio.chaves[i + 2];
            filhoCheio.chaves[i + 2] = null;
        }

        // Se não é folha, move os filhos também
        if (!filhoCheio.ehFolha) {
            for (int i = 0; i < 3; i++) {
                novoNo.filhos[i] = filhoCheio.filhos[i + 2];
                filhoCheio.filhos[i + 2] = null;
            }
        }

        filhoCheio.numChaves = 2;

        // Move os filhos do nó pai para abrir espaço
        for (int i = noPai.numChaves; i >= indice + 1; i--) {
            noPai.filhos[i + 1] = noPai.filhos[i];
        }
        noPai.filhos[indice + 1] = novoNo;

        // Move as chaves do nó pai para abrir espaço
        for (int i = noPai.numChaves - 1; i >= indice; i--) {
            noPai.chaves[i + 1] = noPai.chaves[i];
        }
        // Promove a chave do meio
        noPai.chaves[indice] = filhoCheio.chaves[2];
        filhoCheio.chaves[2] = null;
        noPai.numChaves++;
    }

    // Inserção pública
    public void inserir(Livro livro) {
        if (raiz.estaCheio()) {
            NoArvoreB novaRaiz = new NoArvoreB();
            novaRaiz.ehFolha = false;
            novaRaiz.filhos[0] = raiz;
            dividirFilho(novaRaiz, 0);
            raiz = novaRaiz;
        }
        inserirNaoCheio(raiz, livro);
    }

    // Inserção auxiliar
    private void inserirNaoCheio(NoArvoreB no, Livro livro) {
        int i = no.numChaves - 1;
        if (no.ehFolha) {
            no.inserirChave(livro);
        } else {
            // Corrigido: só compara se no.chaves[i] != null
            while (i >= 0 && no.chaves[i] != null && livro.titulo.compareToIgnoreCase(no.chaves[i].titulo) < 0) {
                i--;
            }
            i++;
            if (no.filhos[i].estaCheio()) {
                dividirFilho(no, i);
                if (no.chaves[i] != null && livro.titulo.compareToIgnoreCase(no.chaves[i].titulo) > 0) {
                    i++;
                }
            }
            inserirNaoCheio(no.filhos[i], livro);
        }
    }

    // Busca pública por ISBN
    public Livro buscar(int isbn) {
        return buscarNo(raiz, isbn);
    }

    // Busca auxiliar por ISBN
    private Livro buscarNo(NoArvoreB no, int isbn) {
        Livro encontrado = no.buscarNoPorIsbn(isbn);
        if (encontrado != null)
            return encontrado;
        if (no.ehFolha)
            return null;
        for (int i = 0; i <= no.numChaves; i++) {
            encontrado = buscarNo(no.filhos[i], isbn);
            if (encontrado != null)
                return encontrado;
        }
        return null;
    }

    // Exibe livros em ordem alfabética de título
    public void exibirEmOrdem() {
        System.out.println("--- Livros em Ordem Alfabética (por Título) ---");
        exibirEmOrdemNo(raiz);
        System.out.println();
    }

    private void exibirEmOrdemNo(NoArvoreB no) {
        int i;
        for (i = 0; i < no.numChaves; i++) {
            if (!no.ehFolha)
                exibirEmOrdemNo(no.filhos[i]);
            Livro l = no.chaves[i];
            if (l != null)
                System.out.println("  Título: " + l.titulo + ", Autor: " + l.autor + ", ISBN: " + l.isbn);
        }
        if (!no.ehFolha)
            exibirEmOrdemNo(no.filhos[i]);
    }

    // Exibe a estrutura da árvore
    public void exibirEstrutura() {
        System.out.println("--- Estrutura da Árvore B ---");
        exibirEstruturaNo(raiz, 0);
    }

    private void exibirEstruturaNo(NoArvoreB no, int nivel) {
        for (int i = 0; i < nivel; i++)
            System.out.print("  ");
        System.out.print("[");
        for (int i = 0; i < no.numChaves; i++) {
            if (no.chaves[i] != null) {
                System.out.print(no.chaves[i].titulo);
                if (i < no.numChaves - 1) System.out.print(", ");
            }
        }
        System.out.println("]");
        if (!no.ehFolha) {
            for (int i = 0; i <= no.numChaves; i++) {
                exibirEstruturaNo(no.filhos[i], nivel + 1);
            }
        }
    }
}

// Programa principal
public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArvoreB arvore = new ArvoreB();

        // Inserção automática dos 13 livros para teste de balanceamento
        arvore.inserir(new Livro("O Senhor dos Anéis", "J.R.R. Tolkien", 101));
        arvore.inserir(new Livro("A Fundação", "Isaac Asimov", 202));
        arvore.inserir(new Livro("Duna", "Frank Herbert", 303));
        arvore.inserir(new Livro("1984", "George Orwell", 404));
        arvore.inserir(new Livro("Cem Anos de Solidão", "Gabriel García Márquez", 505));
        arvore.inserir(new Livro("O Guia do Mochileiro das Galáxias", "Douglas Adams", 606));
        arvore.inserir(new Livro("Fahrenheit 451", "Ray Bradbury", 707));
        arvore.inserir(new Livro("O Apanhador no Campo de Centeio", "J.D. Salinger", 808));
        arvore.inserir(new Livro("Moby Dick", "Herman Melville", 909));
        arvore.inserir(new Livro("Guerra e Paz", "Liev Tolstói", 111));
        arvore.inserir(new Livro("A Revolução dos Bichos", "George Orwell", 222));
        arvore.inserir(new Livro("Ulisses", "James Joyce", 333));
        arvore.inserir(new Livro("Crime e Castigo", "Fiódor Dostoiévski", 444));

        int opcao;
        do {
            System.out.println("\n=== SISTEMA DE BIBLIOTECA COM ÁRVORE B ===");
            System.out.println("1. Inserir Livro");
            System.out.println("2. Buscar Livro por ISBN");
            System.out.println("3. Listar Livros (Ordem por Título)");
            System.out.println("4. Exibir Estrutura da Árvore");
            System.out.println("5. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = Integer.parseInt(sc.nextLine());

            switch (opcao) {
                case 1:
                    System.out.print("Digite o Título: ");
                    String titulo = sc.nextLine();
                    System.out.print("Digite o Autor: ");
                    String autor = sc.nextLine();
                    System.out.print("Digite o ISBN: ");
                    int isbn = Integer.parseInt(sc.nextLine());
                    Livro novoLivro = new Livro(titulo, autor, isbn);
                    arvore.inserir(novoLivro);
                    System.out.println("Livro '" + titulo + "' inserido com sucesso!");
                    break;
                case 2:
                    System.out.print("Digite o ISBN do livro a ser buscado: ");
                    int isbnBusca = Integer.parseInt(sc.nextLine());
                    Livro livroEncontrado = arvore.buscar(isbnBusca);
                    if (livroEncontrado != null) {
                        System.out.println("Livro Encontrado!");
                        System.out.println("  Título: " + livroEncontrado.titulo);
                        System.out.println("  Autor: " + livroEncontrado.autor);
                        System.out.println("  ISBN: " + livroEncontrado.isbn);
                    } else {
                        System.out.println("Livro com ISBN " + isbnBusca + " não encontrado na árvore!");
                    }
                    break;
                case 3:
                    arvore.exibirEmOrdem();
                    break;
                case 4:
                    arvore.exibirEstrutura();
                    break;
                case 5:
                    System.out.println("Encerrando programa...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        } while (opcao != 5);
        sc.close();
    }
}