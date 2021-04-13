package main;

import java.util.concurrent.ThreadLocalRandom;

public class Main {
	
	public static void main(String[] args) {
		
	}
	
	//Used for getting a rough benchmark for each algorithm
	public static void benchmark(int size, int iterations) {
		int avgTime = 0;
		
		for(int i = 0; i < iterations; i++) {
			Integer[] nums = new Integer[size];
			for(int j = 0; j < nums.length; j++) nums[j] = ThreadLocalRandom.current().nextInt(-nums.length, nums.length);
			
			long startTime = System.currentTimeMillis();
			
			Sorter.quickSort(nums, 0, nums.length - 1);
			
			long time = System.currentTimeMillis() - startTime;
			avgTime += time;
			
			for(int j = 1; j < nums.length; j++) {
				if(nums[j] < nums[j - 1]) {
					System.err.println("---- SORTING ERROR ----");
					return;
				}
			}
		}
		
		System.out.println(size + " Digits Over " + iterations + " Iterations");
		System.out.println("Average Time: " + avgTime/iterations + " ms");
		System.out.println();
	}
	
}
