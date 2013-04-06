package edu.cmu.lti.bic.bolei.lanstat.hmm;

import edu.cmu.lti.bic.bolei.lanstat.hmm.AbstractHMMEvaluator.StateObservationTable;

public class HMMTraniner {

	public static void baumWelchReestimate(HMM hmm, String stream) {
		if (stream.endsWith(HMM.END_OF_STREAM) == false) {
			stream += HMM.END_OF_STREAM;
		}

		ForwardAlgorithmHMMEvaluator forward = new ForwardAlgorithmHMMEvaluator();
		BackwardAlgorithmHMMEvaluator backward = new BackwardAlgorithmHMMEvaluator();

		int T = stream.length();
		int N = hmm.getN();
		int V = hmm.getV();

		double oldLogLikelihood = 0, newLogLikelihood = 0;
		double newProb = 0;

		int itCount = 0;

		do {
			// if (itCount >= 100) {
			// System.err.println("break");
			// break;
			// }
			System.out.println("iteration: " + itCount);
			StateObservationTable alpha = forward.computeTable(hmm, stream);
			StateObservationTable beta = backward.computeTable(hmm, stream);

			oldLogLikelihood = newLogLikelihood;

			// newProb = alpha.getTable()[hmm.getFinalState()][T];

			newProb = alpha.getTable()[hmm.getFinalState()][T];
			double betaProb = beta.getTable()[hmm.getStartState()][0];
			if (newProb * Math.pow(10, AbstractHMMEvaluator.SCALEUP_FACTOR)
					- betaProb
					* Math.pow(10, AbstractHMMEvaluator.SCALEUP_FACTOR * 2) > 1) {
				throw new AssertionError("alphaprob=[" + newProb
						+ "], betaprob=[" + betaProb + "]");
			}
			newLogLikelihood = Math.log(newProb) - alpha.getScaleupCount()[T]
					* Math.log(AbstractHMMEvaluator.SCALEUP_FACTOR);

			System.out.println("new log likelihood:" + newLogLikelihood);
			System.out.println("difference: "
					+ (newLogLikelihood - oldLogLikelihood));

			// if (newLogLikelihood - oldLogLikelihood < 0
			// && oldLogLikelihood != 0) {
			// throw new AssertionError("smaller likelihood");
			// }

			if (newLogLikelihood - oldLogLikelihood < 1
					&& oldLogLikelihood != 0) {
				break;
			}

			// calculate xi

			double[][][] xi = calculateXi(hmm, alpha, beta, stream);
			double[][] gamma = calculateGamma(xi);

			double[] expectTranFromI = calculateExpectedNumTransitionFromStateI(gamma);
			double[] expectNumInJ = calculateExepectedNumInStateJ(gamma);
			double[][] expectTransition = calculateExpectedNumTransitionFromStateIToStateJ(xi);
			double[][] expectJEmitK = calculateExpectedNumEmissionKInStateJ(
					hmm, gamma, stream);

			// update hmm
			for (int i = 0; i < N - 1; i++) {
				for (int j = 0; j < N; j++) {
					hmm.getTransitionTable()[i][j] = expectTransition[i][j]
							/ expectTranFromI[i];
				}
			}

			for (int j = 1; j < N; j++) {
				for (int k = 0; k < V; k++) {
					hmm.getEmissionTable()[j][k] = expectJEmitK[j][k]
							/ expectNumInJ[j];
				}
			}
			itCount++;
		} while (true);

	}

	public static double[][][] calculateXi(HMM hmm,
			StateObservationTable alpha, StateObservationTable beta,
			String stream) {
		int N = hmm.getN();
		int T = stream.length() + 1;
		double[][][] xi = new double[T][N][N];
		for (int t = 0; t < T; t++) {
			// System.out.println("table xi t=" + t);
			double POLambda = 0;
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					if (t == T - 1) {
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
					if (t == T - 1) {
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

		return xi;
	}

	public static double[][] calculateGamma(double[][][] xi) {
		int T = xi.length;
		int N = xi[0].length;
		double[][] gamma = new double[T][N];
		for (int j = 0; j < N; j++) {
			for (int i = 0; i < N; i++) {
				for (int t = 0; t < T; t++) {
					gamma[t][i] += xi[t][i][j];
				}
			}
		}
		return gamma;
	}

	public static double[] calculateExpectedNumTransitionFromStateI(
			double[][] gamma) {
		int N = gamma[0].length;
		int T = gamma.length;
		double[] expectTranFromI = new double[N];
		for (int i = 0; i < N; i++) {
			for (int t = 0; t < T - 1; t++) {
				expectTranFromI[i] += gamma[t][i];
			}
		}
		return expectTranFromI;
	}

	public static double[] calculateExepectedNumInStateJ(double[][] gamma) {
		int T = gamma.length;
		int N = gamma[0].length;
		double[] expectNumInJ = new double[N];
		for (int i = 0; i < N; i++) {
			for (int t = 0; t < T; t++) {
				expectNumInJ[i] += gamma[t][i];
			}
		}
		return expectNumInJ;
	}

	public static double[][] calculateExpectedNumTransitionFromStateIToStateJ(
			double[][][] xi) {
		int T = xi.length;
		int N = xi[0].length;
		double[][] expectTransition = new double[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				for (int t = 0; t < T - 1; t++) {
					expectTransition[i][j] += xi[t][i][j];
				}
			}
		}
		return expectTransition;
	}

	private static double[][] calculateExpectedNumEmissionKInStateJ(HMM hmm,
			double[][] gamma, String stream) {
		int V = hmm.getV();
		int N = gamma[0].length;
		int T = gamma.length;

		double[][] expectJEmitK = new double[N][V];
		for (int j = 0; j < N; j++) {
			for (int k = 0; k < V; k++) {
				for (int t = 1; t < T; t++) {
					if (hmm.getSymbolIndex(stream.charAt(t - 1) + "") == k) {
						expectJEmitK[j][k] += gamma[t][j];
					}
				}
			}
		}
		return expectJEmitK;
	}
}
