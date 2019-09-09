
// Solution of HMM1 problem
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HMM3 {
    public static void main(String[] args) {
        // Reader to read from terminal
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        HMM.read_hmm(reader);

        int [] obs = matrixOps.read_vector(reader);

        try {
            reader.close();
        } catch (Exception e) {
            System.err.println(e);
        }

        HMM.baumWelch(obs);
        //HMM.print_hmm();
        matrixOps.print_matrix_as_row(HMM.A);
        matrixOps.print_matrix_as_row(HMM.B);
    }
}