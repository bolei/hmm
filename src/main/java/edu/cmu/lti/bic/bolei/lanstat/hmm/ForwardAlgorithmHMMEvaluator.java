package edu.cmu.lti.bic.bolei.lanstat.hmm;

public class ForwardAlgorithmHMMEvaluator extends AbstractHMMEvaluator {

	@Override
	public StateObservationTable computeTable(HMM hmm, String stream) {
		int N = hmm.getN();
		int T = stream.length();
		int[] scaleupCount = new int[T];
		double[][] table = new double[N][T];
		for (int i = 0; i < N; i++) {
			// when t = 0
			table[i][0] = hmm.getPi()[i]
					* hmm.getEmissionTable()[i][hmm.getSymbolIndex(stream
							.charAt(0) + "")];
		}
		scaleupCount[0] = HMMUtil.scaleUpTableColumn(table, 0, SCALEUP_FACTOR);

		// forward algorithm
		for (int t = 1; t < T; t++) {
			for (int j = 0; j < N; j++) {
				for (int i = 0; i < N; i++) {
					table[j][t] += table[i][t - 1]
							* hmm.getTransitionTable()[i][j]
							* hmm.getEmissionTable()[j][hmm
									.getSymbolIndex(stream.charAt(t) + "")];
				}
			}
			scaleupCount[t] = scaleupCount[t - 1]
					+ HMMUtil.scaleUpTableColumn(table, t, SCALEUP_FACTOR);
		}
		return new StateObservationTable(table, scaleupCount);
	}

	@Override
	public TableProbResult getTableProbResult(StateObservationTable sotable,
			HMM hmm, String stream) {
		int T = stream.length() - 1;
		double tableProb = 0;
		for (int i = 0; i < hmm.getN(); i++) {
			tableProb += sotable.getTable()[i][T];
		}

		int scaleupCount = sotable.getScaleupCount()[T];
		while (tableProb * Math.pow(10, SCALEUP_FACTOR) < 1) {
			tableProb *= Math.pow(10, SCALEUP_FACTOR);
			scaleupCount++;
		}

		return new TableProbResult(tableProb, scaleupCount);
	}

}
