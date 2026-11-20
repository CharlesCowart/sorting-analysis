import java.util.Random;
/**
 * A class containing 5 sorting algorithms:
 * Insertion, Heapsort, Quicksort, Tree Sort(Red-Black Tree), and Tournament Sort
 * as well as the metrics to compare their efficiencies.
 * @author Charles Cowart
 * @version 2026.04
 */
public class Sorting {
    private Sorting() {}
    /**
     * Global comparison counter. Incremented by every call to compare().
     * Must be reset to 0 before each timed/counted run.
     */
    private static long comparisons = 0;
 
    /**
     * Wraps Integer.compare and increments the comparisons counter.
     * Every algorithm routes all comparisons through this method to
     * ensure accurate counting.
     * @param a first value
     * @param b second value
     * @return negative if a < b, zero if a == b, positive if a > b
     */
    private static int compare(int a, int b) {
        comparisons++;
        return Integer.compare(a, b);
    }
 
    /**
     * Sorts arr in ascending order using insertion sort.
     * Iterates from index 1 to n-1. For each element, saves it as key
     * and walks backward through the sorted left portion, shifting
     * elements right until the correct position for key is found.
     * @param arr the array to sort
     */
    private static void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && compare(arr[j], key) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }
 
    /**
     * Sorts arr in ascending order using heapsort.
     * Phase 1: builds a max-heap in-place by calling heapify on every
     * non-leaf node from the bottom up. Phase 2: repeatedly swaps the
     * root with the last unsorted element and calls
     * heapify to restore the heap property on the shrinking heap.
     * @param arr the array to sort
     */
    private static void heapSort(int[] arr) {
            int n = arr.length;
            for (int i = n / 2 - 1; i >= 0; i--)
                heapify(arr, n, i);
            for (int i = n - 1; i > 0; i--) {
                int tmp = arr[0]; arr[0] = arr[i]; arr[i] = tmp;
                heapify(arr, i, 0);
            }
    }
    /**
     * Restores heap property at index i within a heap of size n.
     * Finds the largest value among the node at i and its two children.
     * If the largest is not i, swaps and recurses down. 
     * Called both during heap construction and during extraction.
     * @param arr the array representing the heap
     * @param n the size of the heap
     * @param i the index of the root of the subtree to heapify
     */
    private static void heapify(int[] arr, int n, int i) {
        int largest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        if (l < n && compare(arr[l], arr[largest]) > 0) largest = l;
        if (r < n && compare(arr[r], arr[largest]) > 0) largest = r;
        if (largest != i) {
            int tmp = arr[i]; arr[i] = arr[largest]; arr[largest] = tmp;
            heapify(arr, n, largest);
        }
    }

    /**
     * Sorts arr in ascending order using quicksort.
     * Partitions the subarray arr[low..high] around a pivot
     * using a stack to avoid stack overflow on large inputs.
     * Uses the last element as pivot.
     * @param arr the array to sort, modified in place
     * @param low the lower bound index of the subarray
     * @param high the upper bound index of the subarray 
     */
    private static void quickSort(int[] arr, int low, int high) {
        int[] stack = new int[arr.length * 2];
        int top = -1;
        stack[++top] = low;
        stack[++top] = high;
        while (top >= 0) {
            high = stack[top--];
            low = stack[top--];
            if (low < high) {
                int pi = partition(arr, low, high);
                stack[++top] = low;
                stack[++top] = pi - 1;
                stack[++top] = pi + 1;
                stack[++top] = high;
            }
        }
    }
    /**
     * Partitions arr[low..high] around the pivot arr[high].
     * Maintains a boundary i such that all elements left of i are <= pivot.
     * Scans j forward across the subarray. When arr[j] <= pivot, increments
     * i and swaps arr[i] and arr[j] then places the pivot at i+1.
     * @param arr the array to partition
     * @param low the lower bound of the subarray
     * @param high the upper bound of the subarray; arr[high] is the pivot
     * @return the final index of the pivot after partitioning
     */
    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (compare(arr[j], pivot) <= 0) {
                i++;
                int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
            }
        }
        int tmp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = tmp;
        return i + 1;
    }

    private static final boolean RED   = true;
    private static final boolean BLACK = false;
 
    private static class RBNode {
        int val;
        boolean color;
        RBNode left, right, parent;
 
        RBNode(int val) {
            this.val   = val;
            this.color = RED;
        }
    }
 
    private static class RedBlackTree {
        RBNode root;
        RBNode leaf = new RBNode(0);
 
        private RedBlackTree() {
            leaf.color = BLACK;
            leaf.left  = leaf;
            leaf.right = leaf;
            root = leaf;
        }
 
        private void insert(int val) {
            RBNode z = new RBNode(val);
            z.left   = leaf;
            z.right  = leaf;
            z.parent = leaf;
 
            RBNode y = leaf;
            RBNode x = root;
 
            while (x != leaf) {
                y = x;
                if (compare(z.val, x.val) < 0)
                    x = x.left;
                else
                    x = x.right;
            }
            z.parent = y;
            if (y == leaf)
                root = z;
            else if (compare(z.val, y.val) < 0)
                y.left = z;
            else
                y.right = z;
 
            z.color = RED;
            insertFixup(z);
        }
 
        private void insertFixup(RBNode z) {
            while (z.parent.color == RED) {
                if (z.parent == z.parent.parent.left) {
                    RBNode y = z.parent.parent.right;
                    if (y.color == RED) {
                        z.parent.color = BLACK;
                        y.color = BLACK;
                        z.parent.parent.color = RED;
                        z = z.parent.parent;
                    } else {
                        if (z == z.parent.right) {
                            z = z.parent;
                            rotateLeft(z);
                        }
                        z.parent.color = BLACK;
                        z.parent.parent.color = RED;
                        rotateRight(z.parent.parent);
                    }
                } else {
                    RBNode y = z.parent.parent.left;
                    if (y.color == RED) {
                        z.parent.color = BLACK;
                        y.color = BLACK;
                        z.parent.parent.color = RED;
                        z = z.parent.parent;
                    } else {
                        if (z == z.parent.left) {
                            z = z.parent;
                            rotateRight(z);
                        }
                        z.parent.color = BLACK;
                        z.parent.parent.color = RED;
                        rotateLeft(z.parent.parent);
                    }
                }
            }
            root.color = BLACK;
        }
 
        private void rotateLeft(RBNode x) {
            RBNode y = x.right;
            x.right  = y.left;
            if (y.left != leaf) y.left.parent = x;
            y.parent = x.parent;
            if (x.parent == leaf) root = y;
            else if (x == x.parent.left) x.parent.left = y;
            else x.parent.right = y;
            y.left = x;
            x.parent = y;
        }
 
        private void rotateRight(RBNode x) {
            RBNode y = x.left;
            x.left = y.right;
            if (y.right != leaf) y.right.parent = x;
            y.parent = x.parent;
            if (x.parent == leaf) root = y;
            else if (x == x.parent.right) x.parent.right = y;
            else x.parent.left  = y;
            y.right  = x;
            x.parent = y;
        }
 
        private void inOrder(RBNode node, int[] result, int[] idx) {
            if (node == leaf) return;
            inOrder(node.left, result, idx);
            result[idx[0]++] = node.val;
            inOrder(node.right, result, idx);
        }
    }
 
    /**
     * Sorts arr in ascending order using tree sort backed by a red-black tree.
     * Inserts every element into a RedBlackTree, then overwrites arr with
     * an in-order traversal of the tree.
     * @param arr the array to sort
     */
    private static void treeSort(int[] arr) {
        RedBlackTree rbt = new RedBlackTree();
        for (int v : arr) rbt.insert(v);
        int[] idx = {0};
        rbt.inOrder(rbt.root, arr, idx);
    }

    /**
     * Sorts arr in ascending order using tournament sort.
     * Builds a complete binary tree of size 2*leaves, 
     * placing input values at the leaves.
     * Internal nodes hold the minimum of their two children. 
     * The root always holds the global minimum. Extracts the
     * minimum n times: each extraction replaces the winning leaf and
     * replays comparisons back up to the root to find the new minimum.
     * @param arr the array to sort
     */
    private static void tournamentSort(int[] arr) {
        int n = arr.length;
        if (n == 0) return;
 
        int leaves = 1;
        while (leaves < n) leaves *= 2; 
        int treeSize = 2 * leaves;
        int[] tree = new int[treeSize];
        int Inf = Integer.MAX_VALUE;
 
        for (int i = 0; i < leaves; i++) {
            if (i < n) {
                tree[leaves + i] = arr[i];
            } else {
                tree[leaves + i] = Inf;
            }
        }
 
        for (int i = leaves - 1; i >= 1; i--) {
            if (compare(tree[2*i], tree[2*i+1]) <= 0)
                tree[i] = tree[2*i];
            else
                tree[i] = tree[2*i+1];
        }
 
        for (int k = 0; k < n; k++) {
            arr[k] = tree[1];
 
            int pos = 1;
            while (pos < leaves) {
                if (tree[2*pos] == arr[k])
                    pos = 2 * pos;
                else
                    pos = 2 * pos + 1;
            }
            tree[pos] = Inf;
 
            pos /= 2;   
            while (pos >= 1) {
                int left  = tree[2*pos];
                int right = tree[2*pos+1];
                if (compare(left, right) <= 0)
                    tree[pos] = left;
                else
                    tree[pos] = right;
                pos /= 2;            
            }
        }
    }

    /**
     * Generates a random array of [1..n] with no duplicates.
     * @param n the size of the array
     * @param seed seed used for random generation of array
     * @return a shuffled array containing each integer from 1 to n exactly once
     */
    private static int[] generateRandom(int n, long seed) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = i + 1;
        Random rng = new Random(seed);
        for (int i = n - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
        }
        return arr;
    }

    /**
     * Generates an ascending-sorted array [1, 2, ..., n].
     * @param n the size of the array
     * @return sorted array from 1 to n
     */
    private static int[] generateSorted(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = i + 1;
        return arr;
    }
 
    /**
     * Generates a descending-sorted array [n, n-1, ..., 1].
     * @param n the size of the array
     * @return reverse-sorted array from n down to 1
     */
    private static int[] generateReverseSorted(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = n - i;
        return arr;
    }

    /**
     * Runs all five sorting algorithms on a clone of original, recording
     * run time (ms) and comparison count for each. Resets the global
     * comparisons counter before each algorithm. Prints results as strings.
     * @param label a description of the input type
     * @param original the input array
     */
    private static void runExperiment(String label, int[] original) {
        System.out.println("\n" + label);

        int[] string;

        string = original.clone(); comparisons = 0;
        long start = System.currentTimeMillis();
        insertionSort(string);
        System.out.println("Insertion time=" + (System.currentTimeMillis()-start) + "ms comps=" + comparisons);

        string = original.clone(); comparisons = 0;
        start = System.currentTimeMillis();
        heapSort(string);
        System.out.println("Heap time=" + (System.currentTimeMillis()-start) + "ms comps=" + comparisons);

        string = original.clone(); comparisons = 0;
        start = System.currentTimeMillis();
        quickSort(string, 0, string.length - 1);
        System.out.println("Quick time=" + (System.currentTimeMillis()-start) + "ms comps=" + comparisons);

        string = original.clone(); comparisons = 0;
        start = System.currentTimeMillis();
        treeSort(string);
        System.out.println("Tree time=" + (System.currentTimeMillis()-start) + "ms comps=" + comparisons);

        string = original.clone(); comparisons = 0;
        start = System.currentTimeMillis();
        tournamentSort(string);
        System.out.println("Tournament time=" + (System.currentTimeMillis()-start) + "ms comps=" + comparisons);
    }
    /**
     * Runs the full experiment across input sizes n = 1000,
     * 5000, 10000, and 50000, testing each algorithm against random, sorted, and
     * reverse-sorted input. Prints all results.
     * @param args unused
     */
    public static void main(String[] args) {
        int[] sizes = {1000, 5000, 10000, 50000};
        for (int n : sizes) {
            System.out.println("\n=== n=" + n + " ===");
            runExperiment("Random", generateRandom(n, 1));
            runExperiment("Sorted", generateSorted(n));
            runExperiment("Reverse-Sorted", generateReverseSorted(n));
        }
    }
}