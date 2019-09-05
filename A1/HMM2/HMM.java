// Class to hold HMM
import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStreamReader;
import java.util.Arrays;

public class HMM {
    // PUBLIC
    static double[][] A;
    static double[][] B;
    static double[][] pi;
    static matrixOps mat_ops = new matrixOps();

    // Reads values from console and populates A, B, pi
    public static void read_hmm(BufferedReader reader) {
        A = mat_ops.read_matrix(reader);
        B = mat_ops.read_matrix(reader);
        pi = mat_ops.read_matrix(reader);   
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
        double[][] state_prob = mat_ops.multiply(pi, A);
        double[][] emission_prob = mat_ops.multiply(state_prob, B);
        mat_ops.print_matrix(emission_prob);
    }

    // Uses forward algorithm to compute probability of
    // a given sequence of observations
    public double sequence_probability(int[] observations) {
        double[][] alpha = 
            mat_ops.vector_col_elem_wise_mult(pi, B, observations[0]);

        // TODO(oleguer): Convert to ln
        for (int i = 1; i < observations.length; i++)
            alpha = mat_ops.vector_col_elem_wise_mult(
                    mat_ops.multiply(alpha, A), B, observations[i]);

        double sum = 0;
        for (int i = 0; i < alpha[0].length; i++)
            sum += alpha[0][i];
        return sum;
    }

    // Uses viterbi algorithm to compute the most likely sequence
    // given a set of observations
    public int[] most_likely_sequence(int[] observations) {
        int[] result;
        double[][] delta =
            mat_ops.vector_col_elem_wise_mult(pi, B, observations[0]);
        
        // for (int i = 1; i < observations.)

        return result;
    }
}