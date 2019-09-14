// Sorts birds by most likely
// Groups birds by similarity and tests 
public class BirdManager {
    public int states;
    public int emissions;

    private BirdModel[] bird_models; // Array of the model for each bird

    // COnstructor which loads num of states and emissions used
    public BirdManager(int s, int em) {
        states = s;
        emissions = em;
    }

    // Clears bird_model array
    // TODO(oleguer): Prepare stuff for new round
    public void start_round(int numBirds) {
        bird_models = new BirdModel[numBirds];
        for (int i = 0; i < numBirds; i++) {
            bird_models[i] = new BirdModel(states, emissions);
        }
    }

    public void finish_round() {
        return;
    }

    public void updateBirdObss(GameState pState) {
        for (int i = 0; i < bird_models.length; i++) {
            Bird bird = pState.getBird(i);
            if (bird.isAlive()) {
                bird_models[i].addObservation(bird.getLastObservation());
            }
        }
    }

    // Returns par: bird to shoot nad next expected move
    public Pair<Integer, Integer> bestShoot(GameState pState) {
        double max = Double.NEGATIVE_INFINITY;
        int max_bird = -1;
        for (int i = 0; i < bird_models.length; i++) {
            Bird bird = pState.getBird(i);
            if (bird.isAlive()) {
                bird_models[i].updateModel();
                if (Double.isNaN(bird_models[i].confidence)) {
                    System.err.print("Nan in confidence: ");
                    System.err.println(i);
                }
                // System.err.println();
                // System.err.print("max: ");
                // System.err.println(max);
                if (bird_models[i].confidence > max) {
                    max_bird = i;
                    max = bird_models[i].confidence;
                }
            }
        }
        Pair<Integer, Integer> shot_info = new Pair<Integer, Integer>();
        if (max_bird == -1) { // Didnt find maximum
            shot_info.first = -1;
            shot_info.second = -1;
            return shot_info;
        }

        int future_obs = bird_models[max_bird].predictMovement();
        // System.err.println(bird_models[i].confidence);
        // System.err.print("obs: ");
        // System.err.println(future_obs);
        shot_info.first = max_bird;
        shot_info.second = future_obs;
        return shot_info;
    }
}