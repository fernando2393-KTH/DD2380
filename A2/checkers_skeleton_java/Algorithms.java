import java.util.*;

public class Algorithms {

    public Pair<Integer, Integer> alphabeta(GameState gameState, int depth, int alpha, int beta, int player) {
        if (gameState.isEOG()) {
            if (gameState.isRedWin())
                return new Pair<Integer, Integer>(0, Integer.MIN_VALUE);
            if (gameState.isWhiteWin())
                return new Pair<Integer, Integer>(0, Integer.MAX_VALUE);
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

        int number_of_whites = 0;
        int number_of_reds = 0;

        int white_kings = 0;
        int red_kings = 0;

        /**
         * Prioritize rows 0 and 7 and columns 0 and 7 (4 edges)
         * Prioritize movements ahead in which the piece cannot be eaten
         */

        /************ WHITES CHECKING  ************/

        for (int i = 0; i < 32; i++) {
            if (gamestate.get(i) == Constants.CELL_WHITE || gamestate.get(i) == (Constants.CELL_WHITE & Constants.CELL_KING)){

                number_of_whites++;

                if (gamestate.get(i) == (Constants.CELL_WHITE & Constants.CELL_KING)){
                    white_kings++;
                }

                if (gamestate.cellToRow(i) == 7){
                    whites += 10;
                }

                else if (gamestate.cellToCol(i) == 0){
                    whites += 20;
                }

                else if (gamestate.cellToCol(i) == 7){
                    whites += 20;
                }

                if (gamestate.cellToRow(i) != 0 && gamestate.cellToRow(i) != 7 && 
                gamestate.cellToRow(i) % 2 == 0 && (i % 4) < 3){ // If even not-edge row and one of the first three cells

                    if ((gamestate.get(i-4) != Constants.CELL_RED || gamestate.get(i+5) == Constants.CELL_WHITE)
                    && (gamestate.get(i-3) != Constants.CELL_RED || gamestate.get(i+4) == Constants.CELL_WHITE)){ // If piece not in danger
                        whites += 8 - gamestate.cellToRow(i);
                    }
                }

                if (gamestate.cellToRow(i) != 0 && gamestate.cellToRow(i) != 7 && 
                gamestate.cellToRow(i) % 2 != 0 && (i % 4) > 0){ // If odd not-edge row and one of the last three cells

                    if ((gamestate.get(i-5) != Constants.CELL_RED || gamestate.get(i+4) == Constants.CELL_WHITE)
                    && (gamestate.get(i-4) != Constants.CELL_RED || gamestate.get(i+3) == Constants.CELL_WHITE)){ // If piece not in danger
                        whites += 8 - gamestate.cellToRow(i);
                    }
                }
            }

            /************ REDS CHECKING  ************/
                
            if (gamestate.get(i) == Constants.CELL_RED || gamestate.get(i) == (Constants.CELL_RED & Constants.CELL_KING)){

                number_of_reds++;

                if (gamestate.get(i) == (Constants.CELL_RED & Constants.CELL_KING)){
                    red_kings++;
                }

                if (gamestate.cellToRow(i) == 0){
                    reds += 10;
                }

                else if (gamestate.cellToCol(i) == 0){
                    reds += 20;
                }

                else if (gamestate.cellToCol(i) == 7){
                    reds += 20;
                }

                if (gamestate.cellToRow(i) != 0 && gamestate.cellToRow(i) != 7 && 
                gamestate.cellToRow(i) % 2 == 0 && (i % 4) < 3){ // If even not-edge row and one of the first three cells

                    if ((gamestate.get(i+4) != Constants.CELL_WHITE || gamestate.get(i-3) == Constants.CELL_RED)
                    && (gamestate.get(i+5) != Constants.CELL_WHITE || gamestate.get(i-4) == Constants.CELL_RED)){ // If piece not in danger
                        reds += gamestate.cellToRow(i)+1;
                    }
                }

                if (gamestate.cellToRow(i) != 0 && gamestate.cellToRow(i) != 7 && 
                gamestate.cellToRow(i) % 2 != 0 && (i % 4) > 0){ // If odd not-edge row and one of the last three cells

                    if ((gamestate.get(i+3) != Constants.CELL_WHITE || gamestate.get(i-4) == Constants.CELL_RED)
                    && (gamestate.get(i+4) != Constants.CELL_WHITE || gamestate.get(i-5) == Constants.CELL_RED)){ // If piece not in danger
                        reds += gamestate.cellToRow(i)+1;
                    }
                }
            }
        }

        whites += number_of_whites * 200 + white_kings * 50;
        reds += number_of_reds * 200 + red_kings * 50;

        return whites - reds; 
    }

}