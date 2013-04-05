package edu.cmu.lti.bic.bolei.lanstat.hmm;

public class ForwardAlgorithmHMMEvaluator extends AbstractHMMEvaluator {

	ForwardAlgorithmHMMEvaluator(HMM hmm) {
		super(hmm);
	}

	@Override
	public double evaluate(String stream) {
		// init table

		if (stream.endsWith(HMM.END_OF_STREAM) == false) {
			stream += HMM.END_OF_STREAM;
		}

		int N = hmm.getN();
		int T = stream.length();

		table = new double[N][T + 1];
		int startState = hmm.getStartState();
		int finalState = hmm.getFinalState();
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
			}
			scaleupCount += scaleUpTableColumn(t);
		}

		double result = table[finalState][T];
		System.out.println(result + "\t" + scaleupCount);
		return Math.log(result) - scaleupCount * Math.log(SCALEUP_FACTOR);
	}

}
