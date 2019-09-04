// Class to hold HMM
import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStreamReader;
import java.util.Arrays;

public class HMM {
    static double[][] A;
    static double[][] B;
    static double[][] pi;

    // Reads values from console and populates A, B, pi
    public static void read_hmm() {
        BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in));
        try {
            A = matrixOps.parse_matrix(reader.readLine());
            B = matrixOps.parse_matrix(reader.readLine());
            pi = matrixOps.parse_matrix(reader.readLine());   
        } catch (Exception e) {
            System.err.println("Could not read from System.in");
        }
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