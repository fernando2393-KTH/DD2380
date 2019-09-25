import java.util.*;

public class Algorithms {
    public static Hashtable<String, Integer> seen_states = new Hashtable<String, Integer>();

    public Integer iterativeDeepening(int max_depth, GameState gamestate, Deadline deadline, long time_limit) {
        int best_action = 0;
        int best_action_score = Integer.MIN_VALUE;
        for (int depth = 1; depth < max_depth; depth++) {
            System.err.println("Depth: " + depth);
            Pair<Integer, Integer> action = alphabeta(gamestate, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, Constants.CELL_WHITE);
            if (action.second > best_action_score) {
                best_action_score = action.second;
                best_action = action.first;
            }
            if (deadline.timeUntil() < time_limit) {
                System.err.println("Time limited");
                break;
            }
        }
        return best_action;
    }

    public Pair<Integer, Integer> alphabeta(GameState gameState, int depth, int alpha, int beta, int player) {
        // If terminal state:
        if (depth == 0 || gameState.isEOG()) {
            String encoded_board = gameState.toMessage();

            if (seen_states.contains(encoded_board))
                return new Pair<Integer, Integer>(0, seen_states.get(encoded_board));
            
            int score = 0;
            if (gameState.isEOG()) {
                if (gameState.isWhiteWin())
                    score = Integer.MAX_VALUE;
                else if (gameState.isRedWin())
                    score = Integer.MIN_VALUE;
                else // Is draw
                    score = 0;
            } else { // If not End Of Game
                score = heuristic(gameState);
            }
            seen_states.put(encoded_board, score);
            return new Pair<Integer, Integer>(0, score);
        }
        
        // Otherwise Explore other possible moves
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        // Sort
        GameStateComparer comparator = new GameStateComparer();
        comparator.seen_states = seen_states; // Update seen states table
        Collections.sort(nextStates, comparator);
        // for (int i = 0; i < nextStates.size(); i++) 
        //     System.err.print();


        // If player is WHITE
        if (player == Constants.CELL_WHITE) {
            int bestState = 0;
            int v = Integer.MIN_VALUE;
            for (int i = 0; i < nextStates.size(); i++) {
                Pair<Integer, Integer> state_i = alphabeta(nextStates.elementAt(i), depth - 1, alpha, beta,
                        Constants.CELL_RED);
                if (state_i.second > v) {
                    v = state_i.second;
                    bestState = i;
                }
                alpha = Math.max(alpha, v);
                if (beta <= alpha)
                    break;
            }
            return new Pair<Integer, Integer>(bestState, v); // move, val
        } else {
            // If player is RED
            int bestState = 0;
            int v = Integer.MAX_VALUE;
            for (int i = 0; i < nextStates.size(); i++) {            
                Pair<Integer, Integer> state_i = alphabeta(nextStates.elementAt(i), depth - 1, alpha, beta,
                        Constants.CELL_WHITE);
                if (state_i.second < v) {
                    v = state_i.second;
                    bestState = i;
                }
                beta = Math.min(beta, v);
                if (beta <= alpha)
                    break;
            }
            return new Pair<Integer, Integer>(bestState, v); // move, val
        }
    }

    public int heuristic(GameState gamestate) {
        int whites = 0;
        int reds = 0;

        for (int i = 0; i < 32; i++) {
            if (gamestate.get(i) == Constants.CELL_WHITE)
                whites++;
            if (gamestate.get(i) == Constants.CELL_RED)
                reds++;
        }
        return whites - reds;
    }
}