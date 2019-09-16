// Solution of HMM2 problem
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HMM2 {
    public static void main(String[] args) {

        HMM hmm = new HMM();

        // Reader to read from terminal
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        hmm.read_hmm(reader);

        int [] obs = matrixOps.read_vector(reader);

        try {
            reader.close();
        } catch (Exception e) {
            System.err.println(e);
        }

        int[] sequence = hmm.viterbiAlgorithm(obs);

        System.out.print(sequence[0]);
        for (int i = 1; i < sequence.length; i++){
            System.out.print(" ");
            System.out.print(sequence[i]);
        }
        System.out.println();
    }
}