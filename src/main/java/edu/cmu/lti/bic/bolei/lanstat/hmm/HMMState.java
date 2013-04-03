package edu.cmu.lti.bic.bolei.lanstat.hmm;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class HMMState {
	private String stateName;
	private HashMap<String, Double> emissionMap = new HashMap<String, Double>();

	private static HashMap<String, HMMState> stateFlyWeight = new HashMap<String, HMMState>();

	private static final int INIT_HASH = 456;

	private HMMState(String name, Set<String> emissionSymbols) {
		stateName = name;
		for (String c : emissionSymbols) {
			emissionMap.put(c, 0d);
		}
	}

	public static HMMState getState(String name, Set<String> emissionSymbols) {
		if (stateFlyWeight.get(name) == null) {
			stateFlyWeight.put(name, new HMMState(name, emissionSymbols));
		}
		return stateFlyWeight.get(name);
	}

	public boolean isEmissionSymbolInState(String symbol) {
		return emissionMap.containsKey(symbol);
	}

	public String getStateName() {
		return stateName;
	}

	public HashMap<String, Double> getEmissionMap() {
		return emissionMap;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HMMState == false) {
			return false;
		}
		HMMState other = (HMMState) obj;
		return stateName.equals(other.stateName);
	}

	@Override
	public int hashCode() {
		return INIT_HASH * 37 + stateName.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("name: " + stateName + "\n");
		sb.append("transitions:\n");
		sb.append("emissions:\n");
		for (Entry<String, Double> entry : emissionMap.entrySet()) {
			sb.append("\t" + entry.getKey() + ": " + entry.getValue() + "\n");
		}
		return sb.toString();
	}
}
