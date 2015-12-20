package com.bornaapp.games.borna2d.ai;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.bornaapp.games.borna2d.game.Engine;;
import com.bornaapp.games.borna2d.game.LevelBase;
import com.bornaapp.games.borna2d.game.PlayGround;

public class HeuristicImp implements Heuristic<Node> {

    @Override
    public float estimate(Node startNode, Node endNode) {
        float cost = Float.MAX_VALUE;

        PlayGround playGround = getPlayGround();
        if (playGround == null) {
            Engine.log.error("playGround == null");
            return cost;
        }

        int widthInTiles = playGround.getWidthInTiles();

        int startIndex = startNode.getIndex();
        int endIndex = endNode.getIndex();

        int startY = startIndex / widthInTiles;
        int startX = startIndex % widthInTiles;

        int endY = endIndex / widthInTiles;
        int endX = endIndex % widthInTiles;

        // magnitude of differences on both axes is Manhattan distance (not ideal)
        cost = Math.abs(startX - endX) + Math.abs(startY - endY);
        return cost;
    }

    /**
     * private
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
