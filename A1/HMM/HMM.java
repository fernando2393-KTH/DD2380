// Class to hold HMM
import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import javafx.util.Pair;

public class HMM {
    // PUBLIC
    static double[][] A;
    static double[][] B;
    static double[][] pi;

    // Reads values from console and populates A, B, pi
    public static void read_hmm(BufferedReader reader) {
        A = matrixOps.read_matrix(reader);
        B = matrixOps.read_matrix(reader);
        pi = matrixOps.read_matrix(reader);   
    }

    // Reads values from console and populates A, B, pi
    public static void print_hmm() {
        System.out.println("A:");
        System.out.println(Arrays.deepToString(A));
        System.out.println("B:");
        System.out.println(Arrays.deepToString(B));
        System.out.println("pi:");
        System.out.println(Arrays.deepToString(pi));
    }

    // Returns next emission probability
    public static void next_emission() {
        double[][] state_prob = matrixOps.multiply(pi, A);
        double[][] emission_prob = matrixOps.multiply(state_prob, B);
        matrixOps.print_matrix(emission_prob);
    }

    // Uses forward algorithm to compute probability of
    // a given sequence of observations
    public static double fwdAlgorithm(int[] observations) {
        double[][] alpha = 
            matrixOps.vector_col_elem_wise_mult(pi, B, observations[0]);

        // TODO(oleguer): Convert to ln
        for (int i = 1; i < observations.length; i++)
            alpha = matrixOps.vector_col_elem_wise_mult(
                    matrixOps.multiply(alpha, A), B, observations[i]);

        double sum = 0;
        for (int i = 0; i < alpha[0].length; i++)
            sum += alpha[0][i];
        return sum;
    }

    // Uses viterbi algorithm to compute the most likely sequence
    // given a set of observations
    public static int[] viterbiAlgorithm(int[] observations) {

        int[] result = new int[observations.length];

        int [][] path = new int [pi[0].length][observations.length]; // Array of possible combinations per time

        double [][] deltaPrev =
            matrixOps.vector_col_elem_wise_mult(pi, B, observations[0]);
        
        for (int i = 1; i < observations.length; i++){ // Per observation

            double [][] delta = new double [1][pi[0].length];

            for(int j = 0; j < pi[0].length; j++){ // Per state

                Pair<Double, Integer> max_pair = matrixOps.maxVectorMatrixCol(deltaPrev, A, j);
                path[j][i] = max_pair.getValue();

                delta[0][j] = max_pair.getKey() * B[j][observations[i]];

            }

            deltaPrev = delta; // Update delta
        }

        double maximum = -1;
        int maximum_position = -1;

        for(int i = 0; i < deltaPrev[0].length; i++){
            
            if(deltaPrev[0][i] > maximum){
                maximum = deltaPrev[0][i];
                maximum_position = i;
            }
        }

        result[observations.length - 1] = maximum_position;

        for(int i = observations.length - 2; i > -1; i--){
            result[i] = path[result[i+1]][i+1];
        }

        return result;
    }
}