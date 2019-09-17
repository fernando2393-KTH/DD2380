import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

// Class to sort and guess bird species
class BirdGuesser {
    public ArrayList<BirdGroup> bird_groups;

    public double MIN_RELATABLE = 70; // Minimum similarity to make them related

    public int states;
    public int emissions;

    // Constructor
    public BirdGuesser(int st, int emi) {
        states = st;
        emissions = emi;
        bird_groups = new ArrayList<BirdGroup>();
    }

    public BirdGroup appendNewGroup(BirdModel bird) {
        BirdGroup bird_group = new BirdGroup(bird.species, states, emissions);
        bird_group.appendBird(bird, true);
        bird_groups.add(bird_group);
        bird.groupDistance = 0;
        return bird_group;
    }

    // Returns (birdGroup, distance)
    public Pair<BirdGroup, Double> getBirdGroup(BirdModel bird) {
        Pair<BirdGroup, Double> result = new Pair<BirdGroup, Double>();
        result.first = null;

        if (bird.group != null) {
            result.first = bird.group;
            result.second = bird.groupDistance;
            return result;
        }

        double min = Double.POSITIVE_INFINITY;
        Iterator itr = bird_groups.iterator();
        while (itr.hasNext()) {
            BirdGroup bg = (BirdGroup) itr.next();
            double distance = bg.minimumDistance(bird);
            if (distance < min) {
                min = distance;
                // if (bird_groups.get(i).species == -1)
                //     min = 2*min; // This will enhance grouping into existing ones
                result.first = bg;
            }
        }
        result.second = min;
        if (min > MIN_RELATABLE)
            result.first = null;
        return result;
    }

    // Appends unguessed bird into closest group (or new created)
    public void appendUnknownBird(BirdModel bird) {
        Pair<BirdGroup, Double> bird_group_info = getBirdGroup(bird);
        if (bird_group_info.first != null) { // If group close enough
            bird_group_info.first.appendBird(bird, true);
            bird.groupDistance = bird_group_info.second;
        } else { // Create new group
            BirdGroup new_group = appendNewGroup(bird);
        }
    }

    public void appendKnownBird(BirdModel bird, int species_id) {
        if (bird_groups.isEmpty()) { // Case no groups in bird_groups
            BirdGroup new_group = appendNewGroup(bird);
            return;
        }
        if (bird_groups.get(0).species_to_birdgroup.containsKey(species_id)) { // Case group with that id exists
            BirdGroup bird_group = bird_groups.get(0).species_to_birdgroup.get(species_id);
            bird_group.appendBird(bird, true);
        } else { // Case no group with that id is found (GET CLOSEST WITH ID 0)
            double min = Double.POSITIVE_INFINITY;
            BirdGroup min_dist_bg = null;
            Iterator itr = bird_groups.iterator();
            while (itr.hasNext()) {
                BirdGroup bg = (BirdGroup) itr.next();
                if (bg.species == -1) {
                    double distance = bg.minimumDistance(bird);
                    if (distance < min) {
                        min = distance;
                        min_dist_bg = bg;
                    }
                }
            }
            if (min < MIN_RELATABLE) { // Case there is a close one without category
                min_dist_bg.appendBird(bird, true);
                bird.groupDistance = min;
            } else { // Case we have to create a new group
                BirdGroup new_group = appendNewGroup(bird);
            }
        }

    }

    public int[] getGuesses(BirdModel[] birds) {
        int[] guesses = new int[birds.length];
        for (int i = 0; i < birds.length; i++) {
            Pair<BirdGroup, Double> bird_group_info = getBirdGroup(birds[i]);
            if (bird_group_info.first != null) { // Case no close group found
                bird_group_info.first.appendBird(birds[i], true);
            } else {
                bird_group_info.first = appendNewGroup(birds[i]);
            }
            guesses[i] = bird_group_info.first.assignSpecies();
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
        for (int i = 0; i < n_birds; i++) {
            BirdModel bird = birds[i];
            int real_species = pSpecies[i];
            BirdGroup bird_group = bird.group;

            // Save bird species
            bird.species = real_species;
            if (bird.species == -1) {// If unknown, remove
                if (bird.group != null) {
                    bird.group.removeBird(bird, true);
                }
            }
            if (bird_group == null) { // Case bird didnt have group (shouldnt happen)
                appendKnownBird(bird, real_species);
                continue;
            }
            if (bird_group.species == -1) { // Case we didnt know group species
                bird_group.setSpecies(real_species);
                continue;
            }
            if (bird_group.species != real_species) { // Case we did a mistake
                bird_group.removeBird(bird, true);
                appendKnownBird(bird, real_species);
                continue;
            }
        }
        updateGroups();
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
    }
}