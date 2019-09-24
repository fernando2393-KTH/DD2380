import java.util.*;
import java.lang.*;
import java.io.*;

public class GameStateComparer implements Comparator<GameState> {
    private Algorithms alg = new Algorithms();
    public int player;

    @Override
    public int compare(GameState x, GameState y) {
        int x_value = alg.evaluation3d(x, player);
        int y_value = alg.evaluation3d(y, player);
        if (x_value > y_value)
            return -1;
        else if (x_value < y_value)
            return 1;
        else
            return 0;
    }
}