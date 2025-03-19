import java.util.Stack;

public class Editor {
    private Stack<Character> texto;
    private Stack<Character> historicoDesfazer;  
    private Stack<Character> historicoRefazer;

    public Editor() {
        this.texto = new Stack<Character>();
        this.historicoDesfazer = new Stack<Character>();
        this.historicoRefazer = new Stack<Character>();
    }

    public void inserirTexto(String input_texto){
        for (char letra : input_texto.toCharArray()) {
            this.texto.push(letra);
            this.historicoDesfazer.push(letra);
        }
    }
    public void removerTexto(int quantidade){
        for(int i = 0; i < quantidade; i++){
            this.historicoRefazer.push(this.texto.peek());
            this.historicoRefazer.clear();
            this.historicoDesfazer.clear();
            this.texto.pop();
        }
    }
    public void desfazerTexto(){
        while(!this.historicoDesfazer.isEmpty()){
            this.texto.push(this.historicoDesfazer.pop());
        }
    }
    public String pilhaParaTexto(Stack<Character> stack){
        StringBuilder string = new StringBuilder();
        for(Character letra:stack){
            string.append(letra);
        }
        return string.toString();
    }
    public void exibir(){
        String t = this.pilhaParaTexto(this.texto);
        System.out.println("Texto: "+t);
    }
    
}