package edu.cmu.lti.bic.bolei.lanstat.hmm;

public class HMMUtil {
	public static final double M_LN2 = 0.69314718055994530942;

	public static void print2dArray(double[][] array) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				sb.append(array[i][j] + "\t");
			}
			sb.append("\n");
		}
		System.out.println(sb.toString());
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
}
