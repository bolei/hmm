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
		int startState = hmm.findStateIndexOfSymbol(stream.charAt(0) + "");
		table[startState][0] = 1;

		// forward algorithm
		for (int t = 2; t < T; t++) {
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					table[i][t] += table[j][t - 1]
							* hmm.getTransitionTable()[j][i]
							* hmm.getEmissionTable()[i][hmm
									.getSymbolIndex(stream.charAt(t) + "")];
				}
			}
		}

		// find the best to return
		double result = Double.MIN_VALUE;
		for (int i = 0; i < N; i++) {
			if (table[i][T - 1] > result) {
				result = table[i][T - 1];
			}
		}

		return result;
	}
}
