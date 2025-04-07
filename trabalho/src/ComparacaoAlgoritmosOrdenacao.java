import java.io.*;
import java.util.*;

public class ComparacaoAlgoritmosOrdenacao {
    private static long comparacoes;
    private static long trocas;

    // Classe para armazenar os resultados de cada algoritmo
    static class ResultadoAlgoritmo {
        String nome;
        String tempo;
        long comparacoes;
        long trocas;
        long tempoEmMillis;

        public ResultadoAlgoritmo(String nome, String tempo, long comparacoes, long trocas, long tempoEmMillis) {
            this.nome = nome;
            this.tempo = tempo;
            this.comparacoes = comparacoes;
            this.trocas = trocas;
            this.tempoEmMillis = tempoEmMillis;
        }
    }

    public static void main(String[] args) {
        int[] numeros = lerArquivo("src/dados100_mil.txt");
        
        // Lista para armazenar os resultados
        List<ResultadoAlgoritmo> resultados = new ArrayList<>();
        
        // Armazenar os resultados de cada metodo
        resultados.add(testarAlgoritmo("BubbleSort", numeros.clone()));
        resultados.add(testarAlgoritmo("SelectionSort", numeros.clone()));
        resultados.add(testarAlgoritmo("InsertionSort", numeros.clone()));
        resultados.add(testarAlgoritmo("MergeSort", numeros.clone()));
        resultados.add(testarAlgoritmo("QuickSort", numeros.clone()));
        
        // Ordenar os resultados por tempo (decrescente)
        resultados.sort((r1, r2) -> Long.compare(r2.tempoEmMillis, r1.tempoEmMillis));
        
        // Exibir os resultados ordenados
        for (ResultadoAlgoritmo resultado : resultados) {
            System.out.println("Algoritmo: " + resultado.nome);
            System.out.println("Tempo: " + resultado.tempo);
            System.out.println("Comparações: " + resultado.comparacoes);
            System.out.println("Trocas: " + resultado.trocas);
            System.out.println("-----------------------------");
        }
    }

    // Método para retornar um Resultado do Algoritmo
    private static ResultadoAlgoritmo testarAlgoritmo(String nomeAlgoritmo, int[] array) {
        comparacoes = 0;
        trocas = 0;
        
        long inicio = System.currentTimeMillis();
        
        switch(nomeAlgoritmo) {
            case "BubbleSort":
                bubbleSort(array);
                break;
            case "SelectionSort":
                selectionSort(array);
                break;
            case "InsertionSort":
                insertionSort(array);
                break;
            case "MergeSort":
                mergeSort(array, 0, array.length - 1);
                break;
            case "QuickSort":
                quickSort(array, 0, array.length - 1);
                break;
        }
        
        long fim = System.currentTimeMillis();
        long tempo = fim - inicio;
        String tempoFormatado = formatarTempo(tempo);
        
        gerarArquivoSaida(nomeAlgoritmo, array, tempoFormatado);
        
        return new ResultadoAlgoritmo(nomeAlgoritmo, tempoFormatado, comparacoes, trocas, tempo);
    }

