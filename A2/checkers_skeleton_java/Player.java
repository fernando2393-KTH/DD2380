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

        // Set player
        if (pState.getNextPlayer() == Constants.CELL_RED){
            alg.max_player = Constants.CELL_RED;
            alg.min_player = Constants.CELL_WHITE;
        }
        else {
            alg.max_player = Constants.CELL_WHITE;
            alg.min_player = Constants.CELL_RED;
        }

        // Start iterative deepening
        int max_depth = 16;
        // long time_limit = 3*pDue.timeUntil()/4; // Stop when 10% of the time is left
        long time_limit = (long) 600e6; // Stop when 10% of the time is left
        int next_move = alg.iterativeDeepening(max_depth, pState, pDue, time_limit);
        System.err.println("Action: " + next_move);
        // System.exit(0);
        return lNextStates.elementAt(next_move);
    }
}
