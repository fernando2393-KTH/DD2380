import java.util.*;

public class Algorithms {

    /**
     * 
     * @param gameState the current state of the game
     * @param player    the player who is playing in this turn
     * @return the best next state decision for the current player
     */
    public int miniMax(GameState gameState, int player) {

        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        if (nextStates.size() == 0) { // Terminal state --> evaluate
            return evaluation(gameState, player);
        }

        int v = 0;
        if (player == Constants.CELL_X) {
            int bestPossible = Integer.MIN_VALUE; // Equivalent to -infinity in integer value

            for (int i = 0; i < nextStates.size(); i++) {
                v = miniMax(nextStates.elementAt(i), Constants.CELL_O); // miniMax for opposite player
                bestPossible = Math.max(bestPossible, v);
            }
            return bestPossible;
        } else {
            int bestPossible = Integer.MAX_VALUE; // Equivalent to +infinity in integer value

            for (int i = 0; i < nextStates.size(); i++) {
                v = miniMax(nextStates.elementAt(i), Constants.CELL_X); // miniMax for opposite player
                bestPossible = Math.min(bestPossible, v);
            }
            return bestPossible;
        }
    }

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
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        if (gameState.isXWin())
            return new Pair(0, Integer.MAX_VALUE);
        if (gameState.isOWin())
            return new Pair(0, Integer.MIN_VALUE);

        // If terminal state
        if (depth == 0 || gameState.isEOG())
            return new Pair<Integer, Integer>(0, evaluation(gameState, player)); // move, val

        // If player is X
        if (player == Constants.CELL_X) {
            int bestState = -1;
            int v = Integer.MIN_VALUE;
            for (int i = 0; i < nextStates.size(); i++) {
                Pair<Integer, Integer> state_i = alphabeta(nextStates.elementAt(i), depth - 1, alpha, beta, Constants.CELL_O);
                if (state_i.second > v) {
                    v = state_i.second;
                    bestState = i;
                }
                alpha = Math.max(alpha, v);
                if (beta <= alpha)
                    break;
            }
            return new Pair<Integer, Integer>(bestState, v);  // move, val
        }
        
        // If player is O
        int bestState = -1;      
        int v = Integer.MAX_VALUE;
        for (int i = 0; i < nextStates.size(); i++) {
            Pair<Integer, Integer> state_i = alphabeta(nextStates.elementAt(i), depth - 1, alpha, beta, Constants.CELL_X);
            if (state_i.second < v) {
                v = state_i.second;
                bestState = i;
            }
            beta = Math.min(beta, v);
            if (beta <= alpha)
                break;
        }
        return new Pair<Integer, Integer>(bestState, v);
    }

    /**
     * 
     * @param gameState the gameState to evaluate
     * @param player    the player who is playing in the turn
     * @return the total gains of moving to gameState
     */
    public int evaluation(GameState gameState, int player) {
        int result = 0;

        /*********** Rows checking ***********/
        for (int i = 0; i < GameState.BOARD_SIZE; i++) {
            int numberX = 0; // Number of X in the line
            int numberO = 0; // Number of O in the line
            for (int j = 0; j < GameState.BOARD_SIZE; j++) {
                if (gameState.at(i, j) == Constants.CELL_X) {
                    numberX++;
                } else if (gameState.at(i, j) == Constants.CELL_O) {
                    numberO++;
                }
            }
            result += playerChecker(player, numberX, numberO); // Check winner of the row
        }

        /*********** Columns checking ***********/
        for (int i = 0; i < GameState.BOARD_SIZE; i++) {
            int numberX = 0; // Number of X in the column
            int numberO = 0; // Number of O in the column
            for (int j = 0; j < GameState.BOARD_SIZE; j++) {
                if (gameState.at(j, i) == Constants.CELL_X) {
                    numberX++;
                } else if (gameState.at(j, i) == Constants.CELL_O) {
                    numberO++;
                }
            }
            result += playerChecker(player, numberX, numberO); // Check winner of the column
        }

        /*********** Diagonals checking ***********/
        // First diagonal
        int numberX = 0; // Number of X in the first diagonal
        int numberO = 0; // Number of O in the first diagonal
        for (int i = 0; i < GameState.BOARD_SIZE; i++) {
            if (gameState.at(i, i) == Constants.CELL_X) {
                numberX++;
            } else if (gameState.at(i, i) == Constants.CELL_O) {
                numberO++;
            }
        }
        result += playerChecker(player, numberX, numberO); // Check winner of the first diagonal

        // Second diagonal
        numberX = 0; // Number of X in the second diagonal
        numberO = 0; // Number of O in the second diagonal
        for (int i = 0; i < GameState.BOARD_SIZE; i++) {
            if (gameState.at(i, (GameState.BOARD_SIZE - 1) - i) == Constants.CELL_X) {
                numberX++;
            } else if (gameState.at(i, (GameState.BOARD_SIZE - 1) - i) == Constants.CELL_O) {
                numberO++;
            }
        }
        result += playerChecker(player, numberX, numberO); // Check winner of the second diagonal

        return result;
    }

    /**
     * 
     * @param player  the current player
     * @param numberX the number of X to evaluate
     * @param numberO the number of O to evaluate
     * @return 0 if draw, 1 if player wins, -1 if player loses
     */
    public int playerChecker(int player, int numberX, int numberO) {
        // Assume we are player X
        int n_good = numberX;
        int n_bad = numberO;
        if (player == Constants.CELL_O) { // Swap values otherwise
            n_good = numberO;
            n_bad = numberX;
        }

        if (n_good > 0 && n_bad == 0)
            return (int) Math.pow(10, n_good);
        if (n_good > 0 && n_bad > 0)
            return 0;
        if (n_bad > 0 && n_good == 0)
            return (int) -Math.pow(10, n_bad);
        if (n_good == 0 && n_bad == 0)
            return 1;
        return 0;
    }
}