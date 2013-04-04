package edu.cmu.lti.bic.bolei.lanstat.hmm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Properties;

public class HMM {
	private static final Properties CONFIG = new Properties();

	static {
		try {
			CONFIG.load(HMM.class
					.getResourceAsStream("/config/state.emission.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private double[][] a; // transition table;
	private double[][] b; // emission table

	private int N; // number of states
	private int V = 0; // size of vocabulary

	private int startState;
	private int finalState;

	private ArrayList<String> vocabulary = new ArrayList<String>();

	private HMM() {
	}

	public HMM(int startState, int finalState, double[][] transitionTable,
			double[][] emissionTable, ArrayList<String> vocabulary) {
		this.startState = startState;
		this.finalState = finalState;
		this.a = transitionTable;
		this.b = emissionTable;
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

	public int getStartState() {
		return startState;
	}

	public int getFinalState() {
		return finalState;
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

		LinkedList<HashSet<String>> stateSymbols = new LinkedList<HashSet<String>>();

		// create states
		hmm.N = Integer.parseInt(CONFIG.getProperty("stateNum"));
		for (int i = 0; i < hmm.N; i++) {
			HashSet<String> symbols = new HashSet<String>();
			Collections.addAll(symbols, CONFIG.getProperty(i + "").split(","));
			stateSymbols.add(symbols);
			hmm.V += symbols.size();
			hmm.vocabulary.addAll(symbols);
		}

		hmm.a = new double[hmm.N][hmm.N];
		hmm.b = new double[hmm.N][hmm.V];

		hmm.startState = findStateIndexOfSymbol(stateSymbols, stream.charAt(0)
				+ "");

		hmm.finalState = findStateIndexOfSymbol(stateSymbols,
				stream.charAt(stream.length() - 1) + "");

		// build transitions
		for (int i = 0; i < stream.length(); i++) {
			int currentState = findStateIndexOfSymbol(stateSymbols,
					stream.charAt(i) + "");
			if (currentState < 0) {
				System.err
						.println("no current state found for an emission symbol");
				return null;
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
			LinkedList<HashSet<String>> stateSymbols, String symbol) {
		for (int i = 0; i < stateSymbols.size(); i++) {
			if (stateSymbols.get(i).contains(symbol)) {
				return i;
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

			for (int j = 0; j < columnNum; j++) {
				table[i][j] /= (double) rowTotal;
			}
		}
	}

	public static String arrayToString(double[][] table, int rowNum) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rowNum; i++) {
			sb.append(Arrays.toString(table[i]) + "\n");
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("vocabulary:\n");
		sb.append(vocabulary + "\n");
		sb.append("transition table: \n");
		sb.append(arrayToString(a, N));
		sb.append("emission table: \n");
		sb.append(arrayToString(b, N));
		return sb.toString();
	}

	public int getSymbolIndex(String symbol) {
		return vocabulary.indexOf(symbol);
	}

}
