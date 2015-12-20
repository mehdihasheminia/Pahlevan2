package com.bornaapp.games.borna2d.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.bornaapp.games.borna2d.Build;
import com.bornaapp.games.borna2d.Def;
import com.bornaapp.games.borna2d.UnitConverter;
import com.bornaapp.games.borna2d.ai.GraphGenerator;
import com.bornaapp.games.borna2d.ai.GraphImp;
import com.bornaapp.games.borna2d.iDispose;
import com.bornaapp.games.borna2d.iRender;
import com.bornaapp.games.borna2d.physics.BODYSHAPE;

public class PlayGround implements iRender, iDispose {

    private int widthInTiles; //todo" should they have a default value to enable engine.getWidth? or should we do it in engine?
    private int heightInTiles;
    private int widthInPixels;
    private int heightInPixels;
    private int widthOfEachTileInPixels;
    private int heightOfEachTileInPixels;

    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;

    public GraphImp getAirGraph() {
        return airGraph;
    }

    public GraphImp getGroundGraph() {
        return groundGraph;
    }

    GraphImp airGraph;
    GraphImp groundGraph;

    //region Properties
    public int getWidthInTiles() {
        return widthInTiles;
    }

    public int getHeightInTiles() {
        return heightInTiles;
    }

    public int getWidthInPixels() {
        return widthInPixels;
    }

    public int getHeightInPixels() {
        return heightInPixels;
    }

    public int getWidthOfEachTileInPixels() {
        return widthOfEachTileInPixels;
    }

    public int getHeightOfEachTileInPixels() {
        return heightOfEachTileInPixels;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }
    //endregion

    //region Loading tile Map
    public void load(String filePath) {
        //load graphics
        try {
            // retrieve previously loaded tile map from asset manager of current level
            tiledMap = Engine.getCurrentLevel().getAssetManager().getTiledMap(filePath);
            tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            // calculate dimensions of playground in both tiles and pixels
            MapProperties properties = tiledMap.getProperties();
            widthInTiles = properties.get("width", Integer.class);
            heightInTiles = properties.get("height", Integer.class);
            widthOfEachTileInPixels = properties.get("tilewidth", Integer.class);
            heightOfEachTileInPixels = properties.get("tileheight", Integer.class);
            widthInPixels = widthInTiles * widthOfEachTileInPixels;
            heightInPixels = heightInTiles * heightOfEachTileInPixels;
            //Load level data from file
            this.extractMapBoundaries();
            this.extractObstacles();
            this.extractPortals();
            //this.extractLights();
            this.extractParticleGenerators();
            this.extractCheckpoints();
            //claculate AI pathFinding graphs
            GraphGenerator graphGenerator = new GraphGenerator();
            airGraph = graphGenerator.generateAirGraph(tiledMap);
            groundGraph = graphGenerator.generateGroundGraph(tiledMap);
            //
        } catch (Exception e) {
            Engine.log.error(e.getMessage());
            tiledMap = null;
            tiledMapRenderer = null;
        }
    }
    //endregion

    //region Loading components from tile map
    public void extractMapBoundaries() {
        PooledEngine ashleyEngine = Engine.getCurrentLevel().getAshleyEngine();
        float x, y, w, h;
        //bottom Edge
        x = 0f;
        y = widthOfEachTileInPixels * 0.7f;
        w = widthInPixels;
        h = 0f;
        Entity bottomEdge = ashleyEngine.createEntity();
        bottomEdge.add(Build.AshleyComponent.Body(BodyDef.BodyType.StaticBody, BODYSHAPE.EDGE, x, y, w, h, true, 1.0f, 1.0f, 1.0f, false, 1.0f));
        ashleyEngine.addEntity(bottomEdge);
        //Right Edge
        x = widthInPixels - widthOfEachTileInPixels * 0.7f;
        y = 0f;
        w = 0f;
        h = heightInPixels;
        Entity rightEdge = ashleyEngine.createEntity();
        rightEdge.add(Build.AshleyComponent.Body(BodyDef.BodyType.StaticBody, BODYSHAPE.EDGE, x, y, w, h, true, 1.0f, 1.0f, 1.0f, false, 1.0f));
        ashleyEngine.addEntity(rightEdge);
        //Left Edge
        x = widthOfEachTileInPixels * 0.7f;
        y = 0f;
        w = 0f;
        h = heightInPixels;
        Entity leftEdge = ashleyEngine.createEntity();
        leftEdge.add(Build.AshleyComponent.Body(BodyDef.BodyType.StaticBody, BODYSHAPE.EDGE, x, y, w, h, true, 1.0f, 1.0f, 1.0f, false, 1.0f));
        ashleyEngine.addEntity(leftEdge);
        //top Edge
        x = 0f;
        y = heightInPixels - widthOfEachTileInPixels * 0.7f;
        w = widthInPixels;
        h = 0f;
        Entity topEdge = ashleyEngine.createEntity();
        topEdge.add(Build.AshleyComponent.Body(BodyDef.BodyType.StaticBody, BODYSHAPE.EDGE, x, y, w, h, true, 1.0f, 1.0f, 1.0f, false, 1.0f));
        ashleyEngine.addEntity(topEdge);
    }

