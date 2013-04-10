package edu.cmu.lti.bic.bolei.lanstat.hmm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class HMM {

	// public static final String END_OF_STREAM = "#";
	private double[][] a; // transition table;
	private double[][] b; // emission table
	private double[] pi; // initial state distribution

	private int N; // number of states
	private int V = 0; // size of vocabulary

	private ArrayList<String> vocabulary = new ArrayList<String>();

	private HMM() {
	}

	public HMM(double[][] transitionTable, double[][] emissionTable,
			double[] pi, ArrayList<String> vocabulary) {
		this.a = transitionTable;
		this.b = emissionTable;
		this.pi = pi;
		this.N = a.length;
		this.V = vocabulary.size();
		this.vocabulary = vocabulary;
	}

	public double[][] getTransitionTable() {
		return a;
	}

	public double[][] getEmissionTable() {
		return b;
	}

	public int getN() {
		return N;
	}

	public int getV() {
		return V;
	}

	public double[] getPi() {
		return pi;
	}

	public void setPi(double[] pi) {
		this.pi = pi;
	}

	public void setTransitionTable(double[][] transitionTable) {
		this.a = transitionTable;
	}

	public void setEmissionTable(double[][] emissionTable) {
		this.b = emissionTable;
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
	public static HMM create1stOrderSimpleHMM(String stream) {

		HMM hmm = new HMM();

		HashMap<Integer, HashSet<String>> stateSymbols = new HashMap<Integer, HashSet<String>>();

		// create states, plus start state and final state
		hmm.N = Integer.parseInt(HMMUtil.getConfiguration().getProperty(
				"stateNum"));
		for (int i = 0; i < hmm.N; i++) {
			HashSet<String> symbols = new HashSet<String>();
			Collections.addAll(symbols,
					HMMUtil.getConfiguration().getProperty(i + "").split(","));
			stateSymbols.put(i, symbols);
			hmm.vocabulary.addAll(symbols);
		}

		hmm.V = hmm.vocabulary.size();
		hmm.a = new double[hmm.N][hmm.N];
		hmm.b = new double[hmm.N][hmm.V];
		hmm.pi = new double[hmm.N];

		// build transitions
		for (int i = 0; i < stream.length(); i++) {
			int currentState = findStateIndexOfSymbol(stateSymbols,
					stream.charAt(i) + "");
			if (currentState < 0) {
				System.err
						.println("no current state found for an emission symbol");
				return null;
			}
			if (i == 0) {
				hmm.pi[currentState] = 1;
			}
			if (i < stream.length() - 1) {
				int nextState = findStateIndexOfSymbol(stateSymbols,
						stream.charAt(i + 1) + "");
				if (nextState < 0) {
					System.err
							.println("no next state found for an emission symbol");
					return null;
				}
				hmm.a[currentState][nextState] += 1;
			}
			hmm.b[currentState][hmm.vocabulary.indexOf(stream.charAt(i) + "")] += 1;
		}
		updateProbability(hmm.a, hmm.N, hmm.N);
		updateProbability(hmm.b, hmm.N, hmm.V);

		return hmm;
	}

	private static int findStateIndexOfSymbol(
			HashMap<Integer, HashSet<String>> stateSymbols, String symbol) {
		for (Entry<Integer, HashSet<String>> entry : stateSymbols.entrySet()) {
			if (entry.getValue().contains(symbol)) {
				return entry.getKey();
			}
		}
		return -1;
	}

	private static void updateProbability(double[][] table, int rowNum,
			int columnNum) {
		for (int i = 0; i < rowNum; i++) {
			int rowTotal = 0;
			for (int j = 0; j < columnNum; j++) {
				rowTotal += table[i][j];
			}
			if (rowTotal == 0) {
				continue;
			}
			for (int j = 0; j < columnNum; j++) {
				table[i][j] /= (double) rowTotal;
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("vocabulary:\n");
		sb.append(vocabulary + "\n");
		sb.append("V=" + V + "\n");
		sb.append("N=" + N + "\n");
		sb.append("transition table: \n");
		sb.append(HMMUtil.get2dArrayString(a));
		sb.append("emission table: \n");
		sb.append(HMMUtil.get2dArrayString(b));
		sb.append("pi:\n");
		sb.append(Arrays.toString(pi) + "\n");
		return sb.toString();
	}

	public int getSymbolIndex(String symbol) {
		return vocabulary.indexOf(symbol);
	}

}
