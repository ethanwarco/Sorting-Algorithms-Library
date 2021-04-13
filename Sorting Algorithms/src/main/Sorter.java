package main;

import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * 
 * @author Ethan Warco
 * 
 */
public class Sorter {
	
	@SuppressWarnings("serial")
	private static class ParallelQuickSort<T extends Comparable<T>> extends RecursiveAction {
		
		private final T[] arr;
		private final int fromIndex;
		private final int toIndex;
		private final int threshold;
		
		ParallelQuickSort(T[] arr, int fromIndex, int toIndex, int threshold) {
			this.arr = arr;
			this.fromIndex = fromIndex;
			this.toIndex = toIndex;
			this.threshold = threshold;
		}
		
		protected void compute() {
			if(toIndex - fromIndex <= threshold) quickSort(arr, fromIndex, toIndex);
			else if(fromIndex < toIndex) {
				int p = partition(arr, fromIndex, toIndex);
				
				invokeAll(new ParallelQuickSort<T>(arr, fromIndex, p, threshold), new ParallelQuickSort<T>(arr, p + 1, toIndex, threshold));
			}
		}
		
	}
	
	/**
	 * Quick Sort is a divide and conquer sorting algorithm in which it chooses a pivot and
	 * partitions the array such that members greater than the pivot are moved to the right,
	 * and all members less than the pivot are moved to the left. it then recursively calls
	 * itself on the remaining two halves and repeats. This sort is not stable.
	 * 
	 * <p>
	 * @param <T>
	 * @param arr - an array of a comparable type T
	 * @param fromIndex - the index the sorting algorithm will start at (inclusive)
	 * @param toIndex - the index the sorting algorithm will end at (inclusive)
	 * 
	 * @see parallelQuickSort
	 */
	public static <T extends Comparable<T>> void quickSort(T[] arr, int fromIndex, int toIndex) {
		while(fromIndex < toIndex) {
			int p = partition(arr, fromIndex, toIndex);
			quickSort(arr, p + 1, toIndex);
			toIndex = p;
		}
	}
	
	/**
	 * Parallel Quick Sort is an improved version of the regular quick sort
	 * algorithm, designed to work on multicore machines.
	 * 
	 * <p>
	 * @param <T>
	 * @param arr - an array of a comparable type T
	 * @param fromIndex - the index the sorting algorithm will start at (inclusive)
	 * @param toIndex - the index the sorting algorithm will end at (inclusive)
	 * @param threads - the amount of threads available to the algorithm
	 * 
	 * @throws IllegalArgumentException - if the number of threads given is less than
	 * or equal to 1
	 * 
	 * @see quickSort
	 */
	public static <T extends Comparable<T>> void parallelQuickSort(T[] arr, int fromIndex, int toIndex, int threads) {
		if(threads <= 1) throw new IllegalArgumentException("Threads must be > 1");
		ForkJoinPool pool = new ForkJoinPool(threads);
		pool.submit(new ParallelQuickSort<T>(arr, fromIndex, toIndex, ((toIndex - fromIndex) + threads)/threads)).join();
	}
	
	
	@SuppressWarnings("serial")
	private static class ParallelMergeSort<T extends Comparable<T>> extends RecursiveAction {
		
		private final int threshold;
		private final int fromIndex;
		private final int toIndex;
		private final T[] arr;
		
		ParallelMergeSort(T[] arr, int fromIndex, int toIndex, int threshold) {
			this.arr = arr;
			this.fromIndex = fromIndex;
			this.toIndex = toIndex;
			this.threshold = threshold;
		}
		
		protected void compute() {
			if(toIndex - fromIndex <= threshold) mergeSort(arr, fromIndex, toIndex);
			else if(fromIndex < toIndex) {
				int median = fromIndex + (toIndex - fromIndex + 1)/2;
				
				invokeAll(new ParallelMergeSort<T>(arr, fromIndex, median - 1, threshold), new ParallelMergeSort<T>(arr, median, toIndex, threshold));
				merge(arr, fromIndex, toIndex, median);
			}
		}
		
	}
	
	/**
	 * Merge Sort recursively divides and sorts both halves of the given subarray
	 * until it is left with an array of length 1, in which case it will merge the
	 * two sorted halves together using a merging algorithm. This sort is stable.
	 * 
	 * <p>
	 * @param <T>
	 * @param arr - an array of a comparable type T
	 * @param fromIndex - the index the sorting algorithm will start at (inclusive)
	 * @param toIndex - the index the sorting algorithm will end at (inclusive)
	 * 
	 * @see parallelMergeSort
	 */
	public static <T extends Comparable<T>> void mergeSort(T[] arr, int fromIndex, int toIndex) {
		if(fromIndex < toIndex) {
			int median = fromIndex + (toIndex - fromIndex + 1)/2;
			
			mergeSort(arr, fromIndex, median - 1);
			mergeSort(arr, median, toIndex);
			merge(arr, fromIndex, toIndex, median);
		}
	}
	
