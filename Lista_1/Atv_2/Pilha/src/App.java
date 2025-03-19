public class App {
    public static void main(String[] args) {
        Editor editor = new Editor();

        editor.inserirTexto("Hello,");
        editor.inserirTexto(" World!"); 
        editor.exibir();
        editor.removerTexto(7);
        editor.exibir();
        editor.desfazerTexto();
        editor.exibir();
    }
}
