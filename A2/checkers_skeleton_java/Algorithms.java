import java.util.*;

public class Algorithms {

    public Pair<Integer, Integer> alphabeta(GameState gameState, int depth, int alpha, int beta, int player) {
        if (gameState.isEOG()) {
            if (gameState.isRedWin())
                return new Pair<Integer, Integer>(0, Integer.MAX_VALUE);
            if (gameState.isWhiteWin())
                return new Pair<Integer, Integer>(0, Integer.MIN_VALUE);
            // Else is draw
            return new Pair<Integer, Integer>(0, 0);
        }

        // If terminal state
        if (depth == 0)
            return new Pair<Integer, Integer>(0, evaluate(gameState)); // move, val

        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        // If player is X
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
            // If player is O
            int bestState = 0;
            int v = Integer.MAX_VALUE;
            for (int i = nextStates.size() - 1; i > -1; i--) {
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

    public int evaluate(GameState gamestate) {
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