	/**
	 * Parallel Merge Sort is an improved version of the regular merge sort
	 * algorithm, designed to work on multicore machines.
	 * 
	 * <p>
	 * @param <T>
	 * @param arr - an array of a comparable type T
	 * @param fromIndex - the index the sorting algorithm will start at (inclusive)
	 * @param toIndex - the index the sorting algorithm will end at (inclusive)
	 * @param threads - the amount of threads available to the algorithm
	 * 
	 * @throws IllegalArgumentException - if the number of threads given is less than
	 * or equal to 1
	 * 
	 * @see mergeSort
	 */
	public static <T extends Comparable<T>> void parallelMergeSort(T[] arr, int fromIndex, int toIndex, int threads) {
		if(threads <= 1) throw new IllegalArgumentException("Threads must be > 1");
		ForkJoinPool pool = new ForkJoinPool(threads);
		pool.submit(new ParallelMergeSort<T>(arr, fromIndex, toIndex, ((toIndex - fromIndex) + threads)/threads)).join();
	}
	
	
	/**
	 * Insertion Sort works by looping through the array and for each element, i,
	 * performing a binary search on the already sorted portion of the array and
	 * inserting i into the correct spot. This sort is stable.
	 * 
	 * <p>
	 * @param <T>
	 * @param arr - an array of comparable type T
	 * @param fromIndex - the index the sorting algorithm will start at (inclusive)
	 * @param toIndex - the index the sorting algorithm will end at (inclusive)
	 */
	public static <T extends Comparable<T>> void insertionSort(T[] arr, int fromIndex, int toIndex) {
		for(int i = fromIndex; i <= toIndex; i++) {
			int mid = binarySearch(arr, fromIndex, i, arr[i], true);
			
			swap(arr, i, mid);
			System.arraycopy(arr, mid, arr, mid + 1, i - mid);
		}
	}
	
	
	/**
	 * Heap Sort uses the algorithm used in the Heap Data Structure to keep it sorted.
	 * First, it "heapifies" the array in place, by looping through every node and
	 * performing a sift-down operation on that node in order to confirm that the current
	 * node is the largest among it's children. Once the heapify operation is complete,
	 * the top element in the array will be the largest node. Now, it will loop until the 
	 * heap is empty, choosing the last node in the heap, swapping it with the top node,
	 * then sifting down the node in order to satisfy the heap invariant. Once the heap is
	 * empty, the array will have been sorted. This sort is not stable.
	 * 
	 * <p>
	 * @param <T>
	 * @param arr - an array of comparable type T
	 * @param fromIndex - the index the sorting algorithm will start at (inclusive)
	 * @param toIndex - the index the sorting algorithm will end at (inclusive)
	 */
	public static <T extends Comparable<T>> void heapSort(T[] arr, int fromIndex, int toIndex) {
		int start = ((toIndex - fromIndex)-1)/2;
		while(start >= 0) {
			siftDown(arr, fromIndex, toIndex, start);
			start--;
		}
		
		while(toIndex > fromIndex) {
			swap(arr, fromIndex, toIndex);
			
			toIndex--;
			
			siftDown(arr, fromIndex, toIndex, 0);
		}
	}
	
	private static <T extends Comparable<T>> void siftDown(T[] arr, int fromIndex, int toIndex, int root) {
		while(2*root + 1 + fromIndex <= toIndex) {
			int left = 2*root + 1, right = left + 1;
			int child = left;
			if(right + fromIndex <= toIndex) child = lessThan(arr[left + fromIndex], arr[right + fromIndex]) ? right : left;
			
			if(lessThan(arr[root + fromIndex], arr[child + fromIndex])) {
				swap(arr, child + fromIndex, root + fromIndex);
				root = child;
			} else return;
		}
	}
	
	
	/**
	 * Introsort uses Quicksort's fast sorting, and Heapsort's low worst case time complexity
	 * to sort arrays more efficiently. This sort is not stable.
	 * 
	 * <p>
	 * @param <T>
	 * @param arr - an array of comparable type T
	 * @param fromIndex - the index the sorting algorithm will start at (inclusive)
	 * @param toIndex - the index the sorting algorithm will end at (inclusive)
	 */
	public static <T extends Comparable<T>> void introSort(T[] arr, int fromIndex, int toIndex) {
		introSort(arr, fromIndex, toIndex, (int)(Math.log(arr.length) * 2));
	}
	
