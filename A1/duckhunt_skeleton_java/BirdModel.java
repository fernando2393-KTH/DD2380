import java.util.Arrays;

public class BirdModel extends HMM {
    public int[] observations = new int[0];  // Array of seen observations until now
    public double confidence;  // Confidence of the model
    // OBS: confidence is negative and the highest, the better: log(prob)

    // Grouping variables
    public BirdGroup group = null;  // Grouped by similitude
    public double groupDistance = Double.POSITIVE_INFINITY;
    public int species = -1;  // Bird species

    // Class constructor (randomly initializes A, B, pi)
    public BirdModel(int states, int emissions) {
        super.A = matrixOps.randomMatrix(states, states, true);
        super.B = matrixOps.randomMatrix(states, emissions, false);
        super.pi = matrixOps.randomMatrix(1, states, false);
        super.states = states;
        super.emissions = emissions;
    }

    // Resets and recomputes matrices (usefull when nans appear)
    public void randomReset() {
        super.A = matrixOps.randomMatrix(super.states, super.states, true);
        super.B = matrixOps.randomMatrix(super.states, super.emissions, false);
        super.pi = matrixOps.randomMatrix(1, super.states, false);
        updateModel();
    }

    // Append an observation to BirdModel observations
    public void addObservation(int obs) {
        observations = matrixOps.push(observations, obs);
    }

    //Updates confidence and A, B, pi from linked HMM
    public void updateModel() {
        // System.err.print("OBSs: ");
        // System.err.println(observations.length);
        confidence = super.baumWelch(observations);
    }

    public void updateModelDebug() {
        confidence = super.baumWelchDebug(observations);
    }
    
    // Predicts bird's next movement
    public int predictMovement() {
        int T = observations.length;

        // This is VEEERY improvable
        double[][] Aacc = super.A;
        for (int t = 1; t < T; t++) {
            Aacc = matrixOps.multiply(Aacc, A);
        }
        double[][] state_prob = matrixOps.multiply(pi, Aacc);
        double[][] emission_prob = matrixOps.multiply(state_prob, B);

        // Get the highest movement probability
        double max = -1;
        int max_index = -1;
        for (int i = 0; i < super.emissions; i++) {
            // System.err.println(emission_prob[0][i]);
            // System.err.print("max: ");
            // System.err.println(max);
            if (emission_prob[0][i] > max) {
                max = emission_prob[0][i];
                max_index = i;
            }

        }
        // System.exit(0);
        return max_index;
    }

    public void printBirdInfo(boolean print_matrices) {
        System.err.print("Hash: ");
        System.err.print(this.hashCode());
        System.err.print("  Group: ");
        if (group != null)
            System.err.print(group.hashCode());
        System.err.print(", Species: ");
        System.err.print(species);
        System.err.print(", Conf:");
        System.err.println(Math.round(confidence));
        // System.err.print(", OBSs:");
        // System.err.println(Arrays.toString(observations));
        if (print_matrices)
            super.print_hmm();
    }

    // Given another BirdModel returns the difference among them
    public double getDistance(BirdModel bird) {
        double original_logprob = bird.obsLogProb(bird.observations);
        double this_logprob = super.obsLogProb(bird.observations);
        if (Double.isNaN(original_logprob))
            System.err.print("non original");
        if (Double.isNaN(this_logprob)){

            // System.err.println("non this");
            // this_logprob = Double.NEGATIVE_INFINITY;
            // printBirdInfo(true);
            // System.exit(0);

        }

        // return -this_logprob;
        // System.err.println(Math.abs((original_logprob - this_logprob)/original_logprob));
        return Math.abs((original_logprob - this_logprob)/original_logprob);
    }

}