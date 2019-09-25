import java.util.*;
import java.lang.*;
import java.io.*;

public class GameStateComparer implements Comparator<GameState> {
    public Hashtable<String, Integer> seen_states = new Hashtable<String, Integer>();

    @Override
    public int compare(GameState x, GameState y) {
        String x_code = x.toMessage();
        String y_code = y.toMessage();
        if (!seen_states.contains(x_code) || !seen_states.contains(y_code))
            return 0;
        int x_value = seen_states.get(x_code);
        int y_value = seen_states.get(y_code);
        if (x_value > y_value)
            return -1;
        else if (x_value < y_value)
            return 1;
        else
            return 0;
    }
}