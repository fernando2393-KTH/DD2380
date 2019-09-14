public class BirdModel extends HMM {
    public static int[] observations;  // Array of seen observations until now
    public static double confidence;  // Confidence of the model
    // OBS: confidence is negative and the highest, the better: log(prob)

    // Class constructor (randomly initializes A, B, pi)
    public BirdModel(int states, int emissions) {
        super.A = matrixOps.randomMatrix(states, states);
        super.B = matrixOps.randomMatrix(states, emissions);
        super.pi = matrixOps.randomMatrix(1, states);
        super.states = states;
        super.emissions = emissions;
    }

    // Append an observation to BirdModel observations
    public static void addObservation(int obs) {
        observations = matrixOps.push(observations, obs);
    }

    //Updates confidence and A, B, pi from linked HMM
    public void updateModel() {
        confidence = super.baumWelch(observations);
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
        for (int i = 0; i < super.states; i++) {
            if (emission_prob[0][i] > max) {
                max = emission_prob[0][i];
                max_index = i;
            }
        }
        return max_index;
    }
}