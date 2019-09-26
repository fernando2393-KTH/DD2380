import java.util.*;

public class Algorithms {

    private final int WHITE_KING = Constants.CELL_WHITE | Constants.CELL_KING;
    private final int RED_KING = Constants.CELL_RED | Constants.CELL_KING;

    /* Constants for evaluation function */

    private final int PIECE_VALUE = 10;
    private final int KING_VALUE = 30;
    
    public static int max_player;
    public static int min_player;
    
    /**
     * This function computes the best next state according to evaluation
     * 
     * @param gameState the current state of the game
     * @param depth     the maximum depth for the search algorithm
     * @param alpha     the alpha value
     * @param beta      the beta value
     * @param player    the current player
     * @return          the best state and its computed value
     */
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
    
    /**
     * This function computes the heuristic for the max. player
     * 
     * @param gamestate the current state of the game 
     * @return          the heuristic for the max. player
     */
    public int evaluate(GameState gamestate) {
        int whites = 0;
        int reds = 0;

        int number_of_whites = 0;
        int number_of_reds = 0;

        int white_kings = 0;
        int red_kings = 0;

        int aux = 0;

        /************ WHITES CHECKING  ************/

        for (int i = 0; i < GameState.NUMBER_OF_SQUARES; i++) {

            aux = gamestate.get(i);

            /************ EMPTY CHECKING  ************/

            if (aux == Constants.CELL_EMPTY){
                continue;
            }

            /************ WHITES CHECKING  ************/

            else if (aux == Constants.CELL_WHITE){
                number_of_whites++;
                continue;
            }
            else if (aux == WHITE_KING){
                white_kings++;
                continue;
            }

            /************ REDS CHECKING  ************/
                
            else if (aux == Constants.CELL_RED){
                number_of_reds++;
                continue;
            }
            else if (aux == RED_KING){
                red_kings++;
                continue;
            }
        }

        whites += number_of_whites * PIECE_VALUE + white_kings * KING_VALUE;
        reds += number_of_reds * PIECE_VALUE + red_kings * KING_VALUE;

        if (max_player == Constants.CELL_RED){
            return reds - whites;
        }            

        return whites - reds; 
    }

}