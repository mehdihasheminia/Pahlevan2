package com.bornaapp.games.borna2d.ai;

import com.badlogic.gdx.ai.pfa.indexed.DefaultIndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.bornaapp.games.borna2d.game.Engine;
import com.bornaapp.games.borna2d.game.LevelBase;
import com.bornaapp.games.borna2d.game.PlayGround;

public class GraphImp extends DefaultIndexedGraph<Node> {

    public GraphImp(Array<Node> nodes) {
        super(nodes);

        for (int x = 0; x < nodes.size; ++x) {// speedier than indexOf()
            nodes.get(x).index = x;
        }
    }

    public boolean isNodeAvailableAt(int x, int y) {
        PlayGround playGround = getPlayGround();
        if (playGround == null) {
            Engine.log.error("playGround == null");
            return false;
        }

        int modX = x / playGround.getWidthOfEachTileInPixels();
        int modY = y / playGround.getHeightOfEachTileInPixels();

        int index = playGround.getWidthInTiles() * modY + modX;

        return (index >= 0 && index < nodes.size);
    }

    public Node getNodeByXY(int x, int y) {
        PlayGround playGround = getPlayGround();
        if (playGround == null) {
            Engine.log.error("playGround == null");
            return null;
        }

        int modX = x / playGround.getWidthOfEachTileInPixels();
        int modY = y / playGround.getHeightOfEachTileInPixels();

        return nodes.get(playGround.getWidthInTiles() * modY + modX);
    }

    /**
     * private
     *
     * @return playground of current Level
     */
    private PlayGround getPlayGround() {
        PlayGround playGround = null;
        try {
            playGround = getCurrentLevel().getPlayGround();
        } catch (Exception e) {
            Engine.log.error(e.getMessage());
        }
        return playGround;
    }

    /**
     * private
     *
     * @return instance to current Level
     */
    private LevelBase getCurrentLevel() {
        LevelBase level = null;
        try {
            level = Engine.getCurrentLevel();
        } catch (Exception e) {
            Engine.log.error(e.getMessage());
        }
        return level;
    }
}