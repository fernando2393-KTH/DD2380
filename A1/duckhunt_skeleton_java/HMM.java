
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
    boolean is_guesser = false;

    public final int ITERATION_LIMIT = 1000;
    public final double LOG_NON_IMPROVEMENT = 0.001;

    public HMM(int st, int emi) {
        states = st;
        emissions = emi;
        A = matrixOps.randomMatrix(states, states, true);
        B = matrixOps.randomMatrix(states, emissions, false);
        pi = matrixOps.randomMatrix(1, states, false);
    }

    // Reads values from console and populates A, B, pi
    public void read_hmm(BufferedReader reader) {
        A = matrixOps.read_matrix(reader);
        B = matrixOps.read_matrix(reader);
        pi = matrixOps.read_matrix(reader);
        states = A.length;
        emissions = B[0].length;
    }

    // Prints A, B, pi
    public void print_hmm() {
        System.err.println("A:");
        matrixOps.print_matrix(A);
        System.err.println("B:");
        matrixOps.print_matrix(B);
        System.err.println("pi:");
        matrixOps.print_matrix(pi);
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
            
            norm_ctes[t] = 1 / (norm_ctes[t]);
            for (int i = 0; i < states; i++) {
                alpha[i][t] = alpha[i][t] * norm_ctes[t];
            }
        }
        Pair<double[][], double[]> result = new Pair<double[][], double[]>();
        result.first = alpha;
        result.second = norm_ctes;
        return result;
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

    public Pair<Integer, Double> nextEmissionGivenObs(int[] observations) {
        double[][] delta = new double[1][pi[0].length];
        double[][] deltaPrev = matrixOps.vector_col_elem_wise_mult(pi, B, observations[0]);
        for (int i = 1; i < observations.length; i++) { // Per observation
            for (int j = 0; j < pi[0].length; j++) { // Per state
                Pair<Double, Integer> max_pair = matrixOps.maxVectorMatrixCol(deltaPrev, A, j);
                delta[0][j] = max_pair.first * B[j][observations[i]];
            }

            deltaPrev = delta; // Update delta
        }

        double sum = 0;
        for (int i = 0; i < delta[0].length; i++) {
            sum += delta[0][i];
        }
        for (int i = 0; i < delta[0].length; i++) {
            delta[0][i] = delta[0][i]/sum;
        }
        // double max = Double.NEGATIVE_INFINITY;
        // int max_pos = -1;
        // for (int i = 0; i < delta[0].length; i++) {
        //     if (delta[0][i] > max) {
        //         max = delta[0][i];
        //         max_pos = i;
        //     }
        // }
        // double[][] most_likely_state = new double[1][delta[0].length];
        // most_likely_state[0][max_pos] = 1;

        double[][] state_prob = matrixOps.multiply(delta, A);
        double[][] emission_prob = matrixOps.multiply(state_prob, B);

        Pair<Integer, Double> result = new Pair();
        result.first = 0;
        result.second = emission_prob[0][0];
        for (int i = 1; i < emissions; i++) {
            if (emission_prob[0][i] > result.second) {
                result.first = i;
                result.second = emission_prob[0][i];
            }
        }
        return result;
    }


    // Returns prob of observing i in next observation
    public double probObsi(Pair<double[][], double[]> alpha_info, int obs_k) {
        
        // double cte = 1;
        // for (int i = 0; i < alpha_info.second.length; i++) {
        //     cte = cte*alpha_info.second[i];
        // }

        double sum = 0;
        for (int k = 0; k < states; k++) {
            double sum2 = 0;
            for (int h = 0; h < states; h++) {
                sum2 += A[h][k]*alpha_info.first[h][alpha_info.first[0].length-1];
            }
            sum += sum2*B[k][obs_k];
        }
        return sum;
    }

    public Pair<Integer, Double> nextMove(int[] obss){
        Pair<double[][], double[]> alpha_info = fwdAlgorithm(obss);

        double[][] current_state = new double[1][states];
        double sum = 0;
        for (int i = 0; i < states; i++) {
            current_state[0][i] = alpha_info.first[i][alpha_info.first[0].length-1];
            // System.err.print(Math.round(current_state[0][i]*100) + ", ");
            sum += current_state[0][i];
        }
        // System.err.println();
        // System.err.println(sum);

        double[][] state_prob = matrixOps.multiply(current_state, A);
        double[][] emission_prob = matrixOps.multiply(state_prob, B);

        Pair<Integer, Double> result = new Pair();
        result.first = -1;
        result.second = Double.NEGATIVE_INFINITY;
        sum = 0;
        for (int i = 0; i < emissions; i++) {
            // System.err.print(Math.round(emission_prob[0][i]*100) + ", ");
            sum += emission_prob[0][i];
            if (emission_prob[0][i] > result.second) {
                result.first = i;
                result.second = emission_prob[0][i];
            }
        }
        // System.err.println();
        // System.err.println(sum);

        // if (result.second > 1)
        //     System.exit(0);
        return result;
    }


    // TODO(oleguer): optimize this, compute online log_c and dont store all alpha
    public double obsLogProb(int[] observations) {
        Pair<double[][], double[]> alpha_info = fwdAlgorithm(observations);
        double[] ctes = alpha_info.second;

        double cte = 0;
        for (int i = 0; i < ctes.length; i++) {
            // System.err.print(Math.round(ctes[i]));
            // System.err.print(" ");
            cte -= Math.log(ctes[i]);
        }
        // System.err.println();

        return cte;
    }

    public void updateHMM(int[] observations) {
        int T = observations.length;

        // Forward pass
        Pair<double[][], double[]> alpha_info = fwdAlgorithm(observations);
        double[][] alpha = alpha_info.first;
        double[] norm_ctes = alpha_info.second;

        // Backward pass
        double[][] beta = bkwAlgorithm(observations, norm_ctes);

        // Compute di_gamma & gamma
        // (OBS: NO need to normalize gammas since we use both alpha and beta)
        double[][][] di_gamma = new double[T][states][states];
        double[][] gamma = new double[T][states];
        for (int t = 0; t < T - 1; t++) {
            for (int i = 0; i < states; i++) {
                // OBS: alpha_sum will always be 1 since its normalized
                gamma[t][i] = 0;
                for (int j = 0; j < states; j++) {
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
                if (di_gamma_sum == 0 && is_guesser)
                    di_gamma_sum = 1;
                B[i][k] = di_gamma_sum / gamma_sum;
            }
        }

        // Update pi
        for (int i = 0; i < states; i++) {
            pi[0][i] = gamma[0][i];
        }
    }

    public double baumWelch(int[] observations) {
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
                if (norm_ctes[i] != 0)  // To avoid -infinity in logarith
                    log_prob -= Math.log(norm_ctes[i]);
            }

            if (iterations == 0)
                log_prob_ant = log_prob - (LOG_NON_IMPROVEMENT + 1);
            iterations++;
        }
        return log_prob;
    }

    public double baumWelchDebug(int[] observations) {
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
            System.err.print(", ");
            System.err.print(norm_ctes[i]);
                if (norm_ctes[i] != 0 && norm_ctes[i] != Double.POSITIVE_INFINITY)  // To avoid -infinity in logarith
                    log_prob -= Math.log(norm_ctes[i]);
            }
            // System.err.println(log_prob);

            if (iterations == 0)
                log_prob_ant = log_prob - (LOG_NON_IMPROVEMENT + 1);
            // System.err.println("Log ant: " + log_prob_ant);
            // System.err.println("Log act: " + log_prob);
            // System.err.println("Dif: " + (log_prob - log_prob_ant));
            // System.err.println("----");
            iterations++;
        }
        System.err.println();
        System.err.print("iterations: ");
        System.err.println(iterations);
        return log_prob;
    }
}