import java.util.*;

public class Algorithms {
    public static Hashtable<String, Integer> seen_states = new Hashtable<String, Integer>();
    // private int hashed_depth = 0;  // Up until which depth has been seen

    private final int WHITE_KING = Constants.CELL_WHITE | Constants.CELL_KING;
    private final int RED_KING = Constants.CELL_RED | Constants.CELL_KING;

    /* Constants for evaluation function */
    private final int PIECE_VALUE = 10;
    private final int KING_VALUE = 30;
    private final int COL_EDGE = 1;
    private final int BOTTOM_LINE = 2;
    private final int SAFE_KING = 1;

    /* Store player */
    public static int max_player;
    public static int min_player;
    
    public Integer iterativeDeepening(int max_depth, GameState gamestate, Deadline deadline, long time_limit, int player) {
        int best_action = 0;
        int best_action_score = Integer.MIN_VALUE;
        for (int depth = 1; depth < max_depth; depth++) {
            System.err.println("Depth: " + depth);
            Pair<Integer, Integer> action = alphabeta(gamestate, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, player);
            if (action.second > best_action_score) {
                best_action_score = action.second;
                best_action = action.first;
            }
            if (deadline.timeUntil() < time_limit) {
                System.err.println("Time limited!");
                return best_action;
            }
            System.err.println(seen_states);
        }
        return best_action;
    }

    public Pair<Integer, Integer> alphabeta(GameState gameState, int depth, int alpha, int beta, int player) {

        // Early return conditions
        if (gameState.isEOG()) {
            if (max_player == Constants.CELL_RED){ // Red player
                if (gameState.isRedWin()) {
                    seen_states.put(gameState.toMessage(), Integer.MAX_VALUE);
                    return new Pair<Integer, Integer>(0, Integer.MAX_VALUE);
                }
                if (gameState.isWhiteWin()) {
                    seen_states.put(gameState.toMessage(), Integer.MIN_VALUE);
                    return new Pair<Integer, Integer>(0, Integer.MIN_VALUE);
                }
            } else { // White player
                if (gameState.isRedWin()) {
                    seen_states.put(gameState.toMessage(), Integer.MIN_VALUE);
                    return new Pair<Integer, Integer>(0, Integer.MIN_VALUE);
                }
                if (gameState.isWhiteWin()) {
                    seen_states.put(gameState.toMessage(), Integer.MAX_VALUE);
                    return new Pair<Integer, Integer>(0, Integer.MAX_VALUE);
                }
            }
            seen_states.put(gameState.toMessage(), 0);
            return new Pair<Integer, Integer>(0, 0); // Else is draw
        }
        if (depth == 0)
            return new Pair<Integer, Integer>(0, heuristic(gameState));
    

        // Otherwise Explore other possible moves
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        // If tree values have been hashed, sort them
        int n_childs = nextStates.size();
        Vector<SortableGameState> states = new Vector<SortableGameState>();
        if (depth > 2) {
            for (int i = 0; i < n_childs; i++) {
                GameState nextState = nextStates.elementAt(i);
                String code = nextState.toMessage();
                
                // int score = Integer.MIN_VALUE;
                if (seen_states.containsKey(code)) {
                    int score = seen_states.get(code);
                    SortableGameState sgs = new SortableGameState(nextState, score, i);
                    states.add(sgs);
                }
                else {
                    System.err.println("Missing: " + code);
                }
            }
            Collections.sort(states);  // Sort
            System.err.println("D: " + depth);
            for (int i = 0; i < n_childs; i++) {
                System.err.print(states.get(i).score + ", ");
            }
            System.err.println(" ");
        }
        else if (depth <= 2) {  // Do not sort (could only use heuristics)
            for (int i = 0; i < n_childs; i++) {
                GameState nextState = nextStates.elementAt(i);
                states.add(new SortableGameState(nextState, 0, i));
            }
        }

        // System.err.println("Depth: " + depth);
        // for (int i = 0; i < n_childs; i++) {
        //     System.err.print(states.get(i).score + ", ");
        // }
        // System.err.println(" ");

        if (player == max_player) {  // MAX player
            int bestState = 0;
            int v = Integer.MIN_VALUE;
            for (int i = 0; i < n_childs; i++) {
                Pair<Integer, Integer> state_i = alphabeta(states.get(i).gameState, depth - 1, alpha, beta, min_player);
                if (state_i.second > v) {
                    v = state_i.second;
                    bestState = states.get(i).pos; // Assign pos previous to sorting
                }
                alpha = Math.max(alpha, v);
                if (beta <= alpha)
                    break;
            }
            // System.err.println(gameState.toMessage());
            // System.err.println(v);
            seen_states.put(gameState.toMessage(), v);
            // System.err.println(seen_states.containsKey(gameState.toMessage()));
            // System.err.println(seen_states.get(gameState.toMessage()));
            return new Pair<Integer, Integer>(bestState, v); // move, val
        } else {  // MIN player
            int bestState = 0;
            int v = Integer.MAX_VALUE;
            for (int i = n_childs - 1; i > - 1; i--) {
                Pair<Integer, Integer> state_i = alphabeta(states.get(i).gameState, depth - 1, alpha, beta, max_player);
                if (state_i.second < v) {
                    v = state_i.second;
                    bestState = states.get(i).pos;
                }
                beta = Math.min(beta, v);
                if (beta <= alpha)
                    break;
            }
            seen_states.put(gameState.toMessage(), v);
            return new Pair<Integer, Integer>(bestState, v); // move, val
        }
    }

    public int heuristic(GameState gamestate) {
        int number_of_whites = 0;
        int number_of_reds = 0;
        int white_kings = 0;
        int red_kings = 0;

        int aux = 0;
        for (int i = 0; i < 32; i++) {
            aux = gamestate.get(i);
            if (aux == Constants.CELL_EMPTY)
                continue;
            if (aux == Constants.CELL_WHITE)
                number_of_whites++;
            else if (aux == WHITE_KING)
                white_kings++;
            else if (aux == Constants.CELL_RED)
                number_of_reds++;
            else if (aux == RED_KING)
                red_kings++;
        }

        int whites = number_of_whites * PIECE_VALUE + white_kings * KING_VALUE;
        int reds = number_of_reds * PIECE_VALUE + red_kings * KING_VALUE;

        // System.err.println("Heuristic: +-" + (reds - whites));
        if (max_player == Constants.CELL_RED)
            return reds - whites;
        return whites - reds; 
    }
}