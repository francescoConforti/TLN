/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conceptSimilarity;

// Java Program to find 

import java.util.Arrays;

// covariance of two set.

class Statistics { 

// Function to find mean. 
public static double mean(double arr[], int n) 
{ 
	double sum = 0; 
	
	for(int i = 0; i < n; i++) 
		sum = sum + arr[i]; 
	
	return sum / n; 
} 

// Function to find covariance. 
public static double covariance(double arr1[], 
					double arr2[], int n) 
{ 
	double sum = 0; 
	
	for(int i = 0; i < n; i++) 
		sum = sum + (arr1[i] - mean(arr1, n)) * 
						(arr2[i] - mean(arr2, n)); 
	return sum / (n - 1); 
}

public static double standardDeviation(double numArray[])
    {
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.length;

        for(double num : numArray) {
            sum += num;
        }

        double mean = sum/length;

        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation/length);
    }

// returning double for compliance with rest of application
public static double[] ranks(double[] arr) {
    class Pair {
        final double value;
        final int index;

        Pair(double value, int index) {
            this.value = value;
            this.index = index;
        }
    }

    Pair[] pairs = new Pair[arr.length];
    for (int index = 0; index < arr.length; ++index) {
        pairs[index] = new Pair(arr[index], index);
    }

    Arrays.sort(pairs, (o1, o2) -> -Double.compare(o1.value, o2.value));

    double[] ranks = new double[arr.length];
    ranks[pairs[0].index] = 1;
    for (int i = 1; i < pairs.length; ++i) {
        if (pairs[i].value == pairs[i - 1].value) {
            ranks[pairs[i].index] = ranks[pairs[i - 1].index];
        } else {
            ranks[pairs[i].index] = i + 1;
        }
    }

    return ranks;
}

// Driver code 
	public static void main (String[] args) { 
	
	double arr1[] = {65.21f, 64.75f, 
			65.26f, 65.76f, 65.96f}; 
	int n = arr1.length; 
	
	double arr2[] = {67.25f, 66.39f, 
				66.12f, 65.70f, 66.64f}; 
	
	int m = arr2.length; 
	
	if (m == n) 
	
	System.out.println(covariance(arr1, arr2, m)); 
	
	} 
} 

// This code is contributed by Gitanjali. 
