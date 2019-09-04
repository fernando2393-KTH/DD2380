// Class to hold HMM
import java.util.Arrays;
import matrixOps;

public class HMM {
    static double[][] A;
    static double[][] B;
    static double[][] pi;

    // Reads values from console and populates A, B, pi
    public static void read_hmm() {
        A = matrixOps.read_matrix();
        B = matrixOps.read_matrix();
        pi = matrixOps.read_matrix();   
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

    // PRIVATE

}