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
        
        System.out.println(HMM.fwd_result(obs));

    } 
} 