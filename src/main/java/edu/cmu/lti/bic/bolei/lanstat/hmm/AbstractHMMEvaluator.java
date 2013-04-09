package edu.cmu.lti.bic.bolei.lanstat.hmm;

public abstract class AbstractHMMEvaluator {
	// alpha (forward) table or beta (backward) table
	protected static final int SCALEUP_FACTOR = 6;

	public double evaluate(String stream, HMM hmm) {
		// init table

		StateObservationTable sotable = computeTable(hmm, stream);
		double result = getResult(sotable, hmm, stream);
		return result;
	}

	public abstract StateObservationTable computeTable(HMM hmm, String stream);

	public double getResult(StateObservationTable sotable, HMM hmm,
			String stream) {

		TableProbResult tableProbResult = getTableProbResult(sotable, hmm,
				stream);

		double tableResult = tableProbResult.getTableProb();

		int scaleupCount = tableProbResult.getScaleupCount();

		System.out.println("prob:\t" + tableResult + "\t" + scaleupCount);
		return (Math.log10(tableResult) - scaleupCount * SCALEUP_FACTOR)
				/ (double) stream.length();
	}

	public abstract TableProbResult getTableProbResult(
			StateObservationTable sotable, HMM hmm, String stream);

	protected class StateObservationTable {
		double[][] table;
		int[] scaleupCount;

		StateObservationTable(double[][] table, int[] scaleupCount) {
			this.table = table;
			this.scaleupCount = scaleupCount;
		}

		public double[][] getTable() {
			return table;
		}

		public int[] getScaleupCount() {
			return scaleupCount;
		}
	}

	protected class TableProbResult {
		double tableProb;
		int scaleupCount;

		TableProbResult(double tableProb, int scaleupCount) {
			this.tableProb = tableProb;
			this.scaleupCount = scaleupCount;
		}

		public double getTableProb() {
			return tableProb;
		}

		public int getScaleupCount() {
			return scaleupCount;
		}

	}

}