    public void extractObstacles() {
        try {
            MapLayer obstaclesLayer = tiledMap.getLayers().get(Def.get().collisionLayerName);
            MapObjects mapObjects = obstaclesLayer.getObjects();
            for (MapObject mapObject : mapObjects) {

                if (mapObject instanceof TextureMapObject)
                    continue;

                if (mapObject instanceof RectangleMapObject) {
                    //extract rectangle dimensions
                    RectangleMapObject rect = (RectangleMapObject) mapObject;
                    float x = rect.getRectangle().getX();
                    float y = rect.getRectangle().getY();
                    float w = rect.getRectangle().getWidth();
                    float h = rect.getRectangle().getHeight();
                    //apply it to portal entity
                    PooledEngine ashleyEngine = Engine.getCurrentLevel().getAshleyEngine();
                    Entity portal = ashleyEngine.createEntity();
                    portal.add(Build.AshleyComponent.Body(BodyDef.BodyType.StaticBody, BODYSHAPE.RECTANGLE, x, y, w, h, true, 1.0f, 1.0f, 1.0f, false, 1.0f));
                    ashleyEngine.addEntity(portal);
                } else {
                    //extract Circle dimensions
                    float x = mapObject.getProperties().get("x", Float.class);
                    float y = mapObject.getProperties().get("y", Float.class);
                    float w = mapObject.getProperties().get("width", Float.class);
                    float h = mapObject.getProperties().get("height", Float.class); //<---todo: problem
                    //apply it to portal entity
                    PooledEngine ashleyEngine = Engine.getCurrentLevel().getAshleyEngine();
                    Entity portal = ashleyEngine.createEntity();
                    portal.add(Build.AshleyComponent.Body(BodyDef.BodyType.StaticBody, BODYSHAPE.CIRCLE, x, y, 10, 10, true, 1.0f, 1.0f, 1.0f, false, 1.0f));
                    ashleyEngine.addEntity(portal);
                }
            }
        } catch (Exception e) {
            Engine.log.error(e.getMessage());
        }
    }

