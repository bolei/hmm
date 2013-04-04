package edu.cmu.lti.bic.bolei.lanstat.hmm;

public abstract class AbstractHMMEvaluator {
	// alpha (forward) table or beta (backward) table

	private static final double M_LN2 = 0.69314718055994530942;

	protected static final int SCALEUP_FACTOR = 1000000;

	protected HMM hmm;
	protected double[][] table;

	AbstractHMMEvaluator(HMM hmm) {
		this.hmm = hmm;

	}

	public abstract double evaluate(String stream);

	/**
	 * Evaluate log(exp(left) + exp(right)) more accurately. log(exp(left) +
	 * exp(right)) = log(exp(left)) + log(1 + exp(right) / exp(left)) = left +
	 * log(1 + exp(right - left)) Note: log1p(x) accurately computes log(1+x)
	 * for small x.
	 */
	protected double logAdd(double left, double right) {
		if (right < left) {
			return left + Math.log1p(Math.exp(right - left));
		} else if (right > left) {
			return right + Math.log1p(Math.exp(left - right));
		} else {
			return left + M_LN2;
		}
	}
}
