public class SortableGameState implements Comparable {
    public GameState gameState;
    public int score;
    public int pos;

    public SortableGameState(GameState gs, int sc, int p) {
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
