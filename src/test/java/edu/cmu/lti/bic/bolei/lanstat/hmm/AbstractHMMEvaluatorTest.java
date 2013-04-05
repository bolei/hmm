package edu.cmu.lti.bic.bolei.lanstat.hmm;

import static org.junit.Assert.*;

import org.junit.Test;

public class AbstractHMMEvaluatorTest {

	@Test
	public void testScaleUpTableColumn() {
		int SCALEUP_FACTOR = 6;
		double minPos = Math.pow(1 / 10d, 5);
		System.out.println(minPos);

		System.out.println(Math.log10(minPos));
		System.out.println(Math.log10(minPos) / SCALEUP_FACTOR);

		double temp = (Math.log10(minPos) / SCALEUP_FACTOR) * (-1);

		System.out.println(temp);

		int x = (int) Math.ceil((Math.log10(minPos) / SCALEUP_FACTOR) * (-1)) - 1;
		assertEquals(0, x);
	}

}
