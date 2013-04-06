package edu.cmu.lti.bic.bolei.lanstat.hmm;

import edu.cmu.lti.bic.bolei.lanstat.hmm.AbstractHMMEvaluator.StateObservationTable;

public class HMMTraniner {
	private double xi[][][];

	public void baumWelchReestimate(HMM hmm, String stream) {
		if (stream.endsWith(HMM.END_OF_STREAM) == false) {
			stream += HMM.END_OF_STREAM;
		}

		ForwardAlgorithmHMMEvaluator forward = new ForwardAlgorithmHMMEvaluator();
		BackwardAlgorithmHMMEvaluator backward = new BackwardAlgorithmHMMEvaluator();

		int T = stream.length();
		int N = hmm.getN();
		int V = hmm.getV();
		xi = new double[T + 1][N][N];

		double oldProb, newProb = 0;
		int itCount = 0;

		do {
			// if (itCount >= 100) {
			// System.err.println("break");
			// break;
			// }
			System.out.println("iteration: " + itCount);
			StateObservationTable alpha = forward.computeTable(hmm, stream);
			StateObservationTable beta = backward.computeTable(hmm, stream);
			oldProb = newProb;

			newProb = alpha.getTable()[hmm.getFinalState()][T];
			System.out.println("new loglikelihood: " + newProb);

			if (Math.abs(oldProb - newProb)
					* Math.pow(10, AbstractHMMEvaluator.SCALEUP_FACTOR) < 1) {
				break;
			}

			// calculate xi

			// System.out.println("transition table:");
			// print2dArray(hmm.getTransitionTable());
			// System.out.println("emission table");
			// print2dArray(hmm.getEmissionTable());
			// System.out.println("alpha table");
			// print2dArray(alpha.getTable());
			// System.out.println("beta table");
			// print2dArray(beta.getTable());
			// System.out.println("T=" + T);
			for (int t = 0; t <= T; t++) {
				// System.out.println("table xi t=" + t);
				double POLambda = 0;
				for (int i = 0; i < N; i++) {
					for (int j = 0; j < N; j++) {
						if (t == T) {
							POLambda += alpha.getTable()[i][t];
							continue;
						}
						POLambda += alpha.getTable()[i][t]
								* hmm.getTransitionTable()[i][j]
								* hmm.getEmissionTable()[j][hmm
										.getSymbolIndex(stream.charAt(t) + "")]
								* beta.getTable()[j][t + 1];
					}
				}
				for (int i = 0; i < N; i++) {
					for (int j = 0; j < N; j++) {
						if (t == T) {
							xi[t][i][j] = alpha.getTable()[i][t] / POLambda;
							continue;
						}
						// scale up factor is compensated
						xi[t][i][j] = alpha.getTable()[i][t]
								* hmm.getTransitionTable()[i][j]
								* hmm.getEmissionTable()[j][hmm
										.getSymbolIndex(stream.charAt(t) + "")]
								* beta.getTable()[j][t + 1] / POLambda;
					}
				}
				// print2dArray(xi[t]);
			}

			double[][] gamma = new double[T + 1][N];
			for (int j = 0; j < N; j++) {
				for (int i = 0; i < N; i++) {
					for (int t = 0; t <= T; t++) {
						gamma[t][i] += xi[t][i][j];
					}
				}
			}

			// System.out.println("gamma table:");
			// print2dArray(gamma);

			double[] expectTranFromI = new double[N];
			for (int i = 0; i < N; i++) {
				for (int t = 0; t < T; t++) {
					expectTranFromI[i] += gamma[t][i];
				}
			}

			double[] expectNumInJ = new double[N];
			for (int i = 0; i < N; i++) {
				for (int t = 0; t < T + 1; t++) {
					expectNumInJ[i] += gamma[t][i];
				}
			}

			double[][] expectTransition = new double[N][N];
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					for (int t = 0; t < T; t++) {
						expectTransition[i][j] += xi[t][i][j];
					}
				}
			}

			double[][] expectJEmitK = new double[N][V];
			for (int j = 0; j < N; j++) {
				for (int k = 0; k < V; k++) {
					for (int t = 1; t < T + 1; t++) {
						if (hmm.getSymbolIndex(stream.charAt(t - 1) + "") == k) {
							expectJEmitK[j][k] += gamma[t][j];
						}
					}
				}
			}

			// update hmm
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					if (expectTranFromI[i] == 0) {
						hmm.getTransitionTable()[i][j] = 0;
					} else {
						hmm.getTransitionTable()[i][j] = expectTransition[i][j]
								/ expectTranFromI[i];
					}
				}
			}

			for (int j = 0; j < N; j++) {
				for (int k = 0; k < V; k++) {
					if (expectNumInJ[j] == 0) {
						hmm.getEmissionTable()[j][k] = 0;
					} else {
						hmm.getEmissionTable()[j][k] = expectJEmitK[j][k]
								/ expectNumInJ[j];
					}
				}
			}
			itCount++;
		} while (true);

	}

}
