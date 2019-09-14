class Player {
    public static final Action cDontShoot = new Action(-1, -1);
    private int timestep;
    private BirdManager bird_manager;

    public static final int START_SHOOTING = 60;

    public Player() {
        timestep = 0;
        bird_manager = new BirdManager(5, 9);
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


        if (timestep < 50)
            return cDontShoot;

        Pair<Integer, Integer> shot_info = bird_manager.bestShoot(pState);
        System.err.print("t: ");
        System.err.print(timestep);
        System.err.print(" | ");
        System.err.print(shot_info.first);
        System.err.print(": ");
        System.err.println(shot_info.second);
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
        System.err.println("guessing");
        System.exit(0);

        int[] lGuess = new int[pState.getNumBirds()];
        for (int i = 0; i < pState.getNumBirds(); ++i)
            lGuess[i] = Constants.SPECIES_UNKNOWN;
        return lGuess;
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

    /**
     * If you made any guesses, you will find out the true species of those birds
     * through this function.
     *
     * @param pState   the GameState object with observations etc
     * @param pSpecies the vector with species
     * @param pDue     time before which we must have returned
     */
    public void reveal(GameState pState, int[] pSpecies, Deadline pDue) {
    }

}
