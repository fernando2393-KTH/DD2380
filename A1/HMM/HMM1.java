import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HMM1 { 
    public static void main(String[] args) {

        HMM hmm = new HMM();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        hmm.read_hmm(reader);

        int [] obs = matrixOps.read_vector(reader);

        try {
            reader.close();
        } catch (Exception e) {
            System.err.println(e);
        }

        Pair<double[][], double[]> alpha_info = hmm.fwdAlgorithm(obs);
        double[] ctes = alpha_info.second;

        double cte = 1;
        for (int i = 0; i < ctes.length; i++) {
            cte = cte/ctes[i];
        }

        System.out.println(cte);
    } 
} 