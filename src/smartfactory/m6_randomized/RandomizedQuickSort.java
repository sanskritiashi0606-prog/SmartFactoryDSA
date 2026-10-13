package smartfactory.m6_randomized;

import smartfactory.model.ProductionOrder;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Randomized QuickSort Implementation.
 * Domain Use Case: Sorts and ranks large batches of manufacturing production orders
 * by priority, due date, and processing cost with guaranteed expected O(N log N) time,
 * immune to pathological quadratic worst-case slowdowns on pre-ordered factory streams.
 */
public class RandomizedQuickSort {

    private static final Random RNG = new Random(42); // Seeded for reproducibility

    /**
     * Sorts a list of Production Orders in-place using Randomized QuickSort.
     *
     * Time Complexity:
     *   - Expected Case: O(N log N)
     *   - Worst Case: O(N^2) (probability negligible due to uniform random pivot)
     * Space Complexity: O(log N) auxiliary recursion stack.
     *
     * @param orders List of factory production orders to sort
     * @param comparator Ordering logic (e.g. descending priority, ascending due date)
     */
    public static void sort(List<ProductionOrder> orders, Comparator<ProductionOrder> comparator) {
        if (orders == null || orders.size() <= 1) return;
        quickSort(orders, 0, orders.size() - 1, comparator);
    }

    private static void quickSort(List<ProductionOrder> list, int low, int high, Comparator<ProductionOrder> comp) {
        if (low < high) {
            int pivotIndex = randomizedPartition(list, low, high, comp);
            quickSort(list, low, pivotIndex - 1, comp);
            quickSort(list, pivotIndex + 1, high, comp);
        }
    }

    private static int randomizedPartition(List<ProductionOrder> list, int low, int high, Comparator<ProductionOrder> comp) {
        int randomIndex = low + RNG.nextInt(high - low + 1);
        swap(list, randomIndex, high); // Move random pivot to end
        return partition(list, low, high, comp);
    }

    private static int partition(List<ProductionOrder> list, int low, int high, Comparator<ProductionOrder> comp) {
        ProductionOrder pivot = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (comp.compare(list.get(j), pivot) <= 0) {
                i++;
                swap(list, i, j);
            }
        }
        swap(list, i + 1, high);
        return i + 1;
    }

    private static void swap(List<ProductionOrder> list, int i, int j) {
        ProductionOrder temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    /**
     * Overload to sort raw integer arrays (e.g. batch metrics, sensor IDs).
     *
     * @param arr Array to sort in-place
     */
    public static void sort(int[] arr) {
        if (arr == null || arr.length <= 1) return;
        quickSortInt(arr, 0, arr.length - 1);
    }

    private static void quickSortInt(int[] arr, int low, int high) {
        if (low < high) {
            int randIdx = low + RNG.nextInt(high - low + 1);
            int tmp = arr[randIdx]; arr[randIdx] = arr[high]; arr[high] = tmp;

            int pivot = arr[high];
            int i = low - 1;
            for (int j = low; j < high; j++) {
                if (arr[j] <= pivot) {
                    i++;
                    int t = arr[i]; arr[i] = arr[j]; arr[j] = t;
                }
            }
            int t = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = t;
            int pIdx = i + 1;

            quickSortInt(arr, low, pIdx - 1);
            quickSortInt(arr, pIdx + 1, high);
        }
    }
}
