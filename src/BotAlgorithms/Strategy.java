package BotAlgorithms;

import BotAlgorithms.MCTS.NodeTree;
import GameLogic.Move;

/**
 * Created by giogio on 1/14/17.
 */
public interface Strategy {
    public Move getMove();
    public NodeTree getRootTreeMcts();
}