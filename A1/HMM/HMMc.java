
// Solution of HMM1 problem
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

public class HMMc {
    private static double[][] A_real = { { 0.7, 0.05, 0.25 }, { 0.1, 0.8, 0.1 }, { 0.2, 0.3, 0.5 } };
    private static double[][] B_real = { { 0.7, 0.2, 0.1, 0 }, { 0.1, 0.4, 0.3, 0.2 }, { 0, 0.1, 0.2, 0.7 } };
    private static double[][] pi_real = { { 1, 0, 0 } };

    private static final int IT_INCREMENT = 10;
    private static final boolean DEBUG = false;

    public static void main(String[] args) {

        HMM hmm = new HMM();
        HMM hmm_original = new HMM();
        hmm_original.assignValues(A_real, B_real, pi_real);
        
        // Reader to read from terminal
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        hmm.read_hmm(reader);

        // int states = 4;
        // int emissions = 4;
        // hmm.randomInit(states, emissions);
        hmm.print_hmm();

        // Aux variables for hmm reset
        double A_aux [][] = new double [hmm.A.length][hmm.A[0].length];
        double B_aux [][] = new double [hmm.B.length][hmm.B[0].length];
        double pi_aux [][] = new double [hmm.pi.length][hmm.pi[0].length];

        matrixOps.copy_matrix(hmm.A, A_aux);
        matrixOps.copy_matrix(hmm.B, B_aux);
        matrixOps.copy_matrix(hmm.pi, pi_aux);
        
        // int states = 3;
        // int emissions = 4;
        // HMM.randomInit(states, emissions) // OBS: Question 9 is the same just
        // changing states param)

        //HMM.randomInit(4, 4);
        //hmm_original.print_hmm();
        //hmm.print_hmm(); // Printing the randomly generated matrix


        // Reads the first three lines for question 8, since matrix is initialized in other way
        // try {
        //     reader.readLine();
        //     reader.readLine();
        //     reader.readLine();
        // } catch (Exception e) {
        // }

        
        int[] obs = matrixOps.read_vector(reader);



        double last_log_prob_array[] = new double[obs.length / IT_INCREMENT];
        double iterations_array[] = new double[obs.length / IT_INCREMENT];
        //double error_A_array[] = new double[obs.length / IT_INCREMENT];
        //double error_B_array[] = new double[obs.length / IT_INCREMENT];
        //double error_pi_array[] = new double[obs.length / IT_INCREMENT];
        double error_matrices[] = new double[obs.length / IT_INCREMENT];

        for (int i = 10; i < obs.length + 1; i = i + IT_INCREMENT) { // Increment of 10 observations per iteration
            
            int[] obs_subarray = Arrays.copyOfRange(obs, 0, i);

            Pair<Double, Integer> nw_details = hmm.baumWelchWithDetails(obs_subarray);
            Double estimated_log_prob = hmm.obsLogProb(obs_subarray);
            Double original_log_prob = hmm_original.obsLogProb(obs_subarray);

            // System.out.println(estimated_log_prob);

            last_log_prob_array[i / IT_INCREMENT - 1] = estimated_log_prob;
            iterations_array[i / IT_INCREMENT - 1] = nw_details.second;
            error_matrices[i / IT_INCREMENT - 1] = Math.abs((estimated_log_prob - original_log_prob)/original_log_prob); // Calculation of the difference of probabilities

            hmm.print_hmm();

            // hmm reset            
            matrixOps.copy_matrix(A_aux, hmm.A);
            matrixOps.copy_matrix(B_aux, hmm.B);
            matrixOps.copy_matrix(pi_aux, hmm.pi);
        }

        try {
            reader.close();
        } catch (Exception e) {
            System.err.println(e);
        }

        if (DEBUG) {

            for (int i = 0; i < obs.length / IT_INCREMENT; i++) {
                if (i < obs.length / IT_INCREMENT - 1) {
                    System.out.print(last_log_prob_array[i] + ", ");
                } else {
                    System.out.print(last_log_prob_array[i]);
                }
            }

            System.out.println();

            for (int i = 0; i < obs.length / IT_INCREMENT; i++) {
                if (i < obs.length / IT_INCREMENT - 1) {
                    System.out.print(iterations_array[i] + ", ");
                } else {
                    System.out.print(iterations_array[i]);
                }
            }

            System.out.println();

            /*for (int i = 0; i < obs.length / IT_INCREMENT; i++) {
                if (i < obs.length / IT_INCREMENT - 1) {
                    System.out.print(error_A_array[i] + ", ");
                } else {
                    System.out.print(error_A_array[i]);
                }
            }

            System.out.println();

            for (int i = 0; i < obs.length / IT_INCREMENT; i++) {
                if (i < obs.length / IT_INCREMENT - 1) {
                    System.out.print(error_B_array[i] + ", ");
                } else {
                    System.out.print(error_B_array[i]);
                }
            }

            System.out.println();

            for (int i = 0; i < obs.length / IT_INCREMENT; i++) {
                if (i < obs.length / IT_INCREMENT - 1) {
                    System.out.print(error_pi_array[i] + ", ");
                } else {
                    System.out.print(error_pi_array[i]);
                }
            }

            System.out.println();*/

            for (int i = 0; i < obs.length / IT_INCREMENT; i++) {
                if (i < obs.length / IT_INCREMENT - 1) {
                    System.out.print(error_matrices[i] + ", ");
                } else {
                    System.out.print(error_matrices[i]);
                }
            }

            System.out.println();

        }

        //hmm.print_hmm();

        // System.out.println("error_A: " + error_A);
        // System.out.println("error_B: " + error_B);
        // System.out.println("error_pi: " + error_pi);
    }
}