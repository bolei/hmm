package edu.cmu.lti.bic.bolei.lanstat.hmm;

import org.junit.Test;

import edu.cmu.lti.bic.bolei.lanstat.hmm.AbstractHMMEvaluator.StateObservationTable;

public class HMMTraninerTest {

	HMM hmm = HMMUtil.generateTestHMM();

	private String stream = "AABB";

	@Test
	public void testCalculateXi() {
		System.out.println("xi");
		StateObservationTable alpha = new ForwardAlgorithmHMMEvaluator()
				.computeTable(hmm, stream);
		StateObservationTable beta = new BackwardAlgorithmHMMEvaluator()
				.computeTable(hmm, stream);

		double[][][] xi = HMMTraniner.calculateXi(hmm, alpha, beta, stream);
		for (int t = 0; t < stream.length() - 1; t++) {
			System.out.println("t=" + t);
			HMMUtil.print2dArray(xi[t]);
		}
	}

	@Test
	public void testCalculateGamma() {
		System.out.println("gamma!");
		StateObservationTable alpha = new ForwardAlgorithmHMMEvaluator()
				.computeTable(hmm, stream);
		StateObservationTable beta = new BackwardAlgorithmHMMEvaluator()
				.computeTable(hmm, stream);
		double[][] gamma = HMMTraniner.calculateGamma(alpha, beta);
		HMMUtil.print2dArray(gamma);
	}

	@Test
	public void testCalculateExpectedNumEmissionKInStateJ() {
		System.out.println("expectJEmitK");
		StateObservationTable alpha = new ForwardAlgorithmHMMEvaluator()
				.computeTable(hmm, stream);
		StateObservationTable beta = new BackwardAlgorithmHMMEvaluator()
				.computeTable(hmm, stream);
		double[][] gamma = HMMTraniner.calculateGamma(alpha, beta);
		double[][] expectJEmitK = HMMTraniner
				.calculateExpectedNumEmissionKInStateJ(hmm, gamma, stream);
		System.out.println("expectJEmitK:");
		HMMUtil.print2dArray(expectJEmitK);
	}

	@Test
	public void testGetNextEmissionTable() {
		System.out.println("nextEmissionTable");
		StateObservationTable alpha = new ForwardAlgorithmHMMEvaluator()
				.computeTable(hmm, stream);
		StateObservationTable beta = new BackwardAlgorithmHMMEvaluator()
				.computeTable(hmm, stream);
		double[][] gamma = HMMTraniner.calculateGamma(alpha, beta);
		double[][] expectJEmitK = HMMTraniner
				.calculateExpectedNumEmissionKInStateJ(hmm, gamma, stream);
		double[] expectNumInJ = HMMTraniner
				.calculateExepectedNumInStateJ(gamma);
		double[][] nextEmissionTable = HMMTraniner.getNextEmissionTable(
				expectJEmitK, expectNumInJ);

		System.out.println("next Emission Table:");
		HMMUtil.print2dArray(nextEmissionTable);
	}

}
