
// Solution of HMM1 problem
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HMM3 {
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

        hmm.baumWelch(obs);
        //HMM.print_hmm();
        matrixOps.print_matrix_as_row(hmm.A);
        matrixOps.print_matrix_as_row(hmm.B);
    }
}