package edu.cmu.lti.bic.bolei.lanstat.hmm;

public class ForwardAlgorithmHMMEvaluator extends AbstractHMMEvaluator {

	@Override
	public StateObservationTable computeTable(HMM hmm, String stream) {
		int startState = hmm.getStartState();
		int N = hmm.getN();
		int T = stream.length();
		double[][] table = new double[N][T + 1];
		table[startState][0] = 1;

		// forward algorithm
		int[] scaleupCount = new int[T + 1];
		for (int t = 1; t <= T; t++) {
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					table[i][t] += table[j][t - 1]
							* hmm.getTransitionTable()[j][i]
							* hmm.getEmissionTable()[i][hmm
									.getSymbolIndex(stream.charAt(t - 1) + "")];
				}
			}
			scaleupCount[t] = scaleupCount[t - 1]
					+ HMMUtil.scaleUpTableColumn(table, t, SCALEUP_FACTOR);
		}
		return new StateObservationTable(table, scaleupCount);
	}

	@Override
	protected double getResult(StateObservationTable sotable, HMM hmm,
			String stream) {
		int T = stream.length();
		int finalState = hmm.getFinalState();
		double tableResult = sotable.getTable()[finalState][T];

		System.out.println(tableResult + "\t" + sotable.getScaleupCount()[T]);
		return Math.log(tableResult) - sotable.getScaleupCount()[T]
				* Math.log(SCALEUP_FACTOR);
	}

}
