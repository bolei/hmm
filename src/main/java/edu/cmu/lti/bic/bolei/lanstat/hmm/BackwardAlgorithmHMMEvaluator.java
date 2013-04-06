package edu.cmu.lti.bic.bolei.lanstat.hmm;

public class BackwardAlgorithmHMMEvaluator extends AbstractHMMEvaluator {

	@Override
	public StateObservationTable computeTable(HMM hmm, String stream) {
		// backward algorithm
		int N = hmm.getN();
		int T = stream.length();
		double[][] table = new double[N][T + 1];
		int finalState = hmm.getFinalState();
		table[finalState][T] = 1;
		int[] scaleupCount = new int[T + 1];
		for (int t = T - 1; t >= 0; t--) {
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					table[i][t] += table[j][t + 1]
							* hmm.getTransitionTable()[i][j]
							* hmm.getEmissionTable()[j][hmm
									.getSymbolIndex(stream.charAt(t) + "")];
				}
			}
			// scaleupCount += scaleUpTableColumn(table, t);
			scaleupCount[t] = scaleupCount[t + 1]
					+ HMMUtil.scaleUpTableColumn(table, t, SCALEUP_FACTOR);
		}

		return new StateObservationTable(table, scaleupCount);
	}

	@Override
	protected double getResult(StateObservationTable sotable, HMM hmm,
			String stream) {
		int startState = hmm.getStartState();
		double tableResult = sotable.getTable()[startState][0];

		System.out.println(tableResult + "\t" + sotable.getScaleupCount()[0]);
		return Math.log(tableResult) - sotable.getScaleupCount()[0]
				* Math.log(SCALEUP_FACTOR);
	}

}
