package edu.cmu.lti.bic.bolei.lanstat.hmm;

import java.util.Arrays;

public class ViterbiDecoder {

	public void decode(HMM hmm, String stream) {

		int N = hmm.getN();
		int T = stream.length();

		double[][] delta = new double[N][T];
		int[][] psi = new int[N][T];

		int[] path = new int[T];

		for (int i = 0; i < N; i++) {
			delta[i][0] = hmm.getPi()[i]
					* hmm.getEmissionTable()[i][hmm.getSymbolIndex(stream
							.charAt(0) + "")];
		}

		for (int t = 1; t < T; t++) {
			for (int j = 0; j < N; j++) {
				double max = Double.MIN_VALUE;
				for (int i = 0; i < N; i++) {
					if (max < delta[i][t - 1] * hmm.getTransitionTable()[i][j]) {
						max = delta[i][t - 1] * hmm.getTransitionTable()[i][j];
					}
				}
				delta[j][t] = max
						* hmm.getEmissionTable()[j][hmm.getSymbolIndex(stream
								.charAt(t) + "")];
			}
		}

		for (int t = 1; t < T; t++) {
			for (int j = 0; j < N; j++) {
				int state = -1;
				double max = Double.MIN_VALUE;
				for (int i = 0; i < N; i++) {
					if (max < delta[i][t - 1] * hmm.getTransitionTable()[i][j]) {
						max = delta[i][t - 1] * hmm.getTransitionTable()[i][j];
						state = i;
					}
				}
				psi[j][t] = state;
			}
		}

		path[T - 1] = HMMUtil.findMaxValueIndexIn2dArrayColumn(delta, T - 1);
		for (int t = T - 2; t >= 0; t--) {
			path[t] = psi[path[t + 1]][t + 1];
		}

		System.out.println(Arrays.toString(path));

	}
}
