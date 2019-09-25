import java.util.*;

public class Player {
    Algorithms alg = new Algorithms();
    /**
     * Performs a move
     *
     * @param pState
     *            the current state of the board
     * @param pDue
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */
    public GameState play(final GameState pState, final Deadline pDue) {

        Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);

        if (lNextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(pState, new Move());
        }

        // int max_depth = 3;
        // for (int depth = 1; depth < max_depth; depth++) {
        int depth = 10;
        Pair<Integer, Integer> action = alg.alphabeta(pState, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, Constants.CELL_WHITE);
        // }
        return lNextStates.elementAt(action.first);
    }
}
