import java.util.*;

public class Player {
    Algorithms alg = new Algorithms();
    /**
     * This function performs a move
     *
     * @param pState the current state of the board
     * @param pDue   time before which we must have returned
     * @return       the next state the board is in after our move
     */
    public GameState play(final GameState pState, final Deadline pDue) {

        Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);

        if (lNextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(pState, new Move());
        }

        if (pState.getNextPlayer() == Constants.CELL_RED){
            alg.max_player = Constants.CELL_RED;
            alg.min_player = Constants.CELL_WHITE;
        }
        else {
            alg.max_player = Constants.CELL_WHITE;
            alg.min_player = Constants.CELL_RED;
        }

        int depth = 11;

        Pair<Integer, Integer> action = alg.alphabeta(pState, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, alg.max_player);
        
        return lNextStates.elementAt(action.first);
    }
}
