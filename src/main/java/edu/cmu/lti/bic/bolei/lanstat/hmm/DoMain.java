package edu.cmu.lti.bic.bolei.lanstat.hmm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class DoMain {
	public static void main(String[] args) {
		Properties config = new Properties();
		BufferedReader brIn = null;
		try {
			config.load(HMM.class
					.getResourceAsStream("/config/global.properties"));
			brIn = new BufferedReader(new InputStreamReader(
					HMM.class.getResourceAsStream(config
							.getProperty("inputcorpus"))));
			String stream = brIn.readLine();
			HMM hmm = HMM.create1stOrderSimpleHMM(stream);
			// System.out.println(hmm.toString());

			AbstractHMMEvaluator evaluator = new ForwardAlgorithmHMMEvaluator(
					hmm);
			System.out.println("forward algorithm:");
			System.out.println(evaluator.evaluate(stream));

			evaluator = new BackwardAlgorithmHMMEvaluator(hmm);
			System.out.println("backward algorithm:");
			System.out.println(evaluator.evaluate(stream));
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
