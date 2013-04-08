package edu.cmu.lti.bic.bolei.lanstat.hmm;

import org.junit.Test;

public class ForwardAlgorithmHMMEvaluatorTest {

	@Test
	public void testEvaluate() {
		System.out.println("forward");
		String stream = "AAB";
		HMM hmm = HMMUtil.generateTestHMM();

		System.out.println(hmm.toString());
		ForwardAlgorithmHMMEvaluator forward = new ForwardAlgorithmHMMEvaluator();
		double[][] alphaTable = forward.computeTable(hmm, stream).getTable();
		HMMUtil.print2dArray(alphaTable);
		double actual = forward.evaluate(stream, hmm);
		// double expected = Math.log(0.130032);
		System.out.println("actual=" + actual);
		// System.out.println("expected=" + expected);
		// System.out.println(Math.abs(actual - expected));
		// Assert.assertTrue(Math.abs(actual - expected) < 0.001);
	}
}
