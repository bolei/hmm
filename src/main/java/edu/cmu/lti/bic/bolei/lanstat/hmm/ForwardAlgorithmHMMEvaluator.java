package edu.cmu.lti.bic.bolei.lanstat.hmm;

public class ForwardAlgorithmHMMEvaluator extends AbstractHMMEvaluator {

	ForwardAlgorithmHMMEvaluator(HMM hmm) {
		super(hmm);
	}

	@Override
	public double evaluate(String stream) {
		// init table

		int N = hmm.getN();
		int T = stream.length();

		table = new double[N][T + 1];
		int startState = hmm.getStartState();
		table[startState][0] = 1;

		// forward algorithm
		int scaleupCount = 0;
		for (int t = 1; t <= T; t++) {

			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					table[i][t] += table[j][t - 1]
							* hmm.getTransitionTable()[j][i]
							* hmm.getEmissionTable()[i][hmm
									.getSymbolIndex(stream.charAt(t - 1) + "")];
				}
				if (table[i][t] < (1 / SCALEUP_FACTOR)) {
					table[i][t] *= SCALEUP_FACTOR;
					scaleupCount++;
				}
			}
		}
		System.out.println(HMM.arrayToString(table, N));
		// find the best to return
		double result = Double.MIN_VALUE;
		for (int i = 0; i < N; i++) {
			if (table[i][T] > result) {
				result = table[i][T];
			}
		}

		return Math.log(result) + scaleupCount * Math.log(SCALEUP_FACTOR);
	}
}
