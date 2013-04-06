package edu.cmu.lti.bic.bolei.lanstat.hmm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

public class DoQuestion4 {
	public static void main(String[] args) {

		int stateCount = Integer.parseInt(HMMUtil.getConfiguration()
				.getProperty("stateNum"));
		int N = stateCount + 2;
		int startState = 0;
		int finalState = N - 1;
		ArrayList<String> vocabulary = new ArrayList<String>();
		for (int i = 1; i <= stateCount; i++) {
			Collections.addAll(vocabulary, HMMUtil.getConfiguration()
					.getProperty(i + "").split(","));
		}
		vocabulary.add(HMM.END_OF_STREAM);
		int V = vocabulary.size();

		double[][] transitionTable = HMMUtil.generateRandomTransitionTable(N);
		double[][] emissionTable = HMMUtil.generateRandomEmissionTable(N, V);

		HMM hmm = new HMM(startState, finalState, transitionTable,
				emissionTable, vocabulary);

		Properties config = new Properties();
		BufferedReader brIn = null;
		try {
			config.load(DoQuestion4.class
					.getResourceAsStream("/config/global.properties"));
			brIn = new BufferedReader(new InputStreamReader(
					HMM.class.getResourceAsStream(config
							.getProperty("inputcorpus"))));
			String stream = brIn.readLine();
			HMMTraniner.baumWelchReestimate(hmm, stream);
			System.out.println(hmm.toString());
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
	}
}
