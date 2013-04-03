package edu.cmu.lti.bic.bolei.lanstat.hmm;

public class DoMain {
	public static void main(String[] args) {
		HMM hmm = HMM.create1stNaiveHMM();
		System.out.println(hmm.toString());
	}
}
