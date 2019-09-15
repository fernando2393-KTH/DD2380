import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

// Class to sort and guess bird species
class BirdGuesser {
    public ArrayList<BirdGroup> bird_groups;

    public double MIN_RELATABLE = 200; // Minimum similarity to make them related

    public int states;
    public int emissions;

    // Constructor
    public BirdGuesser(int st, int emi) {
        states = st;
        emissions = emi;
        bird_groups = new ArrayList<BirdGroup>();
    }

    // Return group of id id
    public BirdGroup getGroup(int id) {
        Iterator itr = bird_groups.iterator();
        while (itr.hasNext()) {
            BirdGroup bg = (BirdGroup) itr.next();
            if (bg.groupID == id)
                return bg;
        }
        return bird_groups.get(0);
    }

    public void appendNewGroup(BirdModel bird) {
        BirdGroup bird_group = new BirdGroup(bird_groups.size(), bird.species, states, emissions);
        bird_group.appendBird(bird, true);
        bird_groups.add(bird_group);
    }

    // Returns (birdGroup, distance)
    public Pair<Integer, Double> getBirdGroup(BirdModel bird) {
        Pair<Integer, Double> result = new Pair<Integer, Double>();
        result.first = -1;

        double min = Double.POSITIVE_INFINITY;
        int min_index = -1;
        for (int i = 0; i < bird_groups.size(); ++i) {
            if (bird_groups.get(i).hasBird(bird)) { // Case bird is already in group
                min = bird_groups.get(i).minimumDistance(bird);
                min_index = i;
                System.err.print("birdingroup");
                break;
            }
            double distance = bird_groups.get(i).minimumDistance(bird);
            if (distance < min) {
                min = distance;
                // if (bird_groups.get(i).species == -1)
                //     min = 2*min; // This will enhance grouping into existing ones
                min_index = i;
            }
        }
        result.second = min;
        if (min < MIN_RELATABLE)
            result.first = min_index;
        return result;
    }

    // Appends unguessed bird into closes group (creates new one if none are close
    // enough)
    public void appendUnknownBird(BirdModel bird) {
        Pair<Integer, Double> bird_group = getBirdGroup(bird);
        if (bird_group.first != -1) { // If group close enough
            bird_groups.get(bird_group.first).appendBird(bird, true);
        } else { // Create new group
            appendNewGroup(bird);
        }
    }

    public void appendKnownBird(BirdModel bird, int species_id) {
        if (bird_groups.size() == 0) { // Case no groups in bird_groups
            appendNewGroup(bird);
            return;
        }
        if (bird_groups.get(0).species_to_birdgroup.containsKey(species_id)) { // Case group with that id exists
            BirdGroup bird_group = bird_groups.get(0).species_to_birdgroup.get(species_id);
            bird_group.appendBird(bird, true);
        } else { // Case no group with that id is found
            double min = Double.POSITIVE_INFINITY;
            int min_index = -1;
            for (int i = 0; i < bird_groups.size(); ++i) {
                BirdGroup bird_group = bird_groups.get(i);
                if (bird_group.species == -1) { // Consider only unknow species group
                    double distance = bird_group.minimumDistance(bird);
                    if (distance < min) {
                        min = distance;
                        min_index = i;
                    }
                }
            }
            if (min < MIN_RELATABLE) { // Case there is a close one without category
                bird_groups.get(min_index).appendBird(bird, true);
            } else { // Case we have to create a new group
                appendNewGroup(bird);
            }
        }

    }

    public int[] getGuesses(BirdModel[] birds) {
        int[] guesses = new int[birds.length];
        for (int i = 0; i < birds.length; i++) {
            Pair<Integer, Double> bird_group = getBirdGroup(birds[i]);
            if (bird_group.first != -1) { // Case no close group found
                bird_groups.get(bird_group.first).appendBird(birds[i], true);
            } else {
                appendNewGroup(birds[i]);
                bird_group.first = bird_groups.size() - 1;
            }
            guesses[i] = bird_groups.get(bird_group.first).assignSpecies();
        }
        return guesses;
    }

    private void updateGroups() {
        Iterator itr = bird_groups.iterator();
        while (itr.hasNext()) {
            BirdGroup bg = (BirdGroup) itr.next();
            if (bg.birds_grouped.isEmpty()) {
                itr.remove(); // Remove empty groups
            } else {
                bg.update(); // Update new ones
            }
        }
    }

    public void manageRevelations(BirdModel[] birds, int[] pSpecies) {
        int n_birds = birds.length;
        int error = 0;
        int unknown = 0;
        for (int i = 0; i < n_birds; i++) {
            BirdModel bird = birds[i];
            int real_species = pSpecies[i];
            int group_id = bird.groupID;

            // Save bird species
            bird.species = real_species;
            if (group_id == -1) { // Case bird didnt have group (shouldnt happen)
                appendKnownBird(bird, real_species);
                continue;
            }
            BirdGroup bird_group = getGroup(group_id);
            if (bird_group.species == -1) { // Case we didnt know group species
                unknown++;
                bird_group.setSpecies(real_species);
                continue;
            }
            if (bird_group.species != real_species) { // Case we did a mistake
                error++;
                bird_group.removeBird(bird, true);
                appendKnownBird(bird, real_species);
                continue;
            }
        }
        updateGroups();
        System.err.print("Errors: ");
        System.err.print(error);
        System.err.print(", Unknown: ");
        System.err.println(unknown);
    }

    public void printGrouping(BirdModel[] bird_model, int[] assignments) {
        System.err.print("GROUPPING: (");
        System.err.print(bird_model.length);
        System.err.println(" birds)");
        Iterator itr = bird_groups.iterator();
        while (itr.hasNext()) {
            BirdGroup bg = (BirdGroup) itr.next();
            bg.printGroup(bird_groups);
        }
        // for (int i = 0; i < bird_model.length; i++) {
        // bird_model[i].printBirdInfo(false);
        // Iterator itr2 = bird_groups.iterator();
        // System.err.print("Distances: ");
        // while (itr2.hasNext()) {
        // BirdGroup bg = (BirdGroup) itr2.next();
        // System.err.print(Math.round(bg.getDistance(bird_model[i])*100.)/100.);
        // System.err.print(" ");
        // }
        // System.err.println();

        // }
    }
}