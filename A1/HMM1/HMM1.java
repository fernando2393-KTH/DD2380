// Solution of HMM1 problem
import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStreamReader;

public class HMM1 { 
    public static void main(String[] args) {
        // Reader to read from terminal
        BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in));

        // Create HMM object and read params
        HMM hmm = new HMM();
        hmm.read_hmm(reader);

        // Read observations matrix
        matrixOps mat_ops = new matrixOps();
        int[] observations = mat_ops.read_int_vect(reader);
        
        // Given loaded HMM, compute prob of series of observations
        double prob = hmm.sequence_probability(observations);
        System.out.println(prob);  // Print
    } 
} 