package edu.cmu.lti.bic.bolei.lanstat.hmm;

import edu.cmu.lti.bic.bolei.lanstat.hmm.AbstractHMMEvaluator.StateObservationTable;

public class HMMTraniner {

	public static void baumWelchReestimate(HMM hmm, String stream) {

		final double threshold = 0.0000001;

		ForwardAlgorithmHMMEvaluator forward = new ForwardAlgorithmHMMEvaluator();
		BackwardAlgorithmHMMEvaluator backward = new BackwardAlgorithmHMMEvaluator();

		double oldLogLikelihood = 0, newLogLikelihood = 0;

		int itCount = 0;

		do {
			System.out.println("iteration: " + itCount);
			// if (itCount % 50 == 1) {
			// System.out.println(hmm.toString());
			// }
			StateObservationTable alpha = forward.computeTable(hmm, stream);
			StateObservationTable beta = backward.computeTable(hmm, stream);

			oldLogLikelihood = newLogLikelihood;

			newLogLikelihood = forward.getResult(alpha, hmm, stream);
			System.out.println("average log likelihood:" + newLogLikelihood);
			if (Double.isNaN(newLogLikelihood)) {
				System.out.println(hmm.toString());
				throw new AssertionError("not a number");
			}
			System.out.println("difference: "
					+ (newLogLikelihood - oldLogLikelihood));

			try {
				if (oldLogLikelihood != 0) {
					assert newLogLikelihood - oldLogLikelihood > 0;
				}
			} catch (AssertionError ae) {
				System.out.println("iteration k+1 becomes less than k!!!");
				throw ae;
			}

			if (Math.abs(newLogLikelihood - oldLogLikelihood) < threshold
					&& oldLogLikelihood != 0) {
				System.out.println("converged!");
				break;
			}

			// calculate xi

			double[][][] xi = calculateXi(hmm, alpha, beta, stream);
			double[][] gamma = calculateGamma(alpha, beta);

			double[] expectTranFromI = calculateExpectedNumTransitionFromStateI(gamma);
			double[] expectNumInJ = calculateExepectedNumInStateJ(gamma);
			double[][] expectTransition = calculateExpectedNumTransitionFromStateIToStateJ(xi);
			double[][] expectJEmitK = calculateExpectedNumEmissionKInStateJ(
					hmm, gamma, stream);

			hmm.setTransitionTable(getNextTransitionTable(expectTransition,
					expectTranFromI));

			hmm.setEmissionTable(getNextEmissionTable(expectJEmitK,
					expectNumInJ));

			hmm.setPi(getNextPi(gamma));
			itCount++;
		} while (true);

	}

	private static double[] getNextPi(double[][] gamma) {
		int N = gamma[0].length;
		double[] pi = new double[N];
		for (int i = 0; i < N; i++) {
			pi[i] = gamma[0][i]
					* Math.pow(10, 2 * AbstractHMMEvaluator.SCALEUP_FACTOR) > 1 ? gamma[0][i]
					: 0;
		}
		return pi;
	}

	public static double[][] getNextTransitionTable(
			double[][] expectTransitionFromIToJ, double[] expectTranFromI) {
		int N = expectTransitionFromIToJ.length;
		double[][] newTransition = new double[N][N];

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				newTransition[i][j] = expectTransitionFromIToJ[i][j]
						/ expectTranFromI[i];
			}
		}

		return newTransition;
	}

	public static double[][] getNextEmissionTable(double[][] expectJEmitK,
			double[] expectNumInJ) {
		int N = expectNumInJ.length;
		int V = expectJEmitK[0].length;
		double[][] newEmissionTable = new double[N][V];
		for (int j = 0; j < N; j++) {
			for (int k = 0; k < V; k++) {
				newEmissionTable[j][k] = expectJEmitK[j][k] / expectNumInJ[j];
			}
		}
		return newEmissionTable;
	}

	public static double[][][] calculateXi(HMM hmm,
			StateObservationTable alpha, StateObservationTable beta,
			String stream) {
		int N = hmm.getN();
		int T = stream.length();
		double[][][] xi = new double[T - 1][N][N];
		for (int t = 0; t < T - 1; t++) {
			double POLambda = 0;
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					POLambda += alpha.getTable()[i][t]
							* hmm.getTransitionTable()[i][j]
							* hmm.getEmissionTable()[j][hmm
									.getSymbolIndex(stream.charAt(t + 1) + "")]
							* beta.getTable()[j][t + 1];
				}
			}
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					// scale up factor is compensated
					xi[t][i][j] = alpha.getTable()[i][t]
							* hmm.getTransitionTable()[i][j]
							* hmm.getEmissionTable()[j][hmm
									.getSymbolIndex(stream.charAt(t + 1) + "")]
							* beta.getTable()[j][t + 1] / POLambda;
				}
			}
		}

		// when t = T-1
		// double POLambda = 0;
		// for (int i = 0; i < N; i++) {
		// // POLambda += alpha.getTable()[i][T - 1] * hmm.getEta()[i] * N;
		// POLambda += alpha.getTable()[i][T - 1] * N;
		// }
		//
		// for (int i = 0; i < N; i++) {
		// for (int j = 0; j < N; j++) {
		// // xi[T - 1][i][j] = alpha.getTable()[i][T - 1] *
		// // hmm.getEta()[i]
		// // / POLambda;
		// xi[T - 1][i][j] = alpha.getTable()[i][T - 1] / POLambda;
		// }
		// }

		return xi;
	}

	public static double[][] calculateGamma(StateObservationTable alpha,
			StateObservationTable beta) {
		int T = alpha.getTable()[0].length;
		int N = alpha.getTable().length;
		double[][] gamma = new double[T][N];
		double[] POLambdaT = new double[T];
		for (int t = 0; t < T; t++) {
			for (int i = 0; i < N; i++) {
				POLambdaT[t] += alpha.getTable()[i][t] * beta.getTable()[i][t];
			}
		}
		for (int t = 0; t < T - 1; t++) {
			for (int i = 0; i < N; i++) {
				gamma[t][i] = alpha.getTable()[i][t] * beta.getTable()[i][t]
						/ POLambdaT[t];
			}
		}
		return gamma;
	}

	public static double[] calculateExpectedNumTransitionFromStateI(
			double[][] gamma) {
		int T = gamma.length;
		int N = gamma[0].length;
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

	public static double[][] calculateExpectedNumEmissionKInStateJ(HMM hmm,
			double[][] gamma, String stream) {
		int V = hmm.getV();
		int N = gamma[0].length;
		int T = gamma.length;

		double[][] expectJEmitK = new double[N][V];

		for (int t = 0; t < T; t++) {
			for (int j = 0; j < N; j++) {
				expectJEmitK[j][hmm.getSymbolIndex(stream.charAt(t) + "")] += gamma[t][j];
			}
		}

		return expectJEmitK;
	}
}
