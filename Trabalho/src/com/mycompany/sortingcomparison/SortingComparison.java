package com.mycompany.trabalho_prova;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SortingComparison {

    static String filename = "C:\\Users\\felip\\Downloads\\trabalho(prova)\\trabalho_prova\\src\\com\\mycompany\\trabalho_prova\\dados500_mil.txt";
    static String outputDir = "results/";
    static String teamName = "Felipe Motalvão Rodrigues\nFernando alves de souza\nHenrique ferreira da silva\nVictor HUgo marques leite";

    static long comparisons;
    static long swaps;

    public static void main(String[] args) {
        String[] algorithms = {"MergeSort", "QuickSort", "RadixSort", "BucketSort", "TimSort", "HeapSort"};

        int[] originalNumbers = readNumbersFromFile(filename);
        if (originalNumbers == null) {
            System.err.println("Erro");
            return;
        }

        createOutputDirectory();

        for (String algorithm : algorithms) {
            processAlgorithm(originalNumbers, algorithm, true);
            processAlgorithm(originalNumbers, algorithm, false);
            processReordered(algorithm);
        }

        System.out.println("Salvo em'" + outputDir + "'");
    }

    static void createOutputDirectory() {
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private static void processAlgorithm(int[] numbers, String algorithm, boolean ascending) {
        resetCounters();
        int[] arr = Arrays.copyOf(numbers, numbers.length);

        long start = System.currentTimeMillis();
        sort(arr, algorithm, ascending);
        long time = System.currentTimeMillis() - start;

        String suffix = algorithm + "_" + (ascending ? "asc" : "desc");
        saveResults(arr, suffix, time);
        printStats(algorithm, ascending ? "Crescente" : "Decrescente", time);
    }

    private static void processReordered(String algorithm) {
        String inputFile = outputDir + "sorted_" + algorithm + "_desc.txt";
        int[] worstCase = readSortedFile(inputFile);
        if (worstCase == null) {
            System.err.println("Salvo em" + inputFile);
            return;
        }

        resetCounters();
        long start = System.currentTimeMillis();
        sort(worstCase, algorithm, true);
        long time = System.currentTimeMillis() - start;

        String suffix = algorithm + "_reordered_asc";
        saveResults(worstCase, suffix, time);
        printStats(algorithm, "Reordenado Crescente", time);
    }

    private static void sort(int[] arr, String algorithm, boolean ascending) {
        switch (algorithm) {
            case "MergeSort":
                mergeSort(arr, 0, arr.length - 1, ascending);
                break;
            case "QuickSort":
                quickSort(arr, 0, arr.length - 1, ascending);
                break;
            case "RadixSort":
                radixSort(arr, ascending);
                break;
            case "BucketSort":
                bucketSort(arr, ascending);
                break;
            case "TimSort":
                timSort(arr);
                break;
            case "HeapSort":
                heapSort(arr, ascending);
                break;
            default:
                System.err.println("erro de ordenacao " + algorithm);
        }
    }

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

    private static void radixSort(int[] arr, boolean ascending) {
        int max = Arrays.stream(arr).max().orElse(0);
        int min = Arrays.stream(arr).min().orElse(0);

        if (min < 0) {
            int shift = -min;
            for (int i = 0; i < arr.length; i++) {
                arr[i] += shift;
            }
            max += shift;
        }

        for (int exp = 1; max / exp > 0; exp *= 10) {
            countSort(arr, exp, ascending);
        }

        if (min < 0) {
            int shift = -min;
            for (int i = 0; i < arr.length; i++) {
                arr[i] -= shift;
            }
        }

        if (!ascending) {
            reverseArray(arr);
        }
    }

    private static void countSort(int[] arr, int exp, boolean ascending) {
        int[] output = new int[arr.length];
        int[] count = new int[10];
        Arrays.fill(count, 0);

        for (int num : arr) {
            int digit = (num / exp) % 10;
            count[digit]++;
            comparisons++;
        }

        if (ascending) {
            for (int i = 1; i < 10; i++) {
                count[i] += count[i - 1];
            }
        } else {
            for (int i = 9 - 1; i >= 0; i--) {
                count[i] += count[i + 1];
            }
        }

        for (int i = arr.length - 1; i >= 0; i--) {
            int digit = (arr[i] / exp) % 10;
            output[count[digit] - 1] = arr[i];
            count[digit]--;
            swaps++;
        }

        System.arraycopy(output, 0, arr, 0, arr.length);
    }

    private static void bucketSort(int[] arr, boolean ascending) {
        int n = arr.length;
        if (n <= 0) return;

        int maxVal = Arrays.stream(arr).max().orElse(0);
        int minVal = Arrays.stream(arr).min().orElse(0);

        int bucketCount = n;
        List<Integer>[] buckets = new ArrayList[bucketCount];
        for (int i = 0; i < bucketCount; i++) {
            buckets[i] = new ArrayList<>();
        }

        int range = maxVal - minVal + 1;
        double bucketSize = (double) range / bucketCount;

        for (int num : arr) {
            int bucketIndex = (int) Math.floor((num - minVal) / bucketSize);
            if (bucketIndex < 0) {
                bucketIndex = 0;
            } else if (bucketIndex >= bucketCount) {
                bucketIndex = bucketCount - 1;
            }
            buckets[bucketIndex].add(num);
            swaps++;
        }

        int index = 0;
        for (List<Integer> bucket : buckets) {
            bucket.sort(null);
            if (!ascending) {
                Collections.reverse(bucket);
            }
            for (int num : bucket) {
                arr[index++] = num;
            }
            swaps += bucket.size();
        }
    }

    private static void timSort(int[] arr) {
        Integer[] wrappedArr = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) {
            wrappedArr[i] = arr[i];
        }
        Arrays.sort(wrappedArr);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = wrappedArr[i];
        }
        swaps += arr.length;
    }

    private static void heapSort(int[] arr, boolean ascending) {
        int n = arr.length;

        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i, ascending);
        }

        for (int i = n - 1; i > 0; i--) {
            swap(arr, 0, i);
            heapify(arr, i, 0, ascending);
        }
    }

    private static void heapify(int[] arr, int n, int i, boolean ascending) {
        int largestOrSmallest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n) {
            comparisons++;
            if ((ascending && arr[left] > arr[largestOrSmallest]) || (!ascending && arr[left] < arr[largestOrSmallest])) {
                largestOrSmallest = left;
            }
        }

        if (right < n) {
            comparisons++;
            if ((ascending && arr[right] > arr[largestOrSmallest]) || (!ascending && arr[right] < arr[largestOrSmallest])) {
                largestOrSmallest = right;
            }
        }

        if (largestOrSmallest != i) {
            swap(arr, i, largestOrSmallest);
            heapify(arr, n, largestOrSmallest, ascending);
        }
    }

    private static void reverseArray(int[] arr) {
        for (int i = 0; i < arr.length / 2; i++) {
            swap(arr, i, arr.length - 1 - i);
        }
    }


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
            return br.lines()
                    .flatMap(line -> Arrays.stream(line.split("[,\\[\\]\\s]+")))
                    .filter(s -> !s.trim().isEmpty() && s.matches("-?\\d+"))
                    .mapToInt(Integer::parseInt)
                    .toArray();
        } catch (IOException e) {
            System.err.println("Error de leitura: " + e.getMessage());
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
            System.err.println("Error de leitura: " + e.getMessage());
            return null;
        }
    }

    private static void saveResults(int[] arr, String suffix, long time) {
        String filename = outputDir + "sorted_" + suffix + ".txt";
        try (PrintWriter pw = new PrintWriter(filename)) {
            pw.println("Team: " + teamName);
            pw.println("Algorithm: " + suffix);
            pw.println("Time: " + formatTime(time));
            pw.println("Comparisons: " + comparisons);
            pw.println("Swaps: " + swaps);
            for (int num : arr) {
                pw.println(num);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error de leitura: " + filename + ": " + e.getMessage());
        }
    }

    private static String formatTime(long millis) {
        return String.format("%02d:%02d:%02d.%03d",
                             millis / 3600000, (millis / 60000) % 60,
                             (millis / 1000) % 60, millis % 1000);
    }

    private static void printStats(String algorithm, String mode, long time) {
        System.out.println("Algorithm: " + algorithm);
        System.out.println("Mode: " + mode);
        System.out.println("Time: " + formatTime(time));
        System.out.println("Comparisons: " + comparisons);
        System.out.println("Swaps: " + swaps);
        System.out.println("----------------------");
    }
}