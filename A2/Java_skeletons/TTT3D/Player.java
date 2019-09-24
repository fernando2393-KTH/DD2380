import java.util.*;

public class Player {
    Algorithms alg = new Algorithms();

    /**
     * Performs a move
     *
     * @param gameState the current state of the board
     * @param deadline  time before which we must have returned
     * @return the next state the board is in after our move
     */
    public GameState play(final GameState gameState, final Deadline deadline) {
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        if (nextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(gameState, new Move());
        }
        alg.deadline = deadline; // If less than 10% of the time is left, return max found
        alg.stop_time = 0/* 2*deadline.timeUntil()/3 */; // If less than 10% of the time is left, return max found
        int nextMove = alg.alphabeta(gameState, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, Constants.CELL_X).first;
        return nextStates.elementAt(nextMove);
    }
}
