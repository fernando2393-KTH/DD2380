
// Class to hold HMM
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class HMM {
    static double[][] A;
    static double[][] B;
    static double[][] Pi;

    // Reads values from console and populates A, B, Pi
    public static void read_hmm() {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        A = matrixOps.read_matrix(reader);
        B = matrixOps.read_matrix(reader);
        Pi = matrixOps.read_matrix(reader);


        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Reads values from console and populates A, B, Pi
    public static void print_hmm() {
        System.out.println("A:");
        System.out.println(Arrays.deepToString(A));
        System.out.println("B:");
        System.out.println(Arrays.deepToString(B));
        System.out.println("Pi:");
        System.out.println(Arrays.deepToString(Pi));
    }

    // Returns next emission probability
    public static void next_emission() {
        double[][] state_prob = matrixOps.multiply(Pi, A);
        double[][] emission_prob = matrixOps.multiply(state_prob, B);
        matrixOps.print_matrix(emission_prob);
    }

    // PRIVATE

}