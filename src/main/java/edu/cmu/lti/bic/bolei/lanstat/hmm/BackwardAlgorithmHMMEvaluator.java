package edu.cmu.lti.bic.bolei.lanstat.hmm;

public class BackwardAlgorithmHMMEvaluator extends AbstractHMMEvaluator {

	@Override
	public StateObservationTable computeTable(HMM hmm, String stream) {
		// backward algorithm
		int N = hmm.getN();
		int T = stream.length();
		double[][] table = new double[N][T];
		int[] scaleupCount = new int[T];

		// when t= T-1
		for (int i = 0; i < N; i++) {
			table[i][T - 1] =1;
		}
		scaleupCount[T - 1] = HMMUtil.scaleUpTableColumn(table, T - 1,
				SCALEUP_FACTOR);

		for (int t = T - 2; t >= 0; t--) {
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					table[i][t] += table[j][t + 1]
							* hmm.getTransitionTable()[i][j]
							* hmm.getEmissionTable()[j][hmm
									.getSymbolIndex(stream.charAt(t + 1) + "")];
				}
			}
			// scaleupCount += scaleUpTableColumn(table, t);
			scaleupCount[t] = scaleupCount[t + 1]
					+ HMMUtil.scaleUpTableColumn(table, t, SCALEUP_FACTOR);
		}

		return new StateObservationTable(table, scaleupCount);
	}

	@Override
	public TableProbResult getTableProbResult(StateObservationTable sotable,
			HMM hmm, String stream) {
		int N = hmm.getN();
		int t = 0;
		double tableProb = 0;
		for (int i = 0; i < N; i++) {
			tableProb += hmm.getPi()[i]
					* hmm.getEmissionTable()[i][hmm.getSymbolIndex(stream
							.charAt(t) + "")] * sotable.getTable()[i][t];
		}

		int scaleupCount = sotable.getScaleupCount()[0];
		while (tableProb * Math.pow(10, SCALEUP_FACTOR) < 1) {
			tableProb *= Math.pow(10, SCALEUP_FACTOR);
			scaleupCount++;
		}

		return new TableProbResult(tableProb, scaleupCount);
	}

}
