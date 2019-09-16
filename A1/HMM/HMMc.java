
// Solution of HMM1 problem
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

public class HMMc {
    private static double[][] A_real = { { 0.7, 0.05, 0.25 }, { 0.1, 0.8, 0.1 }, { 0.2, 0.3, 0.5 } };
    private static double[][] B_real = { { 0.7, 0.2, 0.1, 0 }, { 0.1, 0.4, 0.3, 0.2 }, { 0, 0.1, 0.2, 0.7 } };
    private static double[][] pi_real = { { 1, 0, 0 } };

    private static final int IT_INCREMENT = 10;
    private static final boolean DEBUG = true;

    public static void main(String[] args) {

        HMM hmm = new HMM();
        HMM hmm_original = new HMM();

        
        hmm_original.A = A_real;
        hmm_original.B = B_real;
        hmm_original.pi = pi_real;
        
        // Reader to read from terminal
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        hmm.read_hmm(reader);
        
        // int states = 3;
        // int emissions = 4;
        // HMM.randomInit(states, emissions) // OBS: Question 9 is the same just
        // changing states param)

        //HMM.randomInit(4, 4);

        hmm.print_hmm(); // Printing the randomly generated matrix


        // Reads the first three lines for question 8, since matrix is initialized in other way
        /*try {
            reader.readLine();
            reader.readLine();
            reader.readLine();
        } catch (Exception e) {
        }*/

        
        int[] obs = matrixOps.read_vector(reader);

        try {
            reader.close();
        } catch (Exception e) {
            System.err.println(e);
        }

        double last_log_prob_array[] = new double[obs.length / IT_INCREMENT];
        double iterations_array[] = new double[obs.length / IT_INCREMENT];
        //double error_A_array[] = new double[obs.length / IT_INCREMENT];
        //double error_B_array[] = new double[obs.length / IT_INCREMENT];
        //double error_pi_array[] = new double[obs.length / IT_INCREMENT];
        double error_matrices[] = new double[obs.length / IT_INCREMENT];

        for (int i = 10; i < obs.length + 1; i = i + IT_INCREMENT) { // Increment of 10 observations per iteration

            Pair<Double, Integer> nw_details = hmm.baumWelchWithDetails(Arrays.copyOfRange(obs, 0, i));

            Pair<double[][], double[]> alpha_info = hmm_original.fwdAlgorithm(Arrays.copyOfRange(obs, 0, i));
            double[] norm_ctes = alpha_info.second;

            int log_prob_original = 0;
            for (int j = 0; j < norm_ctes.length; j++) {
                    log_prob_original -= Math.log(norm_ctes[j]);
            }

            last_log_prob_array[i / IT_INCREMENT - 1] = nw_details.first;
            iterations_array[i / IT_INCREMENT - 1] = nw_details.second;
            //error_A_array[i / IT_INCREMENT - 1] = matrixOps.forbDistance(hmm.A, A_real);
            //error_B_array[i / IT_INCREMENT - 1] = matrixOps.forbDistance(hmm.B, B_real);
            //error_pi_array[i / IT_INCREMENT - 1] = matrixOps.forbDistance(hmm.pi, pi_real);
            error_matrices[i / IT_INCREMENT - 1] = Math.abs(nw_details.first - log_prob_original); // Calculation of the difference of probabilities

        }

        if (DEBUG) {

            /*for (int i = 0; i < obs.length / IT_INCREMENT; i++) {
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

            for (int i = 0; i < obs.length / IT_INCREMENT; i++) {
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