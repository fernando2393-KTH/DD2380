import java.util.Arrays;

public class BirdModel extends HMM {
    public int[] observations = new int[0];  // Array of seen observations until now
    public double confidence;  // Confidence of the model
    // OBS: confidence is negative and the highest, the better: log(prob)

    // Grouping variables
    public int groupID = -1;  // Grouped by similitude
    public int species = -1;  // Grouped by similitude

    // Class constructor (randomly initializes A, B, pi)
    public BirdModel(int states, int emissions) {
        super.A = matrixOps.randomMatrix(states, states, true);
        super.B = matrixOps.randomMatrix(states, emissions, true);
        super.pi = matrixOps.randomMatrix(1, states, false);
        super.states = states;
        super.emissions = emissions;
    }

    // Append an observation to BirdModel observations
    public void addObservation(int obs) {
        observations = matrixOps.push(observations, obs);
    }

    //Updates confidence and A, B, pi from linked HMM
    public void updateModel() {
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
        System.err.print(groupID);
        System.err.print(", Species: ");
        System.err.print(species);
        System.err.print(", Conf:");
        System.err.print(Math.round(confidence));
        // System.err.print(", OBSs:");
        // System.err.println(Arrays.toString(observations));
        if (print_matrices)
            super.print_hmm();
    }

    // Given another BirdModel returns the difference among them
    public double getDistance(BirdModel bird) {
        Pair<double[][], double[]> alpha_info = super.fwdAlgorithm(bird.observations);
        double[] ctes = alpha_info.second;

        double cte = 0;
        for (int i = 0; i < ctes.length; i++) {
            cte -= Math.log(ctes[i]);
        }
        
        // double dif = 0;
        // dif += matrixOps.distance(super.A, bird.A);
        // dif += matrixOps.distance(super.B, bird.B);
        // dif += matrixOps.distance(super.pi, bird.pi);
        // dif += matrixOps.distance(observations, bird.observations);
        return -cte;
        // return dif/2;
    }
}