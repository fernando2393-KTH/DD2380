// Class to sort and guess bird species
class speciesGuesser {

    public BirdModel[] quintessentials;
    public int[] species; //
    public Boolean[][] trials; // true if group i has attempted species j

    public double[][] similarities;

    public static final double MIN_RELATABLE = 5.5; // Minimum similarity to make them related

    public speciesGuesser() {

    }

    private void firstGroupping(BirdModel[] bird_models) {
        int birds = bird_models.length;
        int group = 0;
        for (int i = 0; i < birds; i++) {
            if (bird_models[i].groupID == -1) {
                for (int j = 0; j < birds; j++) {
                    if (similarities[i][j] > MIN_RELATABLE) {
                        bird_models[j].groupID = group;
                    }
                }
                group++;
            }
        }
        quintessentials = new BirdModel[groups];
        species = new int[groups];
        trials = new boolean[groups][6];
        for (int i = 0; i < groups; i++) {
            species[i] = -1; // Mark we dont know what species is each group
            double[][][] A = new double[0][bird_models[0].states][bird_models[0].states];
            double[][][] B = new double[0][bird_models[0].states][bird_models[0].emissions];
            double[][][] pi = new double[0][1][bird_models[0].states];
            for (int j = 0; j < birds; j++) {
                trials[i][j] = false;
                if (bird_models[j].groupID == i) {
                    A = matrixOPs.pushMat(A, bird_models[j].A);
                    B = matrixOPs.pushMat(B, bird_models[j].B);
                    pi = matrixOPs.pushMat(pi, bird_models[j].pi);
                }
            }
            quintessentials[i].A = matrixOPs.average(A);
            quintessentials[i].B = matrixOPs.average(B);
            quintessentials[i].pi = matrixOPs.average(pi);
        }

    }

    // 1 in (i, j) means they are equal, 0 they are totally different
    public void computeSimilarityMatrix(BirdModel[] bird_models) {
        int birds = bird_models.length;
        similarities = new double[birds][birds];

        for (int i = 0; i < birds; i++) {
            for (int j = i + 1; j < birds; j++) {
                similarities[i][j] = bird_models[i].difference(bird_models[j]);
            }
        }
    }

    private int[] getGuessings(BirdModel[] bird_models) {
        int birds = bird_models.length;
        int[] guesses = new int[birds];
        for (int i = 0; i < birds; i++) {
            int group = bird_models[i].groupID;
            if (groupID == -1) { // In case it doesnt have a category
                guesses[i] = -1;
            }
            if (species[group] != -1) { // If we know the species of this bird group, save and continue
                guesses[i] = species[group];
            } else { // If we dont know the species of this group
                for (int j = 0; j < 6; j++) {
                    Boolean species_used_in_another_group = false;
                    for (int k = 0; k < 6; ++k) {
                        if (species[k] == j) {
                            species_used_in_another_group = true;
                            break;
                        }
                    }
                    if (!species_used_in_another_group && !attempted[i][j]) { // If that species was not attempted
                        guesses[i] = j; // Attempt it
                        attempted[i][j] = true; // Mark it as attempted (make sure you try different ones)
                    }
                }
            }
        }
        return guesses;
    }

    private void checkGuessings(BirdModel[] bird_models, int[] pSpecies) {
        int birds = bird_models.length;
        int[] guesses = new int[birds];
        for (int i = 0; i < birds; i++) {
            if (pSpecies[i] != -1) { // If we get useful information
                species[bird_models[i].groupID] = pSpecies[i]; // TODO(oleguer): Consider case collision, split into new
                                                               // group
            }
        }
    }

    // Returns group of new birdModel, creates one if doesn match any
    public void groupNew(BirdModel bird_model) {
        for (int i = 0; i < quintessentials.length; i++) {
            if (bird_model.difference(quintessentials[i]) < MIN_RELATABLE) {
                bird_model.groupID = i;
            }
        }
        if (bird_model.groupID == -1) { // If didnt find close enough group, create new one

        }
    }

    public int[] firstRoundGuess(BirdModel[] bird_models) {
        computeSimilarityMatrix(bird_models);
        firstGroupping(bird_models);
    }

    public int[] middleRoundGuess(BirdModel[] bird_models) {

    }

}