	private static <T extends Comparable<T>> void introSort(T[] arr, int fromIndex, int toIndex, int maxDepth) {
		if(fromIndex < toIndex) {
			if(maxDepth == 0) heapSort(arr, fromIndex, toIndex);
			else {
				int p = partition(arr, fromIndex, toIndex);
				
				introSort(arr, fromIndex, p, maxDepth - 1);
				introSort(arr, p + 1, toIndex, maxDepth - 1);
			}
		}
	}
	
	
	/**
	 * Timsort was created in 2002 by Tim Peters, the main contributor to the Python programming language.
	 * It is a hybrid algorithm that uses insertion sort and merge sort to work well on real world data, 
	 * where the order of the data not exactly completely random. It is stable.
	 * 
	 * <p>
	 * @param <T>
	 * @param arr - an array of comparable type T
	 * @param fromIndex - the index the sorting algorithm will start at (inclusive)
	 * @param toIndex - the index the sorting algorithm will end at (inclusive)
	 */
	public static <T extends Comparable<T>> void timSort(T[] arr, int fromIndex, int toIndex) {
		//Finds the 6 least significant bits
		int r = 0, n;
		for(n = arr.length; n >= 32; n >>= 1) r |= n & 1;
		int minrun = r + n;
		
		//The integer contained in the stack marks the beginning index of each run (inclusive)
		Stack<Integer> runs = new Stack<Integer>();
		
		//Code for finding runs
		for(int i = fromIndex, j = fromIndex+1; i <= toIndex; i = j) {
			runs.push(i);
			
			//Tracks for runs that are ascending / descending
			boolean ascending = true, descending = true;
			while(j <= toIndex && (ascending || descending)) {
				if(arr[j].compareTo(arr[j - 1]) >= 0) descending = false;
				else ascending = false;
				
				j++;
			}
			
			if(j - i - 1 < minrun) {
				j = Math.min(i + minrun, toIndex);
				insertionSort(arr, i, j);
				j++;
			} else if(descending) reverse(arr, i, j - 1);
			
			
			//Code for merging runs, MUST maintain the following invariants, where A, B, & C are the top 3 runs of the stack:
			// - A + B < C
			// - A < B
			while(runs.size() > 1) {
				int a = runs.pop(), b = runs.pop(), bLen = a - b, aLen = j - a;
				
				if(!runs.isEmpty() && aLen + bLen >= b - runs.peek()) {
					merge(arr, runs.peek(), a-1, b); //Merges B with C
					runs.push(a);
				} else if(aLen >= bLen) {
					merge(arr, b, j-1, a); //Merges A with B
					runs.push(b);
				} else {
					runs.push(b);
					runs.push(a);
					break;
				}
			}
		}
		
		//One last loop to merge all runs
		while(runs.size() > 1) {
			int a = runs.pop(), b = runs.pop(), bLen = a - b, aLen = toIndex - a;
			
			if(!runs.isEmpty() && aLen + bLen >= b - runs.peek()) {
				merge(arr, runs.peek(), a-1, b); //Merges B with C
				runs.push(a);
			} else {
				merge(arr, b, toIndex, a); //Merges A with B
				runs.push(b);
			}
		}
	}
	
	
	@SuppressWarnings("serial")
	private static class ParallelRadixSort extends RecursiveAction {
		
		private final int[] arr;
		private final int fromIndex;
		private final int toIndex;
		private final int threshold;
		private final int bit;
		private final boolean signed;
		
		ParallelRadixSort(int[] arr, int fromIndex, int toIndex, int bit, boolean signed, int threshold) {
			this.arr = arr;
			this.fromIndex = fromIndex;
			this.toIndex = toIndex;
			this.threshold = threshold;
			this.signed = signed;
			this.bit = bit;
		}
		
		protected void compute() {
			if(toIndex - fromIndex <= threshold) radixSort(arr, fromIndex, toIndex);
			else if(fromIndex < toIndex) {
				if(bit < 0 || fromIndex >= toIndex) return;
				int zeros = fromIndex - 1, ones = toIndex + 1, index = zeros + 1;
				
				while(index < ones) {
					if(((arr[index] >> bit) & 1) == 1 ^ signed) {
						ones--;
						
						int val = arr[ones];
						arr[ones] = arr[index];
						arr[index] = val;
					} else {
						zeros++;
						index++;
					}
				}
				
				invokeAll(new ParallelRadixSort(arr, fromIndex, zeros, bit-1, false, threshold), new ParallelRadixSort(arr, ones, toIndex, bit-1, false, threshold));
			}
		}
		
	}
	
