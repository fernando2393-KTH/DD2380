
// Class to hold HMM
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.lang.Math;

public class HMM {
    // PUBLIC
    double[][] A;
    double[][] B;
    double[][] pi;
    int states;
    int emissions;

    public final int ITERATION_LIMIT = 1000;
    public final double LOG_NON_IMPROVEMENT = 0.01;

    // Reads values from console and populates A, B, pi
    public void read_hmm(BufferedReader reader) {
        A = matrixOps.read_matrix(reader);
        B = matrixOps.read_matrix(reader);
        pi = matrixOps.read_matrix(reader);
        states = A.length;
        emissions = B[0].length;
    }

    public void assignValues(double[][] a, double[][] b, double[][] p) {
        A = a;
        B = b;
        pi = p;
        states = A.length;
        emissions = B[0].length;
    }

    // Starts h,, with random values
    public void randomInit(int st, int emi) {
        states = st;
        emissions = emi;
        A = matrixOps.randomMatrix(states, states, true);
        B = matrixOps.randomMatrix(states, emissions, true);
        pi = matrixOps.randomMatrix(1, states, false);
    }

    // Reads values from console and populates A, B, pi
    public void print_hmm() {
        System.out.println("A:");
        System.out.println(Arrays.deepToString(A));
        System.out.println("B:");
        System.out.println(Arrays.deepToString(B));
        System.out.println("pi:");
        System.out.println(Arrays.deepToString(pi));
    }


    // Returns next emission probability
    public void next_emission() {
        double[][] state_prob = matrixOps.multiply(pi, A);
        double[][] emission_prob = matrixOps.multiply(state_prob, B);
        matrixOps.print_matrix(emission_prob);
    }

    // Uses forward algorithm to compute probability of
    // a given sequence of observations
    public Pair<double[][], double[]> fwdAlgorithm(int[] observations) {
        int T = observations.length;
        double[][] alpha = new double[states][T]; // Matrix of all computed alphas
        double[] norm_ctes = new double[T];

        // Compute for case t=0:
        norm_ctes[0] = 0;
        for (int i = 0; i < states; i++) {
            alpha[i][0] = pi[0][i] * B[i][observations[0]];
            norm_ctes[0] += alpha[i][0];
        }

        // Normalize alpha (t=1):
        norm_ctes[0] = 1 / norm_ctes[0];
        for (int i = 0; i < states; i++)
            alpha[i][0] = norm_ctes[0] * alpha[i][0];

        // Compute for t>0:
        for (int t = 1; t < T; t++) {
            norm_ctes[t] = 0;
            for (int i = 0; i < states; i++) {
                alpha[i][t] = 0;
                for (int j = 0; j < states; j++) {
                    alpha[i][t] += alpha[j][t - 1] * A[j][i];
                }
                alpha[i][t] = alpha[i][t] * B[i][observations[t]];
                norm_ctes[t] += alpha[i][t];
            }
            norm_ctes[t] = 1 / norm_ctes[t];
            for (int i = 0; i < states; i++) {
                alpha[i][t] = alpha[i][t] * norm_ctes[t];
            }
        }
        Pair<double[][], double[]> result = new Pair<double[][], double[]>();
        result.first = alpha;
        result.second = norm_ctes;
        return result;
    }

    public double obsLogProb(int[] observations) {
        Pair<double[][], double[]> alpha_info = fwdAlgorithm(observations);
        double[] ctes = alpha_info.second;

        double cte = 0;
        for (int i = 0; i < ctes.length; i++) {
            cte -= Math.log(ctes[i]);
        }
        return cte;
    }

    /* Uses backward algorithm to compute the sequence of betas */
    public double[][] bkwAlgorithm(int[] observations, double[] norm_ctes) {
        int T = observations.length;
        double[][] beta = new double[states][T];

        // Initialize
        for (int i = 0; i < states; i++) {
            beta[i][observations.length - 1] = norm_ctes[T - 1];
        }

        // Main loop
        for (int t = T - 2; t > -1; t--) {
            for (int i = 0; i < states; i++) {
                beta[i][t] = 0;
                for (int j = 0; j < states; j++) {
                    beta[i][t] += A[i][j] * B[j][observations[t + 1]] * beta[j][t + 1];
                }
                // scale βt(i) with same scale factor as αt(i)
                beta[i][t] = norm_ctes[t] * beta[i][t];
            }
        }
        return beta;
    }

    // Uses viterbi algorithm to compute the most likely sequence
    // given a set of observations
    public int[] viterbiAlgorithm(int[] observations) {

        int[] result = new int[observations.length];
        int[][] path = new int[pi[0].length][observations.length]; // Array of possible combinations per time
        double[][] deltaPrev = matrixOps.vector_col_elem_wise_mult(pi, B, observations[0]);

        for (int i = 1; i < observations.length; i++) { // Per observation
            double[][] delta = new double[1][pi[0].length];
            for (int j = 0; j < pi[0].length; j++) { // Per state

                Pair<Double, Integer> max_pair = matrixOps.maxVectorMatrixCol(deltaPrev, A, j);
                path[j][i] = max_pair.second;
                delta[0][j] = max_pair.first * B[j][observations[i]];
            }

            deltaPrev = delta; // Update delta
        }

        double maximum = -1;
        int maximum_position = -1;
        for (int i = 0; i < deltaPrev[0].length; i++) {
            if (deltaPrev[0][i] > maximum) {
                maximum = deltaPrev[0][i];
                maximum_position = i;
            }
        }

        result[observations.length - 1] = maximum_position;
        for (int i = observations.length - 2; i > -1; i--) {
            result[i] = path[result[i + 1]][i + 1];
        }
        return result;
    }

