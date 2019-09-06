import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HMM1 { 
    public static void main(String[] args) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        HMM.read_hmm(reader);

        int [] obs = matrixOps.read_vector(reader);

        try {
            reader.close();
        } catch (Exception e) {
            System.err.println(e);
        }

        double[][] alpha_mat = HMM.fwdAlgorithm(obs);

        double sum = 0;
        for (int i = 0; i < alpha_mat.length; i++) {
            sum += alpha_mat[i][obs[0]];
        }
        
        System.out.println(sum);

    } 
} 