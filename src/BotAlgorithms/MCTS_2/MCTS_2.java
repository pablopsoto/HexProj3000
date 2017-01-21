package BotAlgorithms.MCTS_2;

import BotAlgorithms.Strategy;
import EnumVariables.StatusCell;
import GameLogic.Board;
import GameLogic.Move;

import java.util.ArrayList;

/**
 * Created by giogio on 1/21/17.
 */
public class MCTS_2 implements Strategy{
    private StatusCell ally;
    private StatusCell enemy;
    private Board realBoard;
    private int maxtTime, n_expansion;
    private NodeTree root;
    private NodeTree lastMove;


    public MCTS_2(Board realBoard, StatusCell color, int maxtTime, int depthLevel){
        this.realBoard = realBoard;
        this.ally = color;
        if(color == StatusCell.Blue)
            enemy = StatusCell.Red;
        else
            enemy = StatusCell.Blue;
        this.maxtTime = maxtTime;
    }

    public Move start(){
        setNewRoot();
        double startTime = System.currentTimeMillis();
        n_expansion = 0;
        while (System.currentTimeMillis() - startTime <maxtTime){
            expansion(selection(root));
        }
        System.out.println("Expansions: "+n_expansion);

        return getBestMove().getMove();

    }

    public NodeTree getBestMove(){
        NodeTree bestNode = null;
        double bestValue = -999999999;
        for(NodeTree child: root.getChildren()){
            if(child.isDeadCell())
                continue;
            if(child.isWinningMove() || child.isLosingMove()){
                lastMove = child;
                return child ;
            }
            double value = UCB1(child);
            if(value>bestValue) {
                bestNode = child;
                bestValue = value;
            }
        }
        lastMove = bestNode;
        return bestNode;
    }

    public NodeTree selection(NodeTree node){
        if(node.getState().getFreeMoves().size()>0)
            return node;

        NodeTree bestNode = node;
        double bestValue = -999999999;
        for(NodeTree child: node.getChildren()){
            double value = UCB1(child);
            if(value>bestValue) {
                bestNode = child;
                bestValue = value;
            }
        }
        return selection(bestNode);
    }

    public void setNewRoot(){
        if(lastMove!=null) {
            Board copy = lastMove.getState().getCopy();

            for (NodeTree child : lastMove.getChildren()) {
                Move move = child.getMove();
                copy.putStone(move.getX(), move.getY(), enemy);
                if (copy.isEqual(realBoard)) {
                    root = child;
                    root.setParent(null);
                    System.out.println("USED________");
                    return;
                }
                copy.setEmpty(move.getX(), move.getY());
            }
            lastMove = null;
            setNewRoot();
        }else {
            root = new NodeTree(null);
            root.setColor(enemy);
            root.setState(realBoard.getCopy());
        }
    }

    public void expansion(NodeTree node){
        if(!node.isWinningMove() && !node.isLosingMove() && !node.isDeadCell()) {
            ArrayList<Move> moves = node.getState().getFreeMoves();
            NodeTree newNode = new NodeTree(node);
            Move move = moves.remove((int) (Math.random() * moves.size()));
            newNode.setMove(move);
            n_expansion++;
            if (!newNode.isWinningMove() && !newNode.isLosingMove() && !newNode.isDeadCell()){
                simulate(newNode);
            }else {
                if(!newNode.isDeadCell()) {
                    if(newNode.getColor() == ally){
                        if(newNode.isWinningMove())
                            newNode.incrementWin(10);
                        else
                            newNode.incrementWin(5);
                    }else {
                        if (newNode.isWinningMove())
                            newNode.incrementWin(-10);
                        else
                            newNode.incrementWin(-5);
                    }
                }
            }
            newNode.incrementGame();
        }else{
            if(!node.isDeadCell()) {
                if(node.getColor() == ally){
                    if(node.isWinningMove())
                        node.incrementWin(10);
                    else
                        node.incrementWin(5);
                }else {
                    if (node.isWinningMove())
                        node.incrementWin(-10);
                    else
                        node.incrementWin(-5);
                }
            }
            node.incrementGame();
        }



    }

    public void simulate(NodeTree node){
        StatusCell current;
        if(node.getColor() == StatusCell.Blue)
            current = StatusCell.Red;
        else
            current = StatusCell.Blue;

        Board copy = node.getState().getCopy();
        ArrayList<Move> freeMoves = copy.getFreeMoves();

        int n_moves = freeMoves.size();
        for(int i=0;i<n_moves/2;i++){
            Move move = freeMoves.remove((int)(Math.random()*freeMoves.size()));
            copy.putStone(move.getX(),move.getY(),current);
        }

        if(current == StatusCell.Blue)
            current = StatusCell.Red;
        else
            current = StatusCell.Blue;

        for(Move move: freeMoves){
            copy.putStone(move.getX(),move.getY(),current);
        }

        if(copy.hasWon(ally))
            node.incrementWin(1);
        else
            node.incrementWin(-1);

    }


    public double UCB1(NodeTree node){

        float vi = (float) node.getWins() / node.getGames();
        int np = node.getGames();
        int ni = node.getParent().getGames();
        float a = 0.5f;
        double C = Math.sqrt(2);
        if(vi>a)
            C = 0;

        return vi + C * Math.sqrt(Math.log(np)/ni);
    }


    @Override
    public Move getMove() {
        return start();
    }

    @Override
    public BotAlgorithms.MCTS.NodeTree getRootTreeMcts() {
        return null;
    }
}