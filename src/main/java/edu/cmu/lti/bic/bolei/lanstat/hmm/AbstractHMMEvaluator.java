package edu.cmu.lti.bic.bolei.lanstat.hmm;

public abstract class AbstractHMMEvaluator {
	// alpha (forward) table or beta (backward) table

	protected HMM hmm;
	protected double[][] table;


	AbstractHMMEvaluator(HMM hmm) {
		this.hmm = hmm;

	}

	public abstract double evaluate(String stream);
}
