package edu.cmu.lti.bic.bolei.lanstat.hmm;

import java.util.ArrayList;
import java.util.Collections;

import junit.framework.Assert;

import org.junit.Test;

public class ForwardAlgorithmHMMEvaluatorTest {

	@Test
	public void testEvaluate() {
		int startState = 0;
		ArrayList<String> vocabulary = new ArrayList<String>();
		Collections.addAll(vocabulary, new String[] { "A", "B" });
		String stream = "AAB";
		double[][] transitionTable = { { 0.6d, 0.4d }, { 0d, 1.0d } };
		double[][] emissionTable = { { 0.8d, 0.2d }, { 0.3d, 0.7d } };

		HMM hmm = new HMM(startState, -1, transitionTable, emissionTable,
				vocabulary);
		System.out.println(hmm.toString());
		ForwardAlgorithmHMMEvaluator forward = new ForwardAlgorithmHMMEvaluator(
				hmm);
		double actual = forward.evaluate(stream);
		double expected = Math.log(0.13);
		System.out.println(Math.abs(actual - expected));
		Assert.assertTrue(Math.abs(actual - expected) < 0.001);

	}
}
