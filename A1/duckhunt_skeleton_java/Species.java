import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

public class Species {
    public ArrayList<Integer> observations;
    public int bird_num;
    public static int total_bird_num; // All the sspecies

    HMM shooting_model;
    HMM guessing_model;

    public Species(int shoot_st, int guess_st, int emi) {
        shooting_model = new HMM(shoot_st, emi);
        guessing_model = new HMM(guess_st, emi);
        guessing_model.is_guesser = true;
        observations = new ArrayList();
    }

    // Assigns bird to species
    public void appendObs(int[] obss) {
        for (int i = 0; i < obss.length; i ++) {
            observations.add(obss[i]);
        }
        bird_num++;
        total_bird_num++;
    }

    public void updateModels() {
        int obss[] = new int[observations.size()];
        int counter = 0;
        Iterator itr = observations.iterator();
        while (itr.hasNext()) {
            obss[counter] = (int) itr.next();
            counter++;
        }
        if (counter == 0) // No observations
            return;
        double guessing_confidence = guessing_model.baumWelch(obss);
        double shooting_confidence = shooting_model.baumWelch(obss);
    }

    // Returns Bayes logprob
    public double getSpeciesProb(int[] obss, boolean use_bayes) {
        // IDEA: P(lambda|obs) = P(obs|lambda)*P(lambda)/P(obs)
        // EQUIVALENT = log(P(obs|lambda)) + log(P(lambda))
        double obs_lambda = guessing_model.obsLogProb(obss);
        // if (use_bayes && bird_num > 0) {
        //     double p_lamda = Math.log(((double) bird_num)/((double) total_bird_num));
        //     return obs_lambda + p_lamda;
        // }
        return obs_lambda;
    }

    public Pair<Integer, Double> nextMovement(int[] obss) {
        // Deepcopy species shooting model
        HMM custom_bird_hmm = new HMM(shooting_model.states, shooting_model.emissions);
        custom_bird_hmm.A = matrixOps.get_matrix(shooting_model.A);
        custom_bird_hmm.B = matrixOps.get_matrix(shooting_model.B);
        custom_bird_hmm.pi = matrixOps.get_matrix(shooting_model.pi);
        // Fine-tune with given obss
        // shooting_model.print_hmm();
        // custom_bird_hmm.print_hmm();
        custom_bird_hmm.baumWelch(obss);
        // custom_bird_hmm.print_hmm();
        // System.exit(0);
        // return custom_bird_hmm.nextEmissionGivenObs(obss);
        // return custom_bird_hmm.nextEmissionGivenObs(obss);
        return custom_bird_hmm.nextMove(obss);
    }

    public void printSpecies() {
        System.err.print("Observations: ");
        System.err.println(observations.size());
        System.err.print("bird_num: ");
        System.err.println(bird_num);
        System.err.print("total_bird_num: ");
        System.err.println(total_bird_num);
        guessing_model.print_hmm();
    }
}