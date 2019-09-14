
// Solution of HMM1 problem
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.Object;

public class HMMc {
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
        matrixOps.print_matrix(HMM.A);
        matrixOps.print_matrix(HMM.B);
        matrixOps.print_matrix(HMM.pi);


        double[][] A_real = {{0.7, 0.05, 0.25}, {0.1, 0.8, 0.1}, {0.2, 0.3, 0.5}};
        double[][] B_real = {{0.7, 0.2, 0.1, 0}, {0.1, 0.4, 0.3, 0.2}, {0, 0.1, 0.2, 0.7}};
        double[][] pi_real = {{1, 0, 0}};
        double error_A = Math.abs(matrixOps.normFrob(HMM.A) - matrixOps.normFrob(A_real));
        double error_B = Math.abs(matrixOps.normFrob(HMM.B) - matrixOps.normFrob(B_real));
        double error_pi = Math.abs(matrixOps.normFrob(HMM.pi) - matrixOps.normFrob(pi_real));
        
        if(error_A < 0.01){
            System.out.print("error_A: " + error_A);
            System.out.println(" CONVERGES!");
        }
        else {
            System.out.println("error_A: " + error_A);
        }
        
        if(error_B < 0.01){
            System.out.print("error_B: " + error_B);
            System.out.println(" CONVERGES!");
        }
        else {
            System.out.println("error_B: " + error_B);
        }
        
        if(error_pi < 0.01){
            System.out.print("error_pi: " + error_pi);
            System.out.println(" CONVERGES!");
        }
        else {
            System.out.println("error_pi: " + error_pi);
        }
    }
}