import java.util.*;

public class Algorithms {

    private final int WHITE_KING = Constants.CELL_WHITE | Constants.CELL_KING;
    private final int RED_KING = Constants.CELL_RED | Constants.CELL_KING;

    /* Constants for evaluation function */

    private final int PIECE_VALUE = 10;
    private final int KING_VALUE = 30;
    private final int COL_EDGE = 1;
    private final int BOTTOM_LINE = 2;
    private final int SAFE_KING = 1;
    
    public static int max_player;
    public static int min_player;
    


    public Pair<Integer, Integer> alphabeta(GameState gameState, int depth, int alpha, int beta, int player) {
        
        if (gameState.isEOG()) {
            if (max_player == Constants.CELL_RED){ // Red player
                if (gameState.isRedWin()){
                    return new Pair<Integer, Integer>(0, Integer.MAX_VALUE);
                }
                if (gameState.isWhiteWin()){
                    return new Pair<Integer, Integer>(0, Integer.MIN_VALUE);
                }
            }
            else { // White player
                if (gameState.isRedWin()){
                    return new Pair<Integer, Integer>(0, Integer.MIN_VALUE);
                }
                if (gameState.isWhiteWin()){
                    return new Pair<Integer, Integer>(0, Integer.MAX_VALUE);
                }
            }            
            // Else is draw
            return new Pair<Integer, Integer>(0, 0);
        }

        // If terminal state
        if (depth == 0)
            return new Pair<Integer, Integer>(0, evaluate(gameState)); // move, val

        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        // If player is X
        if (player == max_player) {
            int bestState = 0;
            int v = Integer.MIN_VALUE;
            for (int i = 0; i < nextStates.size(); i++) {
                Pair<Integer, Integer> state_i = alphabeta(nextStates.elementAt(i), depth - 1, alpha, beta,
                        min_player);
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
            for (int i = 0; i < nextStates.size(); i++) {
                Pair<Integer, Integer> state_i = alphabeta(nextStates.elementAt(i), depth - 1, alpha, beta, max_player);
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
            if (gamestate.get(i) == Constants.CELL_WHITE || gamestate.get(i) == (WHITE_KING)){

                if (gamestate.get(i) == (WHITE_KING)){
                    white_kings++;
                }
            //     else {
            //         number_of_whites++;
            //     }

            //     if (GameState.cellToRow(i) == 7){
            //         whites += BOTTOM_LINE;
            //     }

            //     else if (GameState.cellToCol(i) == 0){
            //         whites += COL_EDGE;
            //     }

            //     else if (GameState.cellToCol(i) == 7){
            //         whites += COL_EDGE;
            //     }
                
            //     if (gamestate.get(i) != (WHITE_KING)) { // Do it only for normal pieces

            //     if (GameState.cellToRow(i) != 0 && GameState.cellToRow(i) != 7 && 
            //     GameState.cellToRow(i) % 2 == 0 && (i % 4) < 3){ // If even not-edge row and one of the first three cells

            //         if (((gamestate.get(i-4) != Constants.CELL_RED || gamestate.get(i-4) != RED_KING) || gamestate.get(i+5) == Constants.CELL_WHITE)
            //         && ((gamestate.get(i-3) != Constants.CELL_RED || gamestate.get(i-3) != RED_KING) || gamestate.get(i+4) == Constants.CELL_WHITE)
            //         && (gamestate.get(i+4) != RED_KING || gamestate.get(i-3) == Constants.CELL_WHITE)
            //         && (gamestate.get(i+5) != RED_KING || gamestate.get(i-4) == Constants.CELL_WHITE)){ // If piece not in danger
                        
            //             if (gamestate.get(i) != (WHITE_KING)){ // Do it only for normal pieces
            //                 whites += GameState.cellToRow(i)+1;
            //             }
            //             else {
            //                 whites += SAFE_KING; // Unitary cost
            //             }
            //         }

            //     }

            //     if (GameState.cellToRow(i) != 0 && GameState.cellToRow(i) != 7 && 
            //     GameState.cellToRow(i) % 2 != 0 && (i % 4) > 0){ // If odd not-edge row and one of the last three cells

            //         if (((gamestate.get(i-5) != Constants.CELL_RED || gamestate.get(i-5) != RED_KING) || gamestate.get(i+4) == Constants.CELL_WHITE)
            //         && ((gamestate.get(i-4) != Constants.CELL_RED || gamestate.get(i-4) != RED_KING) || gamestate.get(i+3) == Constants.CELL_WHITE)
            //         && (gamestate.get(i+3) != RED_KING || gamestate.get(i-4) == Constants.CELL_WHITE)
            //         && (gamestate.get(i+4) != RED_KING || gamestate.get(i-5) == Constants.CELL_WHITE)){ // If piece not in danger
                                                
            //             if (gamestate.get(i) != (WHITE_KING)){ // Do it only for normal pieces
            //                 whites += GameState.cellToRow(i)+1;
            //             }
            //             else {
            //                 whites += SAFE_KING; // Unitary cost
            //             }
            //         }
            //     }
            // }
            }

            /************ REDS CHECKING  ************/
                
            if (gamestate.get(i) == Constants.CELL_RED || gamestate.get(i) == (RED_KING)){

                if (gamestate.get(i) == (RED_KING)){
                    red_kings++;
                }
                // else {
                //     number_of_reds++;
                // }

                // if (GameState.cellToRow(i) == 0){
                //     reds += BOTTOM_LINE;
                // }

                // else if (GameState.cellToCol(i) == 0){
                //     reds += COL_EDGE;
                // }

                // else if (GameState.cellToCol(i) == 7){
                //     reds += COL_EDGE;
                // }

                

                // if (GameState.cellToRow(i) != 0 && GameState.cellToRow(i) != 7 && 
                // GameState.cellToRow(i) % 2 == 0 && (i % 4) < 3){ // If even not-edge row and one of the first three cells

                //     if (((gamestate.get(i+4) != Constants.CELL_WHITE || gamestate.get(i+4) != WHITE_KING) || gamestate.get(i-3) == Constants.CELL_RED)
                //     && ((gamestate.get(i+5) != Constants.CELL_WHITE || gamestate.get(i+5) != WHITE_KING) || gamestate.get(i-4) == Constants.CELL_RED)
                //     && (gamestate.get(i-4) != WHITE_KING || gamestate.get(i+5) == Constants.CELL_RED)
                //     && (gamestate.get(i-3) != WHITE_KING || gamestate.get(i+4) == Constants.CELL_RED)){ // If piece not in danger
                        
                //         if (gamestate.get(i) != (RED_KING)){ // Do it only for normal pieces
                //         reds += GameState.cellToRow(i)+1;
                //         }
                //         else {
                //             reds += SAFE_KING; // Unitary cost
                //         }
                //     }
                // }

                // if (GameState.cellToRow(i) != 0 && GameState.cellToRow(i) != 7 && 
                // GameState.cellToRow(i) % 2 != 0 && (i % 4) > 0){ // If odd not-edge row and one of the last three cells

                //     if (((gamestate.get(i+3) != Constants.CELL_WHITE || gamestate.get(i+3) != WHITE_KING) || gamestate.get(i-4) == Constants.CELL_RED)
                //     && ((gamestate.get(i+4) != Constants.CELL_WHITE || gamestate.get(i+4) != WHITE_KING) || gamestate.get(i-5) == Constants.CELL_RED)
                //     && (gamestate.get(i-5) != WHITE_KING || gamestate.get(i+4) == Constants.CELL_RED)
                //     && (gamestate.get(i-4) != WHITE_KING || gamestate.get(i+3) == Constants.CELL_RED)){ // If piece not in danger
                        
                //         if (gamestate.get(i) != (RED_KING)){ // Do it only for normal pieces
                //             reds += GameState.cellToRow(i)+1;
                //             }
                //             else {
                //                 reds += SAFE_KING; // Unitary cost
                //             }
                //     }
                // }
            }
        }

        whites += number_of_whites * PIECE_VALUE + white_kings * KING_VALUE;
        reds += number_of_reds * PIECE_VALUE + red_kings * KING_VALUE;

        if (max_player == Constants.CELL_RED)
            return reds - whites;

        return whites - reds; 
    }

}