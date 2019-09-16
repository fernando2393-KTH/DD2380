import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

// Class to hold group of birds
class BirdGroup extends BirdModel {
    public int species; // Bird id representing group
    public boolean[] tried; // True if species i has been ettempted to guess (to make different guesses)

    public ArrayList<BirdModel> birds_grouped;
    public static Map<Integer, BirdGroup> species_to_birdgroup = new HashMap<>();

    public BirdGroup(int species_id, int st, int emi) {
        super(st, emi);
        species = species_id;        
        tried = new boolean[6];
        birds_grouped = new ArrayList<BirdModel>();
    }

    // Updates general
    public void update() {
        int num_birds = birds_grouped.size();
        if (num_birds == 0)
            return;
        double[][][] Aac = new double[num_birds][super.states][super.states];
        double[][][] Bac = new double[num_birds][super.states][super.emissions];
        double[][][] piac = new double[num_birds][1][super.states];
        for (int k = 0; k < num_birds; k++)
            for (int i = 0; i < super.states; ++i)
                for (int j = 0; j < super.states; ++j)
                    Aac[k][i][j] = birds_grouped.get(k).A[i][j];
        for (int k = 0; k < num_birds; k++)
            for (int i = 0; i < super.states; ++i)
                for (int j = 0; j < super.emissions; ++j)
                    Bac[k][i][j] = birds_grouped.get(k).B[i][j];
        for (int k = 0; k < num_birds; k++)
            for (int i = 0; i < super.states; ++i)
                piac[k][0][i] = birds_grouped.get(k).pi[0][i];

        super.A = matrixOps.average(Aac);
        super.B = matrixOps.average(Bac);
        super.pi = matrixOps.average(piac);
    }

    // Appends bird to group
    public void appendBird(BirdModel bird, boolean update_model) {
        // If bird already in this group
        if (bird.group == this)
            return;
        
        // If wrong group
        if (bird.species != -1 && species != -1 && bird.species != species) {
            System.err.println("Wrong bird in this group");
            return;
        }

        // Remove from other groups
        // if (bird.group != null && bird.group != this) {
        //     bird.group.removeBird(bird, false);
        // }

        // Assign bird group to this one
        bird.group = this;
        
        // Add to list of birds in group
        if (!birds_grouped.contains(bird))
            birds_grouped.add(bird);

        // If bird has species, set species for the group
        if (bird.species != -1) {
            setSpecies(bird.species);
        }
        // If first group update
        if (birds_grouped.size() == 1) {
            update_model = true;
        }
        if (update_model)
            update();
    }

    // Removes a bird from group (VERY IMPROVABLE)
    public void removeBird(BirdModel bird, boolean update_model) {
        if (!birds_grouped.contains(bird)) {
            return;
        }
        bird.group = null;
        Iterator itr = birds_grouped.iterator();
        while (itr.hasNext()) {
            BirdModel b = (BirdModel) itr.next();
            if (bird == b) {
                itr.remove();
            }
        }
        if (update_model)
            update();
    }

    public boolean hasBird(BirdModel bird) {
        return birds_grouped.contains(bird);
    }

    // Returns spcies if known, a value to try otherwise
    public int assignSpecies() {
        if (species != -1)
            return species;
        for (int i = 0; i < tried.length; i++) // If species not in any other group and not tried in this one
            if (!species_to_birdgroup.containsKey(i) && !tried[i]) {
                tried[i] = true;
                return i;
            }
        for (int i = 0; i < tried.length; i++) // Relaxed version
            if (!species_to_birdgroup.containsKey(i))
                return i;
        return 0;
    }

    public void setSpecies(int species_id) {
        // System.err.print(groupID);
        // System.err.print(" <- ");
        // System.err.println(species_id);

        if (species_to_birdgroup.containsKey(species_id))
            if (species_to_birdgroup.get(species_id) != this) { // Move all birds there
                System.err.println("Crash!");
                BirdGroup birdgroup = species_to_birdgroup.get(species_id);
                Iterator itr = birds_grouped.iterator();
                while (itr.hasNext()) {
                    BirdModel bird =(BirdModel) itr.next();
                    birdgroup.appendBird(bird, false);
                }
                birds_grouped = new ArrayList<BirdModel>(); // Remove all elements from this one

                return;
            }

        species = species_id;
        species_to_birdgroup.put(species_id, this);
        Iterator itr = birds_grouped.iterator();
        while (itr.hasNext()) {
            BirdModel bird =(BirdModel) itr.next();
            bird.species = species;
        }
    }

    public void printGroup(ArrayList<BirdGroup> bird_groups) {
        System.err.print("Group: ");
        System.err.println(this.hashCode());
        System.err.print("Species: ");
        System.err.println(species);
        System.err.print("Birds: ");
        System.err.println(birds_grouped.size());
        Iterator itr = birds_grouped.iterator();
        while (itr.hasNext()) {
            BirdModel bm = (BirdModel) itr.next();
            bm.printBirdInfo(false);
            Iterator itr2 = bird_groups.iterator();
            System.err.print(", Distances: ");
            while (itr2.hasNext()) {
                BirdGroup bg = (BirdGroup) itr2.next();
                System.err.print(Math.round(bg.getDistance(bm)*10000.)/100.);
                System.err.print(" ");
            }
            System.err.println();
        }
        System.err.println("############");
    }

    // Returns minimal distance between 
    public double minimumDistance(BirdModel bird) {
        // double min = Double.POSITIVE_INFINITY;
        // Iterator itr = birds_grouped.iterator();
        // while (itr.hasNext()) {
        //     BirdModel bm = (BirdModel) itr.next();
        //         if (bm.species == species) {
        //         double distance = bm.getDistance(bird);
        //         if (distance < min) {
        //             min = distance;
        //         }
        //     }
        // }
        // return min;
        // return birds_grouped.get(0).getDistance(bird);
        return super.getDistance(bird);
    }

}