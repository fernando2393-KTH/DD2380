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
        int nextMove = alg.alphabeta(gameState, 2, Integer.MIN_VALUE, Integer.MAX_VALUE,
                Constants.CELL_X).first;
        return nextStates.elementAt(nextMove);

    }
}
