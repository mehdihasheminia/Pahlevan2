package com.bornaapp.games.borna2d.ai;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.bornaapp.games.borna2d.game.Engine;
import com.bornaapp.games.borna2d.game.PlayGround;

import java.util.Iterator;

public class PathFindingDebugger {
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

//    public void drawPoint2Point(Vector2 pos1, Vector2 pos2) {//<----todo?
//        shapeRenderer.setProjectionMatrix(Engine.getCurrentLevel().getCamera().combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(0, 0, 1, 1);
//        shapeRenderer.circle(pos1.x, pos1.y, 5);
//        shapeRenderer.circle(pos2.x, pos2.y, 5);
////        shapeRenderer.line(pos1, pos2, 10);
//        shapeRenderer.rectLine(pos1, pos2, 3);
//        shapeRenderer.end();
//    }

    /**
     *
     * @param path
     */
    public void drawPath(GraphPathImp path) {
        Iterator<Node> pathIterator = path.iterator();
        Node priorNode = null;

        PlayGround playGround = null;
        try {
            playGround = Engine.getCurrentLevel().getPlayGround();
        } catch (Exception e) {
            Engine.log.error(e.getMessage());
        }
        if (playGround == null) {
            Engine.log.error("playGround == null");
            return;
        }

        while (pathIterator.hasNext()) {
            Node node = pathIterator.next();

            int i = node.getIndex();

            shapeRenderer.setProjectionMatrix(Engine.getCurrentLevel().getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1, 0, 0, 1);
            shapeRenderer.circle(playGround.getWidthOfEachTileInPixels() / 2 + (i % playGround.getWidthInTiles()) * playGround.getWidthOfEachTileInPixels(),
                    playGround.getHeightOfEachTileInPixels() / 2 + (i / playGround.getWidthInTiles()) * playGround.getHeightOfEachTileInPixels(), 5);
            shapeRenderer.end();

            if (priorNode != null) {
                int j = priorNode.getIndex();

                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.line(playGround.getWidthOfEachTileInPixels() / 2 + (i % playGround.getWidthInTiles()) * playGround.getWidthOfEachTileInPixels(),
                        playGround.getHeightOfEachTileInPixels() / 2 + (i / playGround.getWidthInTiles()) * playGround.getHeightOfEachTileInPixels(),
                        playGround.getWidthOfEachTileInPixels() / 2 + (j % playGround.getWidthInTiles()) * playGround.getWidthOfEachTileInPixels(),
                        playGround.getHeightOfEachTileInPixels() / 2 + (j / playGround.getWidthInTiles()) * playGround.getHeightOfEachTileInPixels());
                shapeRenderer.end();
            }

            priorNode = node;
        }
    }
}
