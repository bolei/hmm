package edu.cmu.lti.bic.bolei.lanstat.hmm;

import junit.framework.Assert;

import org.junit.Test;

public class HMMUtilTest {

	@Test
	public void testGenerateRandomTransitionTable() {
		int N = 4;
		double[][] table = HMMUtil.generateRandomTransitionTable(N);
		HMMUtil.print2dArray(table);
		double sum = 0;
		for (int j = 0; j < N; j++) {
			sum += table[1][j];
		}
		Assert.assertEquals(1.0d, sum);
	}

	@Test
	public void testGenerateRandomEmissionTable() {
		int N = 4;
		int V = 3;
		double[][] table = HMMUtil.generateRandomEmissionTable(N, V);
		HMMUtil.print2dArray(table);
		double sum = 0;
		for (int j = 0; j < V; j++) {
			sum += table[1][j];
		}
		Assert.assertEquals(1.0d, sum);
	}

	@Test
	public void testScaleUpTableColumn() {
		double[][] table = { { Math.pow(10, 0) }, { Math.pow(10, 1) },
				{ Math.pow(10, 1) }, { Math.pow(10, 1) },
				{ Math.pow(10, 1) } };
		int x = HMMUtil.scaleUpTableColumn(table, 0, 1);
		HMMUtil.print2dArray(table);
		Assert.assertEquals(0, x);
	}
}
