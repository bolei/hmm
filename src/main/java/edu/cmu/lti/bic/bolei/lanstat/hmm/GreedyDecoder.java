package edu.cmu.lti.bic.bolei.lanstat.hmm;

import java.util.Arrays;

public class GreedyDecoder {
	public void decode(HMM hmm, String stream) {
		int T = stream.length();
		int[] path = new int[T];

		double[][] emissionTable = hmm.getEmissionTable();

		for (int t = 0; t < T; t++) {
			path[t] = HMMUtil.findMaxValueIndexIn2dArrayColumn(emissionTable,
					hmm.getSymbolIndex(stream.charAt(t) + ""));
		}

		System.out.println(Arrays.toString(path));
	}
}
