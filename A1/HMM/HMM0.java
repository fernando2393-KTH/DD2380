import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HMM0 { 
    public static void main(String[] args) {

        HMM hmm = new HMM();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        hmm.read_hmm(reader);

        try {
            reader.close();
        } catch (Exception e) {
            System.err.println(e);
        }
        

        hmm.next_emission();
    } 
} 