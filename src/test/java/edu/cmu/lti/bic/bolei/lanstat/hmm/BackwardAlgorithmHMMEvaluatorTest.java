package edu.cmu.lti.bic.bolei.lanstat.hmm;

import org.junit.Test;

public class BackwardAlgorithmHMMEvaluatorTest {

	@Test
	public void testEvaluate() {
		System.out.println("backward");
		String stream = "AAB";
		HMM hmm = HMMUtil.generateTestHMM();

		System.out.println(hmm.toString());
		BackwardAlgorithmHMMEvaluator backword = new BackwardAlgorithmHMMEvaluator();

		double[][] betaTable = backword.computeTable(hmm, stream).getTable();
		HMMUtil.print2dArray(betaTable);

		double actual = backword.evaluate(stream, hmm);
		// double expected = Math.log(0.032256);
		System.out.println(actual);
		// System.out.println(Math.abs(actual - expected));
		// Assert.assertTrue(Math.abs(actual - expected) < 0.001);
	}
}
