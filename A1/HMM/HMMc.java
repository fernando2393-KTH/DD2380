
// Solution of HMM1 problem
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.Object;

public class HMMc {
    private static double[][] A_real = {{0.7, 0.05, 0.25}, {0.1, 0.8, 0.1}, {0.2, 0.3, 0.5}};
    private static double[][] B_real = {{0.7, 0.2, 0.1, 0}, {0.1, 0.4, 0.3, 0.2}, {0, 0.1, 0.2, 0.7}};
    private static double[][] pi_real = {{1, 0, 0}};

    public static void main(String[] args) {
        // Reader to read from terminal
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        HMM.read_hmm(reader);
        // TODO(Fernando): For question 8, instead of read_hmm, initialize it with r
        // int states = 3;
        // int emissions =  4;
        // HMM.randomInit(states, emissions) // OBS: Question 9 is the same just changing states param)

        int [] obs = matrixOps.read_vector(reader);

        try {
            reader.close();
        } catch (Exception e) {
            System.err.println(e);
        }

        // TODO(Fernando): Iterate over number of observations
        // for...
            Pair<Double, Double> nw_details = HMM.baumWelchWithDetails(obs);
            double last_log_prob = nw_details.first;
            double iterations= nw_details.second;        
            double error_A = matrixOps.forbDistance(HMM.A, A_real);
            double error_B = matrixOps.forbDistance(HMM.B, B_real);
            double error_pi = matrixOps.forbDistance(HMM.pi, pi_real);
            // TODO(Fernando) : save this variables into arrays and plot variable vs n_obs


        // System.out.println("error_A: " + error_A);
        // System.out.println("error_B: " + error_B);
        // System.out.println("error_pi: " + error_pi);
    }
}