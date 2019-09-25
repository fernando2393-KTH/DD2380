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

        int max_depth = 5;
        long time_limit = pDue.timeUntil()/10; // Stop when 10% of the time is left
        int next_move = alg.iterativeDeepening(max_depth, pState, pDue, time_limit);
        return lNextStates.elementAt(next_move);
    }
}
