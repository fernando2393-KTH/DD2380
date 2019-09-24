import java.util.*;

public class Algorithms {
    // boolean return_now = false;
    Deadline deadline;
    long stop_time;

    private static final int[][] SCORES = { {1, -10, -100, -1000},
                                            {10, 0, 0, 0},
                                            {100, 0, 0, 0},
                                            {1000, 0, 0, 0}};

    /**
     * Performs alphabeta prunning
     * 
     * @param gameState Current state of the game
     * @param depth     Desired depth of the tree exploration
     * @param alpha     Best value MAX can get
     * @param beta      Worst value MAX can get
     * @param player    Current Player
     * @return // position, value
     */
    public Pair<Integer, Integer> alphabeta(GameState gameState, int depth, int alpha, int beta, int player) {

        byte switcher = 0;

        if (gameState.isXWin())
            return new Pair<Integer, Integer>(0, Integer.MAX_VALUE);
        if (gameState.isOWin())
            return new Pair<Integer, Integer>(0, Integer.MIN_VALUE);
        if (gameState.isEOG())
            return new Pair<Integer, Integer>(0, 0);


        // If terminal state
        if (depth == 0)
            return new Pair<Integer, Integer>(0, evaluation3d(gameState, player)); // move, val

        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        // If player is X
        if (player == Constants.CELL_X) {
            int bestState = 0;
            int v = Integer.MIN_VALUE;
            for (int i = 0; i < nextStates.size(); i++) {
                if(depth > 1 || (depth == 1 && switcher == 0)){

                    if (depth == 1){
                        switcher = 1;
                    }

                    Pair<Integer, Integer> state_i = alphabeta(nextStates.elementAt(i), depth - 1, alpha, beta, Constants.CELL_O);
                    if (state_i.second > v) {
                        v = state_i.second;
                        bestState = i;
                    }
                    alpha = Math.max(alpha, v);
                    if (beta <= alpha)
                        break;
                }
                if (depth == 1 && switcher == 1){
                    switcher = 0;
                }
            }
            return new Pair<Integer, Integer>(bestState, v);  // move, val
        }

        else {
            // If player is O
            int bestState = 0;    
            int v = Integer.MAX_VALUE;
            for (int i = nextStates.size()-1; i > -1; i--) {

                if(depth > 1 || (depth == 1 && switcher == 0)){

                    if (depth == 1){
                        switcher = 1;
                    }

                Pair<Integer, Integer> state_i = alphabeta(nextStates.elementAt(i), depth - 1, alpha, beta, Constants.CELL_X);
                if (state_i.second < v) {
                    v = state_i.second;
                    bestState = i;
                }
                beta = Math.min(beta, v);
                if (beta <= alpha)
                    break;
            }

            if (depth == 1 && switcher == 1){
                switcher = 0;
            }                
            }

            return new Pair<Integer, Integer>(bestState, v);  // move, val
        
    }
}
  
