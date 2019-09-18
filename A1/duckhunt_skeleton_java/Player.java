import java.util.Arrays;

class Player {
    private Species[] species;

    private int timestep;
    private int[] sent_guesses;

    public static final int SHOOTING_STATES = 2;
    public static final int GUESSING_STATES = 1;
    public static final double START_SHOOTING_TIMESTEP = 77;
    public static final double START_SHOOTING_ROUND = 1;
    public static final double SHOOT_THRESHOLD = 0.68;
    public static final double USE_BAYES_ROUND = 3;
    public static final Action cDontShoot = new Action(-1, -1);

    public int shots = 0;
    public int hits = 0;
    public int correct_guesses = 0;
    public int failed_guesses = 0;

    public Player() {
        species = new Species[6];
        for (int i = 0; i < species.length; i++) {
            species[i] = new Species(SHOOTING_STATES, GUESSING_STATES, 9);
        }
    }

    private int[] getBirdObservations(Bird bird) {
        int[] bird_observations = new int[bird.getSeqLength()];
        for (int i = 0; i < bird.getSeqLength(); i++) {
            bird_observations[i] = bird.getObservation(i);
            if (bird_observations[i] == -1) {
                return Arrays.copyOfRange(bird_observations, 0, i - 1);
            }
        }
        return bird_observations;
    }

    private int mostLikelySpecies(int[] obss, boolean use_bayes){
        double max = Double.NEGATIVE_INFINITY;
        int max_pos = -1;
        for (int j = 0; j < species.length; j++) {
            double value = species[j].getSpeciesProb(obss, use_bayes);
            if (value > max) {
                max = value;
                max_pos = j;
            }
        }
        return max_pos;
    }

    // Retruns true if all species have birds
    private boolean allSpecies() {
        for (int i = 0; i < species.length; i++) {
            if (species[i].bird_num == 0)
                return false;
        }
        return true;
    }

    public Action shoot(GameState pState, Deadline pDue) {
        timestep += pState.getNumNewTurns(); // Update timestep count
        boolean use_bayes = allSpecies() && (pState.getRound() > USE_BAYES_ROUND);

        if (timestep > START_SHOOTING_TIMESTEP && pState.getRound() > START_SHOOTING_ROUND) {
            int num_birds = pState.getNumBirds();
            double max_prob = -1;
            int movement = -1;
            int bird_to_shoot = -1;
            for (int i = 0; i < num_birds; i++) {
                if (pState.getBird(i).isAlive()) {

                    // 1. Identify black stork
                    int[] obss = getBirdObservations(pState.getBird(i));
                    // System.err.println("OBSERVATIONS");
                    // for (int j = 0; j < obss.length; j++) {
                    //     System.err.println(obss[j]);
                    // }

                    int bird_species = mostLikelySpecies(obss, use_bayes);
                    if (bird_species == Constants.SPECIES_BLACK_STORK) {
                        continue;
                    }

                    // 2. For the rest, compute most likely next movement
                    Pair<Integer, Double> move_info = species[bird_species].nextMovement(obss);
                    if (move_info.second > max_prob) {
                        max_prob = move_info.second;
                        movement = move_info.first;
                        bird_to_shoot = i;
                    }
                }

                // 3. Shoot most certain
                // System.err.print("Shooting: ");
                // System.err.println(max_prob);
                if (max_prob > SHOOT_THRESHOLD) {
                    shots++;
                    System.err.print("FIRE! Prob: ");
                    System.err.print(Math.round(max_prob * 100));
                    System.err.print(", Bird: ");
                    System.err.println(bird_to_shoot);
                    return new Action(bird_to_shoot, movement);
                }

            }
        }

        return cDontShoot;
    }

    public int[] guess(GameState pState, Deadline pDue) {
        int birds_num = pState.getNumBirds();
        
        sent_guesses = new int[birds_num];
        if(pState.getRound() == 0) {
            // for (int i = 0; i < birds_num; i++)
            //     sent_guesses[i] = -1;
            return sent_guesses;
        }

        boolean use_bayes = allSpecies() && (pState.getRound() > USE_BAYES_ROUND);
        // if (use_bayes) 
        //     System.err.print("Using bayes");

        for (int i = 0; i < birds_num; i++) {
            Bird bird = pState.getBird(i);
            int[] obss = getBirdObservations(bird);
            sent_guesses[i] = mostLikelySpecies(obss, use_bayes); // Find closest species
        }
        return sent_guesses;
    }

    public void hit(GameState pState, int pBird, Deadline pDue) {
        hits++;
        System.err.println("HIT BIRD!!!");
    }

    private void guessingStatistics(int[] real_vals) {
        int correct = 0;
        int error = 0;
        int unknown = 0;
        for (int i = 0; i < real_vals.length; i++) {
            if (sent_guesses[i] == -1)
                unknown++;
            else if (sent_guesses[i] == real_vals[i])
                correct++;
            else
                error++;
        }
        correct_guesses +=correct;
        failed_guesses += error;

        System.err.print("Sent: ");
        for (int i = 0; i < real_vals.length; i++) {
            System.err.print(sent_guesses[i]);
            System.err.print(" ");
        }
        System.err.println();
        System.err.print("Gott: ");
        for (int i = 0; i < real_vals.length; i++) {
            System.err.print(real_vals[i]);
            System.err.print(" ");
        }
        System.err.println();
        System.err.print("Correct: ");
        System.err.print(correct);
        System.err.print(", Errors: ");
        System.err.print(error);
        System.err.print(", Unknown: ");
        System.err.println(unknown);
    }

    /**
     * If you made any guesses, you will find out the true species of those birds
     * through this function.
     *
     * @param pState   the GameState object with observations etc
     * @param pSpecies the vector with species
     * @param pDue     time before which we must have returned
     */
    public void reveal(GameState pState, int[] pSpecies, Deadline pDue) {
        timestep = 0;
        guessingStatistics(pSpecies);
        System.err.println("SHOOTING: SHOTS: " + shots + ", HITS: " + hits + ", POINTS: " + (hits - (shots - hits)));
        System.err.println("GUESSING: CORRECT: " + correct_guesses + ", FAILED: " + failed_guesses + ", POINTS: " + (correct_guesses - failed_guesses));

        // Assign Birds
        int birds_num = pState.getNumBirds();
        for (int i = 0; i < birds_num; i++) {
            Bird bird = pState.getBird(i);
            int[] obss = getBirdObservations(bird);
            int real_species = pSpecies[i];
            species[real_species].appendObs(obss);
        }
        // Update species
        for (int i = 0; i < species.length; i++) {
            species[i].updateModels();
        }
        // System.exit(0);
    }

}
