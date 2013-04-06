package edu.cmu.lti.bic.bolei.lanstat.hmm;

import org.junit.Test;
import junit.framework.Assert;

public class HMMTest {

	@Test
	public void testCreate1stOrderSimpleHMM() {
		String stream = "ABCDEFGHIJKLMNOPQRSTUVWXYZ #";
		HMM hmm = HMM.create1stOrderSimpleHMM(stream);
		Assert.assertEquals(1.0d, hmm.getTransitionTable()[1][2]);
		Assert.assertEquals(15 / 21d, hmm.getTransitionTable()[2][2]);
		System.out.println(hmm);
	}

}
