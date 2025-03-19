import java.util.ArrayList;

public class App {    
    public static void main(String[] args) {
        Trabalho trabalho1 = new Trabalho(1,"trabalho Metalurgia",120);
        Trabalho trabalho2 = new Trabalho(2,"trabalho Fisica",246);
        Trabalho trabalho3 = new Trabalho(3,"trabalho Ciencia",8984);
        Trabalho trabalho4 = new Trabalho(4,"trabalho Matematica",18468);
        
        Impressao fila_Impressao = new Impressao();

        fila_Impressao.adicionarNaLista(trabalho1);
        fila_Impressao.adicionarNaLista(trabalho2);
        fila_Impressao.adicionarNaLista(trabalho3);
        fila_Impressao.adicionarNaLista(trabalho4);
        trabalho1.setNome_arquivo("Trabalho de Biologia");
        fila_Impressao.adicionarNaLista(trabalho1);
        fila_Impressao.exibirFila();
        fila_Impressao.removerDaLista();
        fila_Impressao.exibirFila();
        Trabalho trabalho5 = new Trabalho(5,"trabalho Matematica(2)",18468);
        fila_Impressao.adicionarNaLista(trabalho5);

    }
}
