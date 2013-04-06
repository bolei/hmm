package edu.cmu.lti.bic.bolei.lanstat.hmm;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

public class HMMUtil {
	public static final double M_LN2 = 0.69314718055994530942;

	private static Properties config;

	public static void print2dArray(double[][] array) {

		System.out.println(get2dArrayString(array));
	}

	public static String get2dArrayString(double[][] array) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			sb.append("=>");
			for (int j = 0; j < array[0].length; j++) {
				sb.append(array[i][j] + "\t");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public static int scaleUpTableColumn(double[][] table, int t,
			int scaleupFactor) {
		double minPos = Double.MAX_VALUE;
		for (int i = 0; i < table.length; i++) {
			if (table[i][t] < minPos
					&& table[i][t] * Math.pow(10, scaleupFactor) > 0) {
				minPos = table[i][t];
			}
		}
		if (minPos == 0) {
			return 0;
		}
		int x = (int) Math.ceil((Math.log10(minPos) / scaleupFactor) * (-1)) - 1;
		if (x <= 0) {
			return 0;
		}
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < x; j++) {
				table[i][t] *= Math.pow(10, scaleupFactor);
			}
		}
		return x;
	}

	/**
	 * Evaluate log(exp(left) + exp(right)) more accurately. log(exp(left) +
	 * exp(right)) = log(exp(left)) + log(1 + exp(right) / exp(left)) = left +
	 * log(1 + exp(right - left)) Note: log1p(x) accurately computes log(1+x)
	 * for small x.
	 */
	public static double logAdd(double left, double right) {
		if (right < left) {
			return left + Math.log1p(Math.exp(right - left));
		} else if (right > left) {
			return right + Math.log1p(Math.exp(left - right));
		} else {
			return left + M_LN2;
		}
	}

	public static Properties getConfiguration() {
		try {
			if (config == null) {
				config = new Properties();
				config.load(HMMUtil.class
						.getResourceAsStream("/config/state.emission.properties"));
			}
			return config;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static double[][] generateRandomTransitionTable(int N) {
		// for each row, elements are not uniform distributed.
		// last element is always bigger
		double[][] table = new double[N][N];
		for (int i = 0; i < N - 1; i++) {
			table[i] = generateConstantSumRandomArray(1.0d, N);
		}
		return table;
	}

	public static double[][] generateRandomEmissionTable(int N, int V) {
		double[][] table = new double[N][V];
		for (int i = 1; i < N - 1; i++) {
			table[i] = Arrays.copyOf(generateConstantSumRandomArray(1d, V - 1),
					V);
		}
		table[N - 1][V - 1] = 1;
		return table;
	}

	private static double[] generateConstantSumRandomArray(double sum, int len) {
		double[] array = new double[len];
		Random rand = new Random();
		for (int j = 0; j < len - 1; j++) {
			array[j] = rand.nextDouble() * sum / len;
			sum -= array[j];
		}
		array[len - 1] = sum;
		return array;
	}
}