    public void updateHMM(int[] observations) {
        int T = observations.length;

        // Forward pass
        Pair<double[][], double[]> alpha_info = fwdAlgorithm(observations);
        double[][] alpha = alpha_info.first;
        double[] norm_ctes = alpha_info.second;

        // Backward pass
        double[][] beta = bkwAlgorithm(observations, norm_ctes);

        // System.out.print("Alpha");
        // matrixOps.print_matrix(alpha);
        // System.out.print("CTES");
        // matrixOps.print_vector(norm_ctes);
        // System.out.print("BETA");
        // matrixOps.print_matrix(beta);
        // System.out.print("##################");

        // Compute di_gamma & gamma
        // (OBS: NO need to normalize gammas since we use both alpha and beta)
        double[][][] di_gamma = new double[T][states][states];
        double[][] gamma = new double[T][states];
        for (int t = 0; t < T - 1; t++) {
            for (int i = 0; i < states; i++) {
                // double alpha_sum = 0;
                // for (int k = 0; k < states; k++) {
                // alpha_sum += alpha[k][T-1];
                // }
                // OBS: alpha_sum will always be 1 since its normalized
                gamma[t][i] = 0;
                for (int j = 0; j < states; j++) {
                    // di_gamma[t][i][j] = (alpha[i][t] * A[i][j] * B[j][observations[t+1]] *
                    // beta[j][t+1]) / alpha_sum;
                    di_gamma[t][i][j] = (alpha[i][t] * A[i][j] * B[j][observations[t + 1]] * beta[j][t + 1]);
                    gamma[t][i] += di_gamma[t][i][j];
                }
            }
        }
        // T-1 Special case
        for (int i = 0; i < states; i++) {
            gamma[T - 1][i] = alpha[i][T - 1];
        }

        // Update A
        for (int i = 0; i < states; i++) {
            for (int j = 0; j < states; j++) {
                double gamma_sum = 0;
                double di_gamma_sum = 0;
                for (int t = 0; t < T - 1; t++) {
                    gamma_sum += gamma[t][i];
                    di_gamma_sum += di_gamma[t][i][j];
                }
                A[i][j] = di_gamma_sum / gamma_sum;
            }
        }

        // Update B
        for (int i = 0; i < states; i++) {
            for (int k = 0; k < emissions; k++) {
                double gamma_sum = 0;
                double di_gamma_sum = 0;
                for (int t = 0; t < T - 1; t++) {
                    gamma_sum += gamma[t][i];
                    if (observations[t] == k) {
                        di_gamma_sum += gamma[t][i];
                    }
                }
                B[i][k] = di_gamma_sum / gamma_sum;
            }
        }

        // Update pi
        for (int i = 0; i < states; i++) {
            pi[0][i] = gamma[0][i];
        }
    }

    public void baumWelch(int[] observations) {
        int iterations = 0;
        double log_prob_ant = - (LOG_NON_IMPROVEMENT + 1);
        double log_prob = 0;

        while (iterations < ITERATION_LIMIT && log_prob - log_prob_ant >= LOG_NON_IMPROVEMENT) {
            log_prob_ant = log_prob;
            updateHMM(observations);

            Pair<double[][], double[]> alpha_info = fwdAlgorithm(observations);
            double[] norm_ctes = alpha_info.second;

            log_prob = 0;
            for (int i = 0; i < norm_ctes.length; i++) {
                log_prob -= Math.log(norm_ctes[i]);
            }

            if (iterations == 0)
                log_prob_ant = log_prob - (LOG_NON_IMPROVEMENT + 1);
            // System.out.println("Log ant: " + log_prob_ant);
            // System.out.println("Log act: " + log_prob);
            // System.out.println("Dif: " + (log_prob - log_prob_ant));
            // System.out.println("----");
            iterations++;
        }
        // System.out.println(iterations);
    }

    // Does baum-welch and returns iterations and final log(prob) to build graphs
    public Pair<Double, Integer> baumWelchWithDetails(int[] observations) {
        int iterations = 0;
        double log_prob_ant = - (LOG_NON_IMPROVEMENT + 1);
        double log_prob = 0;

        while (iterations < ITERATION_LIMIT && log_prob - log_prob_ant >= LOG_NON_IMPROVEMENT) {
            log_prob_ant = log_prob;
            updateHMM(observations);

            Pair<double[][], double[]> alpha_info = fwdAlgorithm(observations);
            double[] norm_ctes = alpha_info.second;

            log_prob = 0;
            for (int i = 0; i < norm_ctes.length; i++) {
                log_prob -= Math.log(norm_ctes[i]);
            }

            if (iterations == 0)
                log_prob_ant = log_prob - (LOG_NON_IMPROVEMENT + 1);
            iterations++;
        }
        Pair<Double, Integer> summary = new Pair<Double, Integer>();
        summary.first = log_prob;
        summary.second = iterations;
        return summary;
    }
}