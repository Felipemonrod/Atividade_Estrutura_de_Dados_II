package com.mycompany.sortingcomparison;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SortingComparison {
    static String filename = "dados500_mil";
    static String nome = "Felipe Motalvão Rodrigues";
    
    static long comparisons;
    static long swaps;
    
    public static void main(String[] args) {
        String[] algorithms = {"MergeSort", "QuickSort", "RadixSort"};
        String inputFile = "C:\\Users\\felip\\Documents\\Felipe Code\\Java\\ESTII\\SortingComparison2\\SortingComparison2\\Trabalho\\src\\com\\mycompany\\sortingcomparison\\dados500_mil.txt";
        
        int[] originalNumbers = readNumbersFromFile(inputFile);
        if (originalNumbers == null) return;

        for (String algorithm : algorithms) {
            processAlgorithm(originalNumbers, algorithm, true);  // Ordem crescente
            processAlgorithm(originalNumbers, algorithm, false); // Ordem decrescente
            processReordered(algorithm);                         // Reordenar pior caso
        }
    }

    private static void processAlgorithm(int[] numbers, String algorithm, boolean ascending) {
        resetCounters();
        int[] arr = Arrays.copyOf(numbers, numbers.length);
        
        long start = System.currentTimeMillis();
        sort(arr, algorithm, ascending);
        long time = System.currentTimeMillis() - start;
        
        saveResults(arr, algorithm, ascending ? "asc" : "desc", time);
        printStats(algorithm, ascending ? "Crescente" : "Decrescente", time);
    }

    private static void processReordered(String algorithm) {
        int[] worstCase = readSortedFile("sorted_" + algorithm + "_desc.txt");
        if (worstCase == null) return;
        
        resetCounters();
        long start = System.currentTimeMillis();
        sort(worstCase, algorithm, true);
        long time = System.currentTimeMillis() - start;
        
        saveResults(worstCase, algorithm, "reordered_asc", time);
        printStats(algorithm, "Reordenado Crescente", time);
    }

    private static void sort(int[] arr, String algorithm, boolean ascending) {
        switch (algorithm) {
            case "MergeSort":
                mergeSort(arr, 0, arr.length-1, ascending);
                break;
            case "QuickSort":
                quickSort(arr, 0, arr.length-1, ascending);
                break;
            case "RadixSort":
                radixSort(arr, ascending);
                break;
        }
    }

    // Merge Sort implementation
    private static void mergeSort(int[] arr, int left, int right, boolean ascending) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(arr, left, mid, ascending);
            mergeSort(arr, mid + 1, right, ascending);
            merge(arr, left, mid, right, ascending);
        }
    }

    private static void merge(int[] arr, int left, int mid, int right, boolean ascending) {
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;

        while (i <= mid && j <= right) {
            comparisons++;
            if ((ascending && arr[i] <= arr[j]) || (!ascending && arr[i] >= arr[j])) {
                temp[k++] = arr[i++];
            } else {
                temp[k++] = arr[j++];
                swaps += mid - i + 1;
            }
        }

        while (i <= mid) temp[k++] = arr[i++];
        while (j <= right) temp[k++] = arr[j++];
        
        System.arraycopy(temp, 0, arr, left, temp.length);
    }

    // Quick Sort implementation with randomized pivot
    private static void quickSort(int[] arr, int low, int high, boolean ascending) {
        if (low < high) {
            int pi = randomizedPartition(arr, low, high, ascending);
            quickSort(arr, low, pi - 1, ascending);
            quickSort(arr, pi + 1, high, ascending);
        }
    }

    private static int randomizedPartition(int[] arr, int low, int high, boolean ascending) {
        int randomIndex = ThreadLocalRandom.current().nextInt(low, high + 1);
        swap(arr, randomIndex, high);
        return partition(arr, low, high, ascending);
    }

    private static int partition(int[] arr, int low, int high, boolean ascending) {
        int pivot = arr[high];
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            comparisons++;
            if ((ascending && arr[j] < pivot) || (!ascending && arr[j] > pivot)) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    // Radix Sort implementation with negative numbers support
    private static void radixSort(int[] arr, boolean ascending) {
        // Separate positive and negative numbers
        int max = Arrays.stream(arr).max().getAsInt();
        int min = Arrays.stream(arr).min().getAsInt();
        
        // Handle negative numbers
        if (min < 0) {
            // Shift all numbers to make them positive
            int shift = -min;
            for (int i = 0; i < arr.length; i++) {
                arr[i] += shift;
            }
            max += shift;
        }
        
        // Do radix sort on positive numbers
        for (int exp = 1; max/exp > 0; exp *= 10) {
            countSort(arr, exp, ascending);
        }
        
        // Shift back if needed
        if (min < 0) {
            int shift = -min;
            for (int i = 0; i < arr.length; i++) {
                arr[i] -= shift;
            }
        }
        
        // Reverse if descending order
        if (!ascending) {
            reverseArray(arr);
        }
    }

    private static void countSort(int[] arr, int exp, boolean ascending) {
        int[] output = new int[arr.length];
        int[] count = new int[10];
        
        // Store count of occurrences
        for (int num : arr) {
            int digit = (num / exp) % 10;
            count[digit]++;
            comparisons++;
        }
        
        // Change count[i] so it contains actual position
        if (ascending) {
            for (int i = 1; i < 10; i++) {
                count[i] += count[i - 1];
            }
        } else {
            for (int i = 8; i >= 0; i--) {
                count[i] += count[i + 1];
            }
        }
        
        // Build the output array
        for (int i = arr.length - 1; i >= 0; i--) {
            int digit = (arr[i] / exp) % 10;
            output[count[digit] - 1] = arr[i];
            count[digit]--;
            swaps++;
        }
        
        // Copy the output array to arr[]
        System.arraycopy(output, 0, arr, 0, arr.length);
    }

    private static void reverseArray(int[] arr) {
        for (int i = 0; i < arr.length / 2; i++) {
            swap(arr, i, arr.length - 1 - i);
        }
    }

    // Helper methods
    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
        swaps++;
    }

    private static void resetCounters() {
        comparisons = 0;
        swaps = 0;
    }

    private static int[] readNumbersFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            List<Integer> list = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("[,\\[\\]]");
                for (String token : tokens) {
                    String cleanedToken = token.trim().replaceAll("[^\\d-]", "");
                    if (!cleanedToken.isEmpty() && cleanedToken.matches("-?\\d+")) {
                        list.add(Integer.parseInt(cleanedToken));
                    }
                }
            }
            return list.stream().mapToInt(i -> i).toArray();
        } catch (IOException e) {
            System.err.println("Erro na leitura: " + e.getMessage());
            return null;
        }
    }

    private static int[] readSortedFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            return br.lines()
                    .filter(line -> line.matches("-?\\d+"))
                    .mapToInt(Integer::parseInt)
                    .toArray();
        } catch (IOException e) {
            System.err.println("Erro na leitura: " + e.getMessage());
            return null;
        }
    }

    private static void saveResults(int[] arr, String algorithm, String suffix, long time) {
        String filename = "sorted_" + algorithm + "_" + suffix + ".txt";
        try (PrintWriter pw = new PrintWriter(filename)) {
            pw.println("Equipe: " + nome);
            pw.println("Algoritmo: " + algorithm + " - " + suffix);
            pw.println("Tempo: " + formatTime(time));
            pw.println("Comparações: " + comparisons);
            pw.println("Trocas: " + swaps);
            for (int num : arr) {
                pw.println(num);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Erro ao salvar: " + e.getMessage());
        }
    }

    private static String formatTime(long millis) {
        return String.format("%02d:%02d:%02d:%03d",
            millis / 3600000, (millis / 60000) % 60,
            (millis / 1000) % 60, millis % 1000);
    }

    private static void printStats(String algorithm, String mode, long time) {
        System.out.println("Algoritmo: " + algorithm);
        System.out.println("Modo: " + mode);
        System.out.println("Tempo: " + formatTime(time));
        System.out.println("Comparações: " + comparisons);
        System.out.println("Trocas: " + swaps);
        System.out.println("----------------------");
    }
}