	/**
	 * This implementation of Radix Sort is a variant known as Binary MSD Radix Sort, AKA
	 * Binary Quicksort. It sorts 32 bit signed integers by creating 2 "buckets": Ones, and Zeros.
	 * Starting with the most significant bit, or MSD, it inserts each digit in the array
	 * into the "Ones" bucket if the given bit is 1, or the "Zeros" bucket if the given bit
	 * is a 0. Then, the recursion starts and each bucket is sorted by their radix, with the
	 * next most significant bit. This process continues until the given bit is the least
	 * significant bit, or LSD. While this sort is much faster than quicksort, it is only
	 * compatible with numbers and bit-based data. It will not work with comparable objects
	 * as it is not a comparison-based algorithm. This sort is also not stable.
	 * 
	 * <p>
	 * @param arr - an array of integers
	 * @param fromIndex - the index the sorting algorithm will start at (inclusive)
	 * @param toIndex - the index the sorting algorithm will end at (inclusive)
	 * 
	 * @see parallelRadixSort
	 */
	public static void radixSort(int[] arr, int fromIndex, int toIndex) {
		radixSort(arr, fromIndex, toIndex, true, 31);
	}
	
	private static void radixSort(int[] arr, int fromIndex, int toIndex, boolean signed, int bit) {
		if(bit < 0 || fromIndex >= toIndex) return;
		int zeros = fromIndex - 1, ones = toIndex + 1, index = zeros + 1;
		
		while(index < ones) {
			if(((arr[index] >> bit) & 1) == 1 ^ signed) {
				ones--;
				
				int val = arr[ones];
				arr[ones] = arr[index];
				arr[index] = val;
			} else {
				zeros++;
				index++;
			}
		}
		
		radixSort(arr, fromIndex, zeros, false, bit - 1);
		radixSort(arr, ones, toIndex, false, bit - 1);
	}
	
	/**
	 * An improved version of Radix Sort, this sort is designed to work on multicore machines.
	 * 
	 * <p>
	 * @param arr - an array of integers
	 * @param fromIndex - the index the sorting algorithm will start at (inclusive)
	 * @param toIndex - the index the sorting algorithm will end at (inclusive)
	 * @param threads - the amount of threads available to the algorithm
	 * 
	 * @throws IllegalArgumentException - if the number of threads given is less than
	 * or equal to 1
	 * 
	 * @see radixSort
	 */
	public static void parallelRadixSort(int[] arr, int fromIndex, int toIndex, int threads) {
		if(threads <= 1) throw new IllegalArgumentException("Threads must be > 1");
		ForkJoinPool pool = new ForkJoinPool(threads);
		pool.submit(new ParallelRadixSort(arr, fromIndex, toIndex, 31, true, ((toIndex - fromIndex) + threads)/threads)).join();
	}
	
	
	
	
	
	//HELPER FUNCTIONS
	
	private static <T extends Comparable<T>> int partition(T[] arr, int low, int high) {
		//Uses Hoare's partition algorithm
		T pivot = arr[low + (high - low)/2];
		int i = low - 1, j = high + 1;
		
		while(true) {
			do i++;
			while(lessThan(arr[i], pivot));
			
			do j--;
			while(lessThan(pivot, arr[j]));
			
			if(i >= j) return j;
			
			swap(arr, i, j);
		}
	}
	
	private static <T extends Comparable<T>> void merge(T[] arr, int fromIndex, int toIndex, int median) {
		T[] temp = Arrays.copyOfRange(arr, fromIndex, toIndex + 1);
		
		int k = median - fromIndex, j = 0, i = fromIndex, jLim = median - fromIndex;
		while(i <= toIndex) {
			if(k == temp.length) break;
			while(j < jLim && temp[j].compareTo(temp[k]) <= 0) {
				arr[i] = temp[j];
				j++;
				i++;
			}
			
			if(j == jLim) break;
			while(k < temp.length && temp[k].compareTo(temp[j]) < 0) {
				arr[i] = temp[k];
				k++;
				i++;
			}
		}
		
		if(k == temp.length) System.arraycopy(temp, j, arr, i, jLim - j);
		else if(j == jLim) System.arraycopy(temp, k, arr, i, temp.length - k);
	}
	
	//Returns a < b
	private static <T extends Comparable<T>> boolean lessThan(T a, T b) {
		return a.compareTo(b) < 0;
	}
	
	private static <T extends Comparable<T>> void swap(T[] arr, int index1, int index2) {
		T obj = arr[index2];
		arr[index2] = arr[index1];
		arr[index1] = obj;
	}
	
	private static <T extends Comparable<T>> int binarySearch(T[] arr, int low, int high, T obj, boolean stable) {
		int mid = low + (high - low)/2;
		while(low <= high) {
			mid = low + (high - low)/2;
			
			if(lessThan(arr[mid], obj) || (!stable && arr[mid].compareTo(obj) == 0)) low = mid + 1;
			else high = mid - 1;
		}
		
		return mid;
	}
	
	private static <T extends Comparable<T>> void reverse(T[] arr, int fromIndex, int toIndex) {
		while(toIndex > arr.length/2) {
			swap(arr, fromIndex, toIndex);
			fromIndex++;
			toIndex--;
		}
	}
	
}
