package edu.cmu.lti.bic.bolei.lanstat.hmm;

import java.util.ArrayList;
import java.util.Collections;

import junit.framework.Assert;

import org.junit.Test;

public class ForwardAlgorithmHMMEvaluatorTest {

	@Test
	public void testEvaluate() {
		int startState = 0;
		int finalState = 3;
		ArrayList<String> vocabulary = new ArrayList<String>();
		Collections.addAll(vocabulary, new String[] { "A", "B", "#" });
		String stream = "AAB#";
		double[][] transitionTable = { { 0.0, 1.0, 0.0, 0 },
				{ 0, 0.6d, 0.4d, 0 }, { 0d, 0, 0.8, 0.2 }, { 0, 0, 0, 0 } };
		double[][] emissionTable = { { 0, 0, 0 }, { 0.8d, 0.2d, 0 },
				{ 0.3d, 0.7d, 0 }, { 0, 0, 1 } };

		HMM hmm = new HMM(startState, finalState, transitionTable,
				emissionTable, vocabulary);
		System.out.println(hmm.toString());
		ForwardAlgorithmHMMEvaluator forward = new ForwardAlgorithmHMMEvaluator();
		double actual = forward.evaluate(stream, hmm);
		double expected = Math.log(0.032256);
		System.out.println(actual);
		System.out.println(Math.abs(actual - expected));
		Assert.assertTrue(Math.abs(actual - expected) < 0.001);
	}
}
