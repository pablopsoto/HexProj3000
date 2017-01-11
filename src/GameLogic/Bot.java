package GameLogic;

import EnumVariables.BotType;
import EnumVariables.GameType;
import EnumVariables.StatusCell;

import java.util.Random;

/**
 * Created by giogio on 1/11/17.
 */
public class Bot extends Player{
    private BotType type;

    public Bot(BotType type, StatusCell color, Match match){
        this.type = type;
        this.color = color;
        this.match = match;
    }

    @Override
    public void makeMove(int x, int y) {
        Thread t = new Thread((() -> makeMoveThread()));
        t.start();
    }

    private void makeMoveThread(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int x = (int)(Math.random()*match.getBoard().getSize());
        int y = (int)(Math.random()*match.getBoard().getSize());
        match.setBotTurn(false);
        super.makeMove(x, y);
    }
}
