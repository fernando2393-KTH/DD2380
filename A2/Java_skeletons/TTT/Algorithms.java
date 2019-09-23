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
     * @return
     */
    public Pair<Integer, Integer> alphabeta(GameState gameState, int depth, int alpha, int beta, int player) {
        int v = 0;
        int bestState = 0;
        Pair<Integer, Integer> state_i;
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);
        // int nextPlayer = (player + 1)%2;

        if (depth == 0 || nextStates.size() == 0) {
            v = evaluation(gameState, player);
            return new Pair<Integer, Integer>(v, bestState);
        } else if (player == Constants.CELL_X) {
            v = Integer.MIN_VALUE;
            for (int i = 0; i < nextStates.size(); i++) {
                state_i = alphabeta(gameState, depth - 1, alpha, beta, Constants.CELL_O);
                if (state_i.first > v) {
                    v = state_i.first;
                    bestState = i;
                }
                alpha = Math.max(alpha, v);
                if (beta <= alpha)
                    break;
            }
        } else {
            v = Integer.MAX_VALUE;
            for (int i = 0; i < nextStates.size(); i++) {
                state_i = alphabeta(gameState, depth - 1, alpha, beta, Constants.CELL_X);
                if (state_i.first < v) {
                    v = state_i.first;
                    bestState = i;
                }
                beta = Math.min(beta, v);
                if (beta <= alpha)
                    break;
            }
        }
        return new Pair<Integer, Integer>(v, bestState);
    }

    /**
     * 
     * @param gameState the gameState to evaluate
     * @param player    the player who is playing in the turn
     * @return the total gains of moving to gameState
     */
    public int evaluation(GameState gameState, int player) {

        int result = 0;

        /*********** Lines checking ***********/

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

            // Check winner of the row

            result += playerChecker(player, numberX, numberO);

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

            // Check winner of the column

            result += playerChecker(player, numberX, numberO);

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

        // Check winner of the first diagonal

        result += playerChecker(player, numberX, numberO);

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

        // Check winner of the second diagonal

        result += playerChecker(player, numberX, numberO);

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

        int result = 0;

        if (player == Constants.CELL_X) {
            if (numberX > numberO) {
                result++;
            } else {
                result--;
            }
        } else {
            if (numberX > numberO) {
                result--;
            } else {
                result++;
            }
        }

        return result;

    }

}