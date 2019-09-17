import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

public class Species extends HMM {
    public ArrayList<Integer> observations;
    public int bird_num;
    public static int total_bird_num; // All the sspecies

    public Species(int st, int emi) {
        super.states = st;
        super.emissions = emi;
        super.A = matrixOps.randomMatrix(super.states, super.states, true);
        super.B = matrixOps.randomMatrix(super.states, super.emissions, false);
        super.pi = matrixOps.randomMatrix(1, super.states, false);
        observations = new ArrayList();
    }

    // Assigns bird to species
    public void appendObs(int[] obss) {
        // System.err.println("appending bird");
        for (int i = 0; i < obss.length; i ++) {
            observations.add(obss[i]);
        }
        bird_num++;
        total_bird_num++;
    }

    public void updateModel() {
        int obss[] = new int[observations.size()];
        int counter = 0;
        Iterator itr = observations.iterator();
        while (itr.hasNext()) {
            obss[counter] = (int) itr.next();
            counter++;
        }
        if (counter == 0) // No observations
            return;
        double confidence = super.baumWelch(obss);
    }

    // Returns Bayes logprob
    public double getProb(int[] obss, boolean use_bayes) {
        // IDEA: P(lambda|obs) = P(obs|lambda)*P(lambda)/P(obs)
        // EQUIVALENT = log(P(obs|lambda)) + log(P(lambda))
        double obs_lambda = super.obsLogProb(obss);
        if (use_bayes) {
            double p_lamda = Math.log(bird_num/total_bird_num);
            return obs_lambda + p_lamda;
        }
        return obs_lambda;
    }

    public Pair<Integer, Double> nextMovement(int[] obss) {
        HMM custom_bird_hmm = new HMM();
        custom_bird_hmm.init(super.states, super.emissions);
        matrixOps.copy_matrix(A, custom_bird_hmm.A);
        matrixOps.copy_matrix(B, custom_bird_hmm.B);
        matrixOps.copy_matrix(pi, custom_bird_hmm.pi);
        custom_bird_hmm.baumWelch(obss);
        return nextEmissionGivenObs(obss);
    }

    public void printSpecies() {
        System.err.print("Observations: ");
        System.err.println(observations.size());
        System.err.print("bird_num: ");
        System.err.println(bird_num);
        System.err.print("total_bird_num: ");
        System.err.println(total_bird_num);
        super.print_hmm();
    }
}