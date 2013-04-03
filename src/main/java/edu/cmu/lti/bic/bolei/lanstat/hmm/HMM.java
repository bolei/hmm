package edu.cmu.lti.bic.bolei.lanstat.hmm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

public class HMM {
	private static Properties config = new Properties();

	static {
		try {
			config.load(HMMState.class
					.getResourceAsStream("/config/state.emission.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private HashMap<HMMState, Set<HMMTransition>> states = new HashMap<HMMState, Set<HMMTransition>>();

	private HMMState startState = null;
	private List<HMMState> finalStates = new LinkedList<HMMState>();

	private HMM() {
	}

	/**
	 * Assume each of the chars in the stream belongs to exactly one of the
	 * states in the HMM. There's no char in the stream that does not belong to
	 * any state nor belongs to multiple states.
	 * 
	 * @param stream
	 *            A stream of input training data
	 * 
	 * @return The start state of HMM
	 */
	public static HMM create1stNaiveHMM() {
		BufferedReader brIn = null;
		HMM hmm = new HMM();

		try {
			brIn = new BufferedReader(new InputStreamReader(
					HMMState.class.getResourceAsStream(config
							.getProperty("inputcorpus"))));
			String stream = brIn.readLine();

			// create states
			String[] stateNames = config.getProperty("states").split(",");
			for (String stateName : stateNames) {
				boolean isStartState = false;
				boolean isFinalState = false;
				if (stateName.endsWith("*")) { // start state
					stateName = stateName.substring(0, stateName.length() - 1);
					isStartState = true;
				} else if (stateName.endsWith("~")) { // end state
					stateName = stateName.substring(0, stateName.length() - 1);
					isFinalState = true;
				}
				Set<String> emissionSymbols = new HashSet<String>();
				Collections.addAll(emissionSymbols,
						config.getProperty(stateName).split(","));
				HMMState state = HMMState.getState(stateName, emissionSymbols);
				hmm.states.put(state, new HashSet<HMMTransition>());
				if (isStartState == true) {
					hmm.startState = state;
				} else if (isFinalState == true) {
					hmm.finalStates.add(state);
				}
			}
			if (hmm.startState == null || hmm.finalStates.isEmpty()) {
				System.err.println("start or final state is not defined!");
				return null;
			}

			// build transitions
			HMMState state1, state2;
			HashMap<HMMTransition, Integer> transitionCount = new HashMap<HMMTransition, Integer>();
			HashMap<String, Integer> symbolCount = new HashMap<String, Integer>();
			for (int i = 0; i < stream.length(); i++) {
				if (i < stream.length() - 1) {
					state1 = hmm.findStateByEmissionSymbol(stream.substring(i,
							i + 1));
					state2 = hmm.findStateByEmissionSymbol(stream.substring(
							i + 1, i + 2));
					if (state1 == null || state2 == null) {
						System.err
								.println("no state found for an emission symbol");
						return null;
					}
					HMMTransition transition = HMMTransition.getTransition(
							state1, state2);
					addCount(transitionCount, transition);
					hmm.addTransition(transition);
				}
				addCount(symbolCount, stream.charAt(i) + "");
			}

			// update emission probabilities
			hmm.updateEmission(symbolCount);

			// update transition probabilities
			hmm.updateTransition(transitionCount);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (brIn != null) {
				try {
					brIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				brIn = null;
			}
		}
		return hmm;
	}

	private void addTransition(HMMTransition transition) {
		HMMState state = transition.getFromState();
		Set<HMMTransition> transitions = states.get(state);
		transitions.add(transition);
		states.put(state, transitions);
	}

	private HMMState findStateByEmissionSymbol(String emissionSymbol) {
		HMMState result = null;
		for (HMMState state : states.keySet()) {
			if (state.isEmissionSymbolInState(emissionSymbol)) {
				result = state;
			}
		}
		return result;
	}

	private void updateEmission(HashMap<String, Integer> symbolCount) {
		int total;
		for (HMMState state : states.keySet()) {
			total = 0;
			for (String symbol : state.getEmissionMap().keySet()) {
				total += getCount(symbolCount, symbol);

			}
			for (String symbol : state.getEmissionMap().keySet()) {
				state.getEmissionMap().put(symbol,
						getCount(symbolCount, symbol) / ((double) total));
			}
		}
	}

	private void updateTransition(
			HashMap<HMMTransition, Integer> transitionCount) {
		int total;
		for (Entry<HMMState, Set<HMMTransition>> entry : states.entrySet()) {
			total = 0;
			for (HMMTransition transition : entry.getValue()) {
				total += getCount(transitionCount, transition);
			}
			for (HMMTransition transition : entry.getValue()) {
				transition.setProbability(getCount(transitionCount, transition)
						/ ((double) total));
			}
		}
	}

	private static <K> void addCount(Map<K, Integer> dict, K key) {
		if (dict.containsKey(key)) {
			dict.put(key, dict.get(key) + 1);
		} else {
			dict.put(key, 1);
		}
	}

	private static <K> int getCount(Map<K, Integer> dict, K key) {
		if (dict.containsKey(key) == false) {
			return 0;
		} else {
			return dict.get(key);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<HMMState, Set<HMMTransition>> entry : states.entrySet()) {
			sb.append(entry.getKey() + "\n");
			for (HMMTransition transition : entry.getValue()) {
				sb.append("\t" + transition.toString() + "\n");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

}
