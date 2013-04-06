package edu.cmu.lti.bic.bolei.lanstat.hmm;

public abstract class AbstractHMMEvaluator {
	// alpha (forward) table or beta (backward) table
	protected static final int SCALEUP_FACTOR = 6;

	public double evaluate(String stream, HMM hmm) {
		// init table

		if (stream.endsWith(HMM.END_OF_STREAM) == false) {
			stream += HMM.END_OF_STREAM;
		}
		StateObservationTable sotable = computeTable(hmm, stream);
		double result = getResult(sotable, hmm, stream);
		return result;
	}

	public abstract StateObservationTable computeTable(HMM hmm, String stream);

	protected abstract double getResult(StateObservationTable sotable, HMM hmm,
			String stream);

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

}
