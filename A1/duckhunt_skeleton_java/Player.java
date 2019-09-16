class Player {
    public static final Action cDontShoot = new Action(-1, -1);
    private int timestep;
    private BirdManager bird_manager;
    private BirdGuesser bird_guesser;
    private int[] sent_guesses;

    public static final int START_SHOOTING = 100;

    public Player() {
        timestep = 0;
        bird_manager = new BirdManager(3, 9);
        bird_guesser = new BirdGuesser(3, 9);
    }

    // Methods to run once the round is finished
    private void startRound(GameState pState) {
        bird_manager.start_round(pState.getNumBirds());
    }

    // Methods to run once the round is finished
    private void endRound() {
        timestep = 0;
        bird_manager.finish_round();
    }

    public Action shoot(GameState pState, Deadline pDue) {
        if (timestep == 0)
            startRound(pState);
        timestep += pState.getNumNewTurns(); // Update timestep count

        bird_manager.updateBirdObss(pState);

        if (timestep < START_SHOOTING)
            return cDontShoot;

        Pair<Integer, Integer> shot_info = bird_manager.bestShoot(pState, timestep);
        // System.err.print("t: ");
        // System.err.print(timestep);
        // System.err.print(" | ");
        // System.err.print(shot_info.first);
        // System.err.print(": ");
        // System.err.println(shot_info.second);
        // System.err.print(", points: ");
        // System.err.println(pState.myScore());
        if (shot_info.first == -1)
            return cDontShoot;
        return new Action(shot_info.first, shot_info.second);

        // // This line would predict that bird 0 will move right and shoot at it.
        // int min = 0;
        // int max = pState.getNumBirds();
        // int bird = (int)(Math.random() * ((max - min) + 1)) + min;
        // return new Action(bird, Constants.MOVE_RIGHT);
    }

    public int[] guess(GameState pState, Deadline pDue) {
        System.err.println("--------------------guessing: " + timestep);
        // speciesGuesser bird_guesser = new speciesGuesser();
        // bird_guesser.computeSimilarityMatrix(bird_manager.bird_models);
        // matrixOps.print_matrix(bird_guesser.similarities);
        bird_manager.updateBirdModels();
        // System.err.println(pDue.remainingMs());
        sent_guesses = new int[pState.getNumBirds()];
        sent_guesses = bird_guesser.getGuesses(bird_manager.bird_models);
        // System.err.println(pDue.remainingMs());

        // bird_guesser.printGrouping(bird_manager.bird_models, guesses);

        return sent_guesses;
    }

    /**
     * If you hit the bird you were trying to shoot, you will be notified through
     * this function.
     *
     * @param pState the GameState object with observations etc
     * @param pBird  the bird you hit
     * @param pDue   time before which we must have returned
     */
    public void hit(GameState pState, int pBird, Deadline pDue) {
        System.err.println("HIT BIRD!!!");
    }

    private void guessing_statistics(int[] real_vals) {
        int correct = 0;
        int error = 0;
        int unknown = 0;
        for (int i = 0; i < real_vals.length; i ++) {
            if (sent_guesses[i] == -1)
                unknown++;
            else if (sent_guesses[i] == real_vals[i])
                correct++;
            else
                error++;
        }
        System.err.print("Correct: ");
        System.err.print(correct);
        System.err.print(", Errors: ");
        System.err.print(error);
        System.err.print(", Unknown: ");
        System.err.println(unknown);
    }

    private void print_remaining(Deadline pDue) {
        System.err.print("MS Left:");
        System.err.println(pDue.remainingMs());
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
        // System.err.println("--------------------revealing: ");

        bird_guesser.manageRevelations(bird_manager.bird_models, pSpecies);
        // bird_guesser.printGrouping(bird_manager.bird_models, pSpecies);

        guessing_statistics(pSpecies);

        // System.err.print("###### PUNCTUATION: ");
        // System.err.println(pState.myScore());
        endRound();

        // if (pState.getRound() == 1)
        //     System.exit(0);
    }

}
