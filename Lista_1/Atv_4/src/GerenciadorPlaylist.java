import java.util.Scanner;

class Musica {
    String titulo;
    String artista;
    Musica proxima;

    public Musica(String titulo, String artista) {
        this.titulo = titulo;
        this.artista = artista;
        this.proxima = null;
    }
}

class Playlist {
    private Musica primeira;

    public Playlist() {
        this.primeira = null;
    }

    public void adicionarMusica(String titulo, String artista) {
        Musica novaMusica = new Musica(titulo, artista);
        if (primeira == null) {
            primeira = novaMusica;
        } else {
            Musica atual = primeira;
            while (atual.proxima != null) {
                atual = atual.proxima;
            }
            atual.proxima = novaMusica;
        }
        System.out.println("Música adicionada com sucesso!");
    }

    public void removerMusica(String titulo) {
        if (primeira == null) {
            System.out.println("A playlist está vazia.");
            return;
        }

        if (primeira.titulo.equalsIgnoreCase(titulo)) {
            primeira = primeira.proxima;
            System.out.println("Música removida com sucesso!");
            return;
        }

        Musica atual = primeira;
        while (atual.proxima != null && !atual.proxima.titulo.equalsIgnoreCase(titulo)) {
            atual = atual.proxima;
        }

        if (atual.proxima == null) {
            System.out.println("Música não encontrada.");
        } else {
            atual.proxima = atual.proxima.proxima;
            System.out.println("Música removida com sucesso!");
        }
    }

    public void buscarMusica(String titulo) {
        Musica atual = primeira;
        while (atual != null) {
            if (atual.titulo.equalsIgnoreCase(titulo)) {
                System.out.println("Música encontrada: " + atual.titulo + " - " + atual.artista);
                return;
            }
            atual = atual.proxima;
        }
        System.out.println("Música não encontrada.");
    }

    public void listarMusicas() {
        if (primeira == null) {
            System.out.println("A playlist está vazia.");
            return;
        }
        Musica atual = primeira;
        System.out.println("Playlist:");
        while (atual != null) {
            System.out.println("- " + atual.titulo + " - " + atual.artista);
            atual = atual.proxima;
        }
    }
}

public class GerenciadorPlaylist {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Playlist playlist = new Playlist();
        int opcao;

        do {
            System.out.println("\nMenu:");
            System.out.println("1. Adicionar música");
            System.out.println("2. Remover música");
            System.out.println("3. Buscar música");
            System.out.println("4. Listar músicas");
            System.out.println("5. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    System.out.print("Título da música: ");
                    String titulo = scanner.nextLine();
                    System.out.print("Artista: ");
                    String artista = scanner.nextLine();
                    playlist.adicionarMusica(titulo, artista);
                    break;
                case 2:
                    System.out.print("Título da música a remover: ");
                    titulo = scanner.nextLine();
                    playlist.removerMusica(titulo);
                    break;
                case 3:
                    System.out.print("Título da música a buscar: ");
                    titulo = scanner.nextLine();
                    playlist.buscarMusica(titulo);
                    break;
                case 4:
                    playlist.listarMusicas();
                    break;
                case 5:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 5);

        scanner.close();
    }
}
