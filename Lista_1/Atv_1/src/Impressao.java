import java.util.ArrayList;

public class Impressao {
    ArrayList<Trabalho> trabalhos;

    public Impressao() {
        this.trabalhos = new ArrayList<>();
    }

    public void adicionarNaLista(Trabalho trabalho){ //enqueue
        this.trabalhos.add(trabalho);
        System.out.println("Trabalho adicionado");
    }

    public Trabalho removerDaLista(){ //dequeue
        if (this.isEmpty()) {
            return null;
        }else{
            this.trabalhos.removeFirst();
            System.out.println("Trabalho removido");
            return this.trabalhos.getFirst();
        }
    }
    public boolean isEmpty(){
        if (trabalhos.isEmpty()) {
            System.out.println("Fila vazia");
            return true;
        }
        return false;
    }
    public void exibirFila(){
        for (int i = 0; i < this.trabalhos.size(); i++) {
            trabalhos.get(i).showAll();
        }
    }

}