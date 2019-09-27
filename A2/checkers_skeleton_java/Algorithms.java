import java.util.*;

public class Algorithms {
    public Hashtable<String, Integer> max_states = new Hashtable<String, Integer>();
    public Hashtable<String, Integer> min_states = new Hashtable<String, Integer>();

    private final int WHITE_KING = Constants.CELL_WHITE | Constants.CELL_KING;
    private final int RED_KING = Constants.CELL_RED | Constants.CELL_KING;

    /* Constants for evaluation function */
    private final int PIECE_VALUE = 10;
    private final int KING_VALUE = 30;
    private final int COL_EDGE = 1;
    private final int BOTTOM_LINE = 2;
    private final int SAFE_KING = 1;

    /* Store player */
    public int max_player;
    public int min_player;
    
    public Integer iterativeDeepening(int max_depth, GameState gamestate, Deadline deadline, long time_limit) {
        int best_action = 0;
        int best_action_score = Integer.MIN_VALUE;
        Pair<Integer, Integer> action =null;
        for (int depth = 1; depth < max_depth; depth++) {
            // System.err.println("Depth: " + depth);

            action = alphabeta(gamestate, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, max_player);
            if (action.second > best_action_score) {
                best_action_score = action.second;
                best_action = action.first;
            }
            if (deadline.timeUntil() < time_limit) {
                // System.err.println("Time limited!");
                return best_action;
            }
            // System.err.println(max_states);
            // System.err.println(min_states);
        }
        System.err.println("Action: " + best_action);
        // Pair<Integer, Integer> action = alphabeta(gamestate, max_depth, Integer.MIN_VALUE, Integer.MAX_VALUE, max_player);
        return best_action;
    }

    public Pair<Integer, Integer> alphabeta(GameState gameState, int depth, int alpha, int beta, int player) {
        // System.err.print("Depth: " + depth + " ");
        // System.err.println("Analizing: " + gameState.toMessage().split(" ")[0]);

        // Early return conditions
        if (gameState.isEOG()) {
            if (max_player == Constants.CELL_RED){ // Red player
                if (gameState.isRedWin()) {
                    max_states.put(gameState.toMessage().split(" ")[0], Integer.MAX_VALUE/2);
                    return new Pair<Integer, Integer>(0, Integer.MAX_VALUE/2);
                }
                if (gameState.isWhiteWin()) {
                    max_states.put(gameState.toMessage().split(" ")[0], Integer.MIN_VALUE/2);
                    return new Pair<Integer, Integer>(0, Integer.MIN_VALUE/2);
                }
            } else { // MIN player
                if (gameState.isRedWin()) {
                    min_states.put(gameState.toMessage().split(" ")[0], Integer.MIN_VALUE/2);
                    return new Pair<Integer, Integer>(0, Integer.MIN_VALUE/2);
                }
                if (gameState.isWhiteWin()) {
                    min_states.put(gameState.toMessage().split(" ")[0], Integer.MAX_VALUE/2);
                    return new Pair<Integer, Integer>(0, Integer.MAX_VALUE/2);
                }
            }
            max_states.put(gameState.toMessage().split(" ")[0], 0);
            min_states.put(gameState.toMessage().split(" ")[0], 0);
            return new Pair<Integer, Integer>(0, 0); // Else is draw
        }

        if (depth == 0)
            return new Pair<Integer, Integer>(0, evaluate(gameState));
    

        // Otherwise Explore other possible moves
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        // If tree values have been hashed, sort them
        int n_childs = nextStates.size();
        Vector<SortableGameState> states = new Vector<SortableGameState>();
        if (depth > 2) {
            GameState nextState = null;
            String code = "";
            int score;
            for (int i = 0; i < n_childs; i++) {
                nextState = nextStates.elementAt(i);
                code = nextState.toMessage().split(" ")[0];
                score = Integer.MIN_VALUE/3;
                if (player == max_player) // Means your children are min
                    if (min_states.containsKey(code)) {
                        score = min_states.get(code);
                        // System.err.println("score:" + score);
                    }
                if (player == min_player)  // Means your children are max
                    score = Integer.MAX_VALUE/3;
                    if (max_states.containsKey(code))
                        score = max_states.get(code);
                // SortableGameState sgs = new SortableGameState(nextState, score, i);
                states.add(new SortableGameState(nextState, score, i));
                // }
                // else {
                //     System.err.println("Missing: " + code);
                // }
            }
            try {
                Collections.sort(states);  // Sort
            } catch(Exception e) {
                System.err.println("Error sorting!");
            }
            // System.err.println("D: " + depth);
            // for (int i = 0; i < n_childs; i++) {
            //     System.err.print(states.get(i).score + ", ");
            // }
            // System.err.println(" ");
        }
        else {  // Do not sort (could only use heuristics)
            GameState nextState;
            for (int i = 0; i < n_childs; i++) {
                nextState = nextStates.elementAt(i);
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
            SortableGameState best_child = null;
            SortableGameState state = null;
            Pair<Integer, Integer> state_info = null;

            for (int i = 0; i < n_childs; i++) {
                state = states.elementAt(i);
                state_info = alphabeta(state.gameState, depth - 1, alpha, beta, min_player);
                if (state_info.second > v) {
                    v = state_info.second;
                    bestState = state.pos; // Assign pos previous to sorting
                    best_child = state;
                }
                alpha = Math.max(alpha, v);
                if (beta <= alpha)
                    break;
            }
            max_states.put(gameState.toMessage().split(" ")[0], v);
            // max_states.put(0.split(" ")[0], v);
            return new Pair<Integer, Integer>(bestState, v); // move, val
        } else {  // MIN player
            
            int bestState = 0;
            int v = Integer.MAX_VALUE;
            SortableGameState best_child = null;
            SortableGameState state = null;
            Pair<Integer, Integer> state_info = null;

            for (int i = n_childs - 1; i > - 1; i--) {
            // for (int i = 0; i < n_childs; i++) {
                state = states.elementAt(i);
                state_info = alphabeta(state.gameState, depth - 1, alpha, beta, max_player);
                if (state_info.second < v) {
                    v = state_info.second;
                    bestState = state.pos;
                    best_child = state;
                }
                beta = Math.min(beta, v);
                if (beta <= alpha)
                    break;
            }
            min_states.put(gameState.toMessage().split(" ")[0], v);
            // min_states.put(best_child.gameState.toMessage().split(" ")[0], v);
            return new Pair<Integer, Integer>(bestState, v); // move, val
        }
    }

    public int evaluate(GameState gamestate) {
        int number_of_whites = 0;
        int number_of_reds = 0;

        int white_kings = 0;
        int red_kings = 0;

        int aux = 0;

        for (int i = 0; i < GameState.NUMBER_OF_SQUARES; i++) {
            aux = gamestate.get(i);
            if (aux == Constants.CELL_EMPTY){
                continue;
            }
            else if (aux == Constants.CELL_WHITE){
                number_of_whites++;
                continue;
            }
            else if (aux == WHITE_KING){
                white_kings++;
                continue;
            }
            else if (aux == Constants.CELL_RED){
                number_of_reds++;
                continue;
            }
            else if (aux == RED_KING){
                red_kings++;
                continue;
            }
        }

        int whites = number_of_whites * PIECE_VALUE + white_kings * KING_VALUE;
        int reds = number_of_reds * PIECE_VALUE + red_kings * KING_VALUE;

        if (max_player == Constants.CELL_RED){
            return reds - whites;
        }            

        return whites - reds; 
    }
}