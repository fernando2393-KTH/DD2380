import java.util.Arrays;

public class BirdModel extends HMM {
    public int groupID = -1;  // Grouped by similitude
    public int[] observations = new int[0];  // Array of seen observations until now
    public double confidence;  // Confidence of the model
    // OBS: confidence is negative and the highest, the better: log(prob)

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

    public void printBirdInfo() {
        System.err.println("----------V");
        super.print_hmm();
        System.err.println("OBSs:");
        System.err.println(Arrays.toString(observations));
        System.err.println("Conf:");
        System.err.println(confidence);
        System.err.println("############");
    }

    // Given another BirdModel returns the difference among them
    public double difference(BirdModel bird) {
        double dif = 0;
        dif += matrixOps.distance(super.A, bird.A);
        dif += matrixOps.distance(super.B, bird.B);
        // dif += matrixOps.distance(super.pi, bird.pi);
        // return dif/3;
        return dif/2;
    }
}