public class SortableGameState implements Comparable {
    public GameState gameState = new GameState();
    public double score;
    public int pos;

    public SortableGameState(GameState gs, double sc, int p) {
        gameState = gs;
        score = sc;
        pos = p;
    }

    @Override
    public int compareTo(Object that) {
        SortableGameState other = (SortableGameState) that;

        if (this.score > other.score)
            return -1;
        else if (this.score < other.score)
            return 1;
        else
            return 0;
    }
}
