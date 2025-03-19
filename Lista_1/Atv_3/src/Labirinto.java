public class Labirinto {
    public static void main(String[] args) {
        char[][] labirinto = {
            {'#', '#', '#', '#', '#'},
            {'#', '.', '.', '.', '#'},
            {'#', '#', 'S', '.', '#'},
            {'#', 'E', '.', '.', '#'},
            {'#', '#', '#', '#', '#'}
        };

        if (resolverLabirinto(labirinto)) {
            imprimirLabirinto(labirinto);
        } else {
            System.out.println("Labirinto sem saída.");
        }
    }

    public static boolean resolverLabirinto(char[][] labirinto) {
        int linhas = labirinto.length;
        int colunas = labirinto[0].length;
        int[] entrada = encontrarPosicao(labirinto, 'E');
        int[] saida = encontrarPosicao(labirinto, 'S');
        
        if (entrada == null || saida == null) {
            return false;
        }
        
        return explorar(labirinto, entrada[0], entrada[1], saida[0], saida[1]);
    }

    public static boolean explorar(char[][] labirinto, int x, int y, int sx, int sy) {
        if (x < 0 || x >= labirinto.length || y < 0 || y >= labirinto[0].length || labirinto[x][y] == '#' || labirinto[x][y] == '*') {
            return false;
        }
        
        if (x == sx && y == sy) {
            return true;
        }
        
        char temp = labirinto[x][y];
        if (temp != 'E') {
            labirinto[x][y] = '*';
        }
        
        if (explorar(labirinto, x - 1, y, sx, sy) ||
            explorar(labirinto, x + 1, y, sx, sy) ||
            explorar(labirinto, x, y - 1, sx, sy) ||
            explorar(labirinto, x, y + 1, sx, sy)) {
            return true;
        }
        
        if (temp != 'E') {
            labirinto[x][y] = '.';
        }
        return false;
    }

    public static int[] encontrarPosicao(char[][] labirinto, char simbolo) {
        for (int i = 0; i < labirinto.length; i++) {
            for (int j = 0; j < labirinto[i].length; j++) {
                if (labirinto[i][j] == simbolo) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    public static void imprimirLabirinto(char[][] labirinto) {
        for (char[] linha : labirinto) {
            for (char c : linha) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }
}
