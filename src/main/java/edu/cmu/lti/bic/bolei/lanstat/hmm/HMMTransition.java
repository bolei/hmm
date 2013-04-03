package edu.cmu.lti.bic.bolei.lanstat.hmm;

import java.util.HashMap;

public class HMMTransition {

	private HMMState fromState;
	private HMMState toState;
	private double probability;

	private static final int INIT_HASH = 123;

	private static HashMap<String, HMMTransition> transitionsFlyweight = new HashMap<String, HMMTransition>();

	private HMMTransition(HMMState fromState, HMMState toState) {
		this.fromState = fromState;
		this.toState = toState;
	}

	public HMMState getFromState() {
		return fromState;
	}

	public HMMState getToState() {
		return toState;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public static HMMTransition getTransition(HMMState fromState,
			HMMState toState) {
		String name = fromState.getStateName() + "," + toState.getStateName();
		if (transitionsFlyweight.get(name) == null) {
			transitionsFlyweight.put(name,
					new HMMTransition(fromState, toState));
		}
		return transitionsFlyweight.get(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HMMTransition == false) {
			return false;
		}
		HMMTransition other = (HMMTransition) obj;
		if (this.fromState.equals(other.fromState)
				&& this.toState.equals(other.toState)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return (INIT_HASH * 37 + fromState.hashCode()) * 37
				+ toState.hashCode();
	}

	@Override
	public String toString() {
		return fromState.getStateName() + "->" + toState.getStateName() + "\t"
				+ probability;
	}

}
