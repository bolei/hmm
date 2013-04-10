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
		int N = stateCount;
		ArrayList<String> vocabulary = new ArrayList<String>();
		for (int i = 0; i < stateCount; i++) {
			Collections.addAll(vocabulary, HMMUtil.getConfiguration()
					.getProperty(i + "").split(","));
		}
		int V = vocabulary.size();

		double[][] transitionTable = HMMUtil.generateRandomTransitionTable(N);
		double[][] emissionTable = HMMUtil.generateRandomEmissionTable(N, V);
		double[] pi = HMMUtil.generateConstantSumRandomArray(1, N);
		HMM hmm = new HMM(transitionTable, emissionTable, pi, vocabulary);

		System.out.println("random init HMM:");
		System.out.println(hmm.toString());

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
