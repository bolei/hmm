package edu.cmu.lti.bic.bolei.lanstat.hmm;

public class BackwardAlgorithmHMMEvaluator extends AbstractHMMEvaluator {

	BackwardAlgorithmHMMEvaluator(HMM hmm) {
		super(hmm);
	}

	@Override
	public double evaluate(String stream) {

		// init table

		int N = hmm.getN();
		int T = stream.length();

		table = new double[N][T + 1];
		int finalState = hmm.getFinalState();
		table[finalState][T] = 1;

		// backward algorithm
		int scaleupCount = 0;
		for (int t = T - 1; t >= 0; t--) {
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					table[i][t] += table[j][t + 1]
							* hmm.getTransitionTable()[i][j]
							* hmm.getEmissionTable()[j][hmm
									.getSymbolIndex(stream.charAt(t) + "")];
				}
				if (table[i][t] < (1 / SCALEUP_FACTOR)) {
					table[i][t] *= SCALEUP_FACTOR;
					scaleupCount++;
				}
			}
		}

		// find the best to return
		double result = Double.MIN_VALUE;
		for (int i = 0; i < N; i++) {
			if (table[i][0] > result) {
				result = table[i][0];
			}
		}

		return Math.log(result) + scaleupCount * Math.log(SCALEUP_FACTOR);

	}

}
