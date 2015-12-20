package com.bornaapp.games.borna2d.ai;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.bornaapp.games.borna2d.Def;
import com.bornaapp.games.borna2d.game.Engine;
import com.bornaapp.games.borna2d.game.LevelBase;
import com.bornaapp.games.borna2d.game.PlayGround;

public class GraphGenerator {

    public GraphImp generateGroundGraph(TiledMap map) {
        PlayGround playGround = getPlayGround();
        if (playGround == null) {
            Engine.log.error("playGround == null");
            return null;
        }

        Array<Node> nodes = new Array<Node>();

        TiledMapTileLayer tiles = (TiledMapTileLayer) map.getLayers().get(Def.get().pathLayerName);
        int mapHeight = playGround.getHeightInTiles();
        int mapWidth = playGround.getWidthInTiles();

        // Loops over the tiles in the map, starting from bottom left corner
        // iterating left to right, then down to up
        for (int y = 0; y < mapHeight; ++y) {
            for (int x = 0; x < mapWidth; ++x) {
                // generate a node for each tile so that they all exist when we create connections
                Node node = new Node();
                node.type = Node.Type.REGULAR;
                nodes.add(node);
            }
        }

        for (int y = 0; y < mapHeight; ++y) {
            for (int x = 0; x < mapWidth; ++x) {
                TiledMapTileLayer.Cell target = tiles.getCell(x, y);
                TiledMapTileLayer.Cell up = tiles.getCell(x, y + 1);
                TiledMapTileLayer.Cell left = tiles.getCell(x - 1, y);
                TiledMapTileLayer.Cell right = tiles.getCell(x + 1, y);
                TiledMapTileLayer.Cell down = tiles.getCell(x, y - 1);

                //Player can only move where there are not tiles on this layer
                Node targetNode = nodes.get(mapWidth * y + x);
                if (target == null) {
                    if (y != 0 && down == null) {
                        Node downNode = nodes.get(mapWidth * (y - 1) + x);
                        targetNode.createConnection(downNode, 1);
                    }
                    if (x != 0 && left == null) {
                        Node leftNode = nodes.get(mapWidth * y + x - 1);
                        targetNode.createConnection(leftNode, 1);
                    }
                    if (x != mapWidth - 1 && right == null) {
                        Node rightNode = nodes.get(mapWidth * y + x + 1);
                        targetNode.createConnection(rightNode, 1);
                    }
                    if (y != mapHeight - 1 && up == null) {
                        Node upNode = nodes.get(mapWidth * (y + 1) + x);
                        targetNode.createConnection(upNode, 1);
                    }
                }
            }
        }
        return new GraphImp(nodes);
    }

    public GraphImp generateAirGraph(TiledMap map) {
        PlayGround playGround = getPlayGround();
        if (playGround == null) {
            Engine.log.error("playGround == null");
            return null;
        }

        Array<Node> nodes = new Array<Node>();

        TiledMapTileLayer tiles = (TiledMapTileLayer) map.getLayers().get(Def.get().pathLayerName);
        int mapHeight = playGround.getHeightInTiles();
        int mapWidth = playGround.getWidthInTiles();

        // Loops over the tiles in the map, starting from bottom left corner
        // iterating left to right, then down to up
        for (int y = 0; y < mapHeight; ++y) {
            for (int x = 0; x < mapWidth; ++x) {
                // generate a node for each tile so that they all exist when we create connections
                Node node = new Node();
                node.type = Node.Type.REGULAR;
                nodes.add(node);
            }
        }

        for (int y = 0; y < mapHeight; ++y) {
            for (int x = 0; x < mapWidth; ++x) {
                TiledMapTileLayer.Cell target = tiles.getCell(x, y);
                TiledMapTileLayer.Cell up = tiles.getCell(x, y + 1);
                TiledMapTileLayer.Cell upLeft = tiles.getCell(x - 1, y + 1);
                TiledMapTileLayer.Cell upRight = tiles.getCell(x + 1, y + 1);
                TiledMapTileLayer.Cell left = tiles.getCell(x - 1, y);
                TiledMapTileLayer.Cell right = tiles.getCell(x + 1, y);
                TiledMapTileLayer.Cell down = tiles.getCell(x, y - 1);
                TiledMapTileLayer.Cell downLeft = tiles.getCell(x - 1, y - 1);
                TiledMapTileLayer.Cell downRight = tiles.getCell(x + 1, y - 1);

                Node targetNode = nodes.get(mapWidth * y + x);
                if (target == null) {
                    if (y != 0 && down == null) {
                        Node downNode = nodes.get(mapWidth * (y - 1) + x);
                        targetNode.createConnection(downNode, 1);
                    }
                    if (x != 0 && y != 0 && downLeft == null) {
                        Node downLeftNode = nodes.get(mapWidth * (y - 1) + (x - 1));
                        targetNode.createConnection(downLeftNode, 1.7f);
                    }
                    if (x != mapWidth - 1 && y != 0 && downRight == null) {
                        Node downRightNode = nodes.get(mapWidth * (y - 1) + (x + 1));
                        targetNode.createConnection(downRightNode, 1.7f);
                    }
                    if (x != 0 && left == null) {
                        Node leftNode = nodes.get(mapWidth * y + x - 1);
                        targetNode.createConnection(leftNode, 1);
                    }
                    if (x != mapWidth - 1 && right == null) {
                        Node rightNode = nodes.get(mapWidth * y + x + 1);
                        targetNode.createConnection(rightNode, 1);
                    }
                    if (y != mapHeight - 1 && up == null) {
                        Node upNode = nodes.get(mapWidth * (y + 1) + x);
                        targetNode.createConnection(upNode, 1);
                    }
                    if (x != 0 && y != mapHeight - 1 && upLeft == null) {
                        Node upLeftNode = nodes.get(mapWidth * (y + 1) + (x - 1));
                        targetNode.createConnection(upLeftNode, 1.7f);
                    }
                    if (x != mapWidth - 1 && y != mapHeight - 1 && upRight == null) {
                        Node upRightNode = nodes.get(mapWidth * (y + 1) + (x + 1));
                        targetNode.createConnection(upRightNode, 1.7f);
                    }
                }
            }
        }
        return new GraphImp(nodes);
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