import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HMM0 { 
    public static void main(String[] args) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        HMM.read_hmm(reader);

        try {
            reader.close();
        } catch (Exception e) {
            System.err.println(e);
        }
        

        HMM.next_emission();
    } 
} 