    @SuppressWarnings("unused")
    private static void ordenarSaida(int[] array) {
        Arrays.sort(array);
        for (int i = 0; i < array.length / 2; i++) {
            int temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }

    // Metodos de ordenação 
    private static void bubbleSort(int[] array) {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                comparacoes++;
                if (array[j] > array[j + 1]) {
                    trocas++;
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }

    private static void selectionSort(int[] array) {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            int min_idx = i;
            for (int j = i + 1; j < n; j++) {
                comparacoes++;
                if (array[j] < array[min_idx]) {
                    min_idx = j;
                }
            }
            trocas++;
            int temp = array[min_idx];
            array[min_idx] = array[i];
            array[i] = temp;
        }
    }

    private static void insertionSort(int[] array) {
        int n = array.length;
        for (int i = 1; i < n; i++) {
            int key = array[i];
            int j = i - 1;
            while (j >= 0 && array[j] > key) {
                comparacoes++;
                trocas++;
                array[j + 1] = array[j];
                j = j - 1;
            }
            trocas++;
            array[j + 1] = key;
        }
    }

    private static void mergeSort(int[] array, int inicio, int fim) {
        if (inicio < fim) {
            int meio = (inicio + fim) / 2;
            mergeSort(array, inicio, meio);
            mergeSort(array, meio + 1, fim);
            merge(array, inicio, meio, fim);
        }
    }

    private static void merge(int[] array, int inicio, int meio, int fim) {
        int n1 = meio - inicio + 1;
        int n2 = fim - meio;

        int[] esquerda = new int[n1];
        int[] direita = new int[n2];

        for (int i = 0; i < n1; i++) {
            esquerda[i] = array[inicio + i];
        }
        for (int j = 0; j < n2; j++) {
            direita[j] = array[meio + 1 + j];
        }

        int i = 0, j = 0;
        int k = inicio;

        while (i < n1 && j < n2) {
            comparacoes++;
            if (esquerda[i] <= direita[j]) {
                array[k] = esquerda[i];
                i++;
            } else {
                array[k] = direita[j];
                j++;
            }
            trocas++;
            k++;
        }

        while (i < n1) {
            array[k] = esquerda[i];
            i++;
            k++;
            trocas++;
        }

        while (j < n2) {
            array[k] = direita[j];
            j++;
            k++;
            trocas++;
        }
    }

    private static void quickSort(int[] array, int inicio, int fim) {
        if (inicio < fim) {
            int pivo = particionar(array, inicio, fim);
            quickSort(array, inicio, pivo - 1);
            quickSort(array, pivo + 1, fim);
        }
    }

    private static int particionar(int[] array, int inicio, int fim) {
        int pivo = array[fim];
        int i = inicio - 1;

        for (int j = inicio; j < fim; j++) {
            comparacoes++;
            if (array[j] <= pivo) {
                i++;
                trocas++;
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }

        trocas++;
        int temp = array[i + 1];
        array[i + 1] = array[fim];
        array[fim] = temp;

        return i + 1;
    }

    private static String formatarTempo(long millis) {
        long horas = millis / 3600000;
        long minutos = (millis % 3600000) / 60000;
        long segundos = (millis % 60000) / 1000;
        long milissegundos = millis % 1000;
        
        return String.format("%02d:%02d:%02d:%03d", horas, minutos, segundos, milissegundos);
    }

    private static void gerarArquivoSaida(String algoritmo, int[] array, String tempo) {
        String pastaResultados = "resultados";
        File pasta = new File(pastaResultados);
    
        if (!pasta.exists()) {
            pasta.mkdir();
        }
    
        String nomeArquivo = pastaResultados + File.separator + "ordenado_" + algoritmo + ".txt";
    
        try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
            // Ordenar o array em ordem decrescente antes de escrever no arquivo
            Arrays.sort(array);
            // Inverter o array para ordem decrescente
            for (int i = 0; i < array.length / 2; i++) {
                int temp = array[i];
                array[i] = array[array.length - 1 - i];
                array[array.length - 1 - i] = temp;
            }
    
            writer.println("Dupla: [FERNANDO ALVES DE SOUZA, FELIPE MONTALVÃO]");
            writer.println("Algoritmo: " + algoritmo);
            writer.println("Tempo de Execução: " + tempo);
            writer.println("Comparações: " + comparacoes);
            writer.println("Trocas: " + trocas);
            writer.println("Dados Ordenados:");
            
            for (int num : array) {
                writer.println(num);
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever arquivo: " + e.getMessage());
        }
    }

    private static int[] lerArquivo(String nomeArquivo) {
        List<Integer> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] numeros = linha.split(",");
                for (String numero : numeros) {
                    lista.add(Integer.parseInt(numero.trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Erro ao converter número: " + e.getMessage());
        }
        
        return lista.stream().mapToInt(i -> i).toArray();
    }
}