    public void extractPortals() {
        try {
            MapLayer portalLayer = tiledMap.getLayers().get(Def.get().portalLayerName);
            MapObjects mapObjects = portalLayer.getObjects();
            for (MapObject mapObject : mapObjects) {
                if (mapObject instanceof TextureMapObject)
                    continue;
                if (mapObject instanceof RectangleMapObject) {
                    //extract rectangle dimensions
                    RectangleMapObject rect = (RectangleMapObject) mapObject;
                    float x = rect.getRectangle().getX();
                    float y = rect.getRectangle().getY();
                    float w = rect.getRectangle().getWidth();
                    float h = rect.getRectangle().getHeight();
                    //apply it to portal entity
                    PooledEngine ashleyEngine = Engine.getCurrentLevel().getAshleyEngine();
                    Entity portal = ashleyEngine.createEntity();
                    portal.add(Build.AshleyComponent.Name(mapObject.getName()));
                    portal.add(Build.AshleyComponent.Body(BodyDef.BodyType.StaticBody, BODYSHAPE.RECTANGLE, x, y, w, h, true, 1.0f, 1.0f, 1.0f, true, 1.0f));
                    ashleyEngine.addEntity(portal);
                } else {
                    //extract Circle dimensions
                    float x = mapObject.getProperties().get("x", Float.class);
                    float y = mapObject.getProperties().get("y", Float.class);
                    float w = mapObject.getProperties().get("width", Float.class);
                    float h = mapObject.getProperties().get("height", Float.class);
                    //apply it to portal entity
                    PooledEngine ashleyEngine = Engine.getCurrentLevel().getAshleyEngine();
                    Entity portal = ashleyEngine.createEntity();
                    portal.add(Build.AshleyComponent.Name(mapObject.getName()));
                    portal.add(Build.AshleyComponent.Body(BodyDef.BodyType.StaticBody, BODYSHAPE.CIRCLE, x, y, w, h, true, 1.0f, 1.0f, 1.0f, true, 1.0f));
                    ashleyEngine.addEntity(portal);
                }
            }
        } catch (Exception e) {
            Engine.log.error(e.getMessage());
        }
    }

//    public void extractLights() {
//        try {
//            MapLayer lightsLayer = tiledMap.getLayers().get(Def.get().lightsLayerName);
//            MapObjects mapObjects = lightsLayer.getObjects();
//            for (MapObject mapObject : mapObjects) {
//                PointLightObj light = new PointLightObj();
//                //get properties
//                float x = mapObject.getProperties().get("x", Float.class);
//                float y = mapObject.getProperties().get("y", Float.class);
//                float d = Float.parseFloat(mapObject.getProperties().get(Def.get().propertyKey_Distance, String.class));
//                String colorString = mapObject.getProperties().get(Def.get().propertyKey_Color, String.class);
//                String[] colorsArray = colorString.split(",");
//                Color color = Color.WHITE;
//                color.set(Float.parseFloat(colorsArray[0]), Float.parseFloat(colorsArray[1]), Float.parseFloat(colorsArray[2]), Float.parseFloat(colorsArray[3]));
//                //Apply properties to object
//                light.setPosition_inPixels(x, y);
//                light.setDistence(d);
//                light.setColor(color);
//            }
//        } catch (Exception e) {
//            Engine.log.error(e.getMessage());
//        }
//    }

    public void extractParticleGenerators() {
        try {
            MapLayer particlesLayer = tiledMap.getLayers().get(Def.get().particlesLayerName);
            MapObjects mapObjects = particlesLayer.getObjects();
            for (MapObject mapObject : mapObjects) {
                //read properties from file
                String path = mapObject.getProperties().get("type", String.class);
                float x = mapObject.getProperties().get("x", Float.class);
                float y = mapObject.getProperties().get("y", Float.class);
                //create particle
                PooledEngine ashleyEngine = Engine.getCurrentLevel().getAshleyEngine();
                Entity particle = ashleyEngine.createEntity();
                particle.add(Build.AshleyComponent.Particle("WaterFountain.prc", x, y));
                ashleyEngine.addEntity(particle);
            }
        } catch (Exception e) {
            Engine.log.error(e.getMessage());
        }
    }

    public void extractCheckpoints() {
        try {
            MapLayer checkpointLayer = tiledMap.getLayers().get(Def.get().checkpointsLayerName);
            MapObjects mapObjects = checkpointLayer.getObjects();
            for (MapObject mapObject : mapObjects) {
                String name = mapObject.getName();
                float x = mapObject.getProperties().get("x", Float.class);
                float y = mapObject.getProperties().get("y", Float.class);
                //
                PooledEngine ashleyEngine = Engine.getCurrentLevel().getAshleyEngine();
                ashleyEngine.addEntity(Build.AshleyEntity.CheckPoint(name, UnitConverter.toMeters(x), UnitConverter.toMeters(y)));
            }
        } catch (Exception e) {
            Engine.log.error(e.getMessage());
        }
    }
    //endregion

    //region overrided methods
    @Override
    public void render() {
        if (tiledMapRenderer != null && tiledMap != null) {
            tiledMapRenderer.setView(Engine.getCurrentLevel().getCamera());
            tiledMapRenderer.render();
        }
    }

    @Override
    public void dispose() {
        // as tiledMap loaded by assetManager,
        // it disposes tiledMap itself at the end of level.
    }
    //endregion
}