    public int evaluation3d(GameState gameState, int player) {
        int board_size = GameState.BOARD_SIZE;
        int result = 0;
        
        // Get all the layers
        for (int i = 0; i < board_size; i++)
            result += evaluateLayer(getLayer(gameState, i, -1, -1), player);
        for (int j = 0; j < board_size; j++)
            result += evaluateLayer(getLayer(gameState, -1, j, -1), player);
        for (int k = 0; k < board_size; k++)
            result += evaluateLayer(getLayer(gameState, -1, -1, k), player);

        // Get diagonals
        // Diag 1
        int numberX = 0;
        int numberO = 0;
        for (int i = 0; i < board_size; i++) {
            if (gameState.at(i, i, i) == Constants.CELL_X) numberX++;
            else if (gameState.at(i, i, i) == Constants.CELL_O) numberO++;
        }
        result += SCORES[numberX][numberO];

        // Diag 2
        numberX = 0;
        numberO = 0;
        for (int i = 0; i < board_size; i++) {
            if (gameState.at((board_size - 1) - i, i, i) == Constants.CELL_X) numberX++;
            else if (gameState.at((board_size - 1) - i, i, i) == Constants.CELL_O) numberO++;
        }
        result += SCORES[numberX][numberO];

        // Diag 3
        numberX = 0;
        numberO = 0;
        for (int i = 0; i < board_size; i++) {
            if (gameState.at((board_size - 1) - i, (board_size - 1) - i, i) == Constants.CELL_X) numberX++;
            else if (gameState.at((board_size - 1) - i, (board_size - 1) - i, i) == Constants.CELL_O) numberO++;
        }
        result += SCORES[numberX][numberO];
        
        // Diag 4
        numberX = 0;
        numberO = 0;
        for (int i = 0; i < board_size; i++) {
            if (gameState.at(i, (board_size - 1) - i, i) == Constants.CELL_X) numberX++;
            else if (gameState.at(i, (board_size - 1) - i, i) == Constants.CELL_O) numberO++;
        }
        result += SCORES[numberX][numberO];

        return result;
    }

    public int[][] getLayer(GameState gameState, int i, int j, int k) {
        int board_size = GameState.BOARD_SIZE;
        int[][] result = new int[board_size][board_size];

        for (int r = 0; r < board_size; r++)
            for (int c = 0; c < board_size; c++) {
                if (i != -1)
                    result[r][c] = gameState.at(i, r, c);
                else if (j != -1)
                    result[r][c] = gameState.at(r, j, c);
                else if (k != -1)
                    result[r][c] = gameState.at(r, c, k);
            }
        return result;
    }

    /**
     * 
     * @param gameState the gameState to evaluate
     * @param player    the player who is playing in the turn
     * @return the total gains of moving to gameState
     */
    public int evaluateLayer(int [][] mat, int player) {
        int result = 0;

        /*********** Rows checking ***********/
        for (int i = 0; i < GameState.BOARD_SIZE; i++) {
            int numberX = 0; // Number of X in the line
            int numberO = 0; // Number of O in the line
            for (int j = 0; j < GameState.BOARD_SIZE; j++) {
                if (mat[i][j] == Constants.CELL_X) {
                    numberX++;
                } else if (mat[i][j] == Constants.CELL_O) {
                    numberO++;
                }
            }
            result += SCORES[numberX][numberO]; // Check winner of the row
        }

        /*********** Columns checking ***********/
        for (int i = 0; i < GameState.BOARD_SIZE; i++) {
            int numberX = 0; // Number of X in the column
            int numberO = 0; // Number of O in the column
            for (int j = 0; j < GameState.BOARD_SIZE; j++) {
                if (mat[j][i] == Constants.CELL_X) {
                    numberX++;
                } else if (mat[j][i] == Constants.CELL_O) {
                    numberO++;
                }
            }
            result += SCORES[numberX][numberO]; // Check winner of the column
        }

        /*********** Diagonals checking ***********/
        // First diagonal
        int numberX = 0; // Number of X in the first diagonal
        int numberO = 0; // Number of O in the first diagonal
        for (int i = 0; i < GameState.BOARD_SIZE; i++) {
            if (mat[i][i] == Constants.CELL_X) {
                numberX++;
            } else if (mat[i][i] == Constants.CELL_O) {
                numberO++;
            }
        }
        result += SCORES[numberX][numberO]; // Check winner of the first diagonal

        // Second diagonal
        numberX = 0; // Number of X in the second diagonal
        numberO = 0; // Number of O in the second diagonal
        for (int i = 0; i < GameState.BOARD_SIZE; i++) {
            if (mat[i][(GameState.BOARD_SIZE - 1) - i] == Constants.CELL_X) {
                numberX++;
            } else if (mat[i][(GameState.BOARD_SIZE - 1) - i] == Constants.CELL_O) {
                numberO++;
            }
        }
        result += SCORES[numberX][numberO]; // Check winner of the second diagonal
        return result;
    }
}