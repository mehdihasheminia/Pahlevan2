package com.bornaapp.gamelib.borna2d.game;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.bornaapp.gamelib.borna2d.Def;
import com.bornaapp.gamelib.borna2d.OnScreenDisplay;
import com.bornaapp.gamelib.borna2d.UnitConverter;
import com.bornaapp.gamelib.borna2d.asset.Assets;
import com.bornaapp.gamelib.borna2d.components.BodyComponent;
import com.bornaapp.gamelib.borna2d.components.NameComponent;
import com.bornaapp.gamelib.borna2d.components.PositionComponent;
import com.bornaapp.gamelib.borna2d.physics.CollisionListener;
import com.bornaapp.gamelib.borna2d.physics.CollisionStatus;
import com.bornaapp.gamelib.borna2d.physics.DebugRenderer2D;

import box2dLight.RayHandler;

/**
 *
 */
public abstract class LevelBase implements GestureListener {

    //region Constructor
    protected LevelBase(String _path) {
        manifestPath = _path;
        assetManager = new Assets();
        //Set up renderer
        loadingRenderer = new ShapeRenderer();
        //Set up camera
        loadingCamera = new OrthographicCamera();
        loadingCamera.setToOrtho(false, Engine.screenWidth_InPixels(), Engine.screenHeight_InPixels());
    }
    //endregion

    //region Level lifetime indicators
    private boolean created = false;
    public boolean paused = false;
    //endregion

    //region Assets
    public Assets getAssetManager() {
        return assetManager;
    }

    private Assets assetManager;
    private String manifestPath;
    //endregion

    //region Physics
    private World world;

    public World getWorld() {
        return world;
    }

    public DebugRenderer2D debugRenderer;
    //endregion

    //region Lights
    private boolean enableLights = false;

    public void enableLightEffects(boolean enableLights) {
        this.enableLights = enableLights;
    }

    private RayHandler rayHandler;

    public RayHandler getRayHandler() {
        return rayHandler;
    }
    //endregion

    //region Tiled map
    //todo: playground(dimensions) in Level can be abstracted from tileMap(resource)
    private PlayGround playGround;

    public PlayGround getPlayGround() {
        return this.playGround;
    }
    //endregion

    //region Checkpoints and Portals
    private ComponentMapper<NameComponent> nameMap = ComponentMapper.getFor(NameComponent.class);
    private ComponentMapper<PositionComponent> posMap = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<BodyComponent> bodyMap = ComponentMapper.getFor(BodyComponent.class);

    private ImmutableArray<Entity> checkpoints;

    public void init_CheckpointQuery() {
        Family checkpointFamily = Family.all(NameComponent.class, PositionComponent.class).get();
        checkpoints = ashleyEngine.getEntitiesFor(checkpointFamily);
    }

    public Vector2 getCheckpoint(String name) {
        for (int i = 0; i < checkpoints.size(); i++) {
            Entity checkpoint = checkpoints.get(i);
            NameComponent nameComp = nameMap.get(checkpoint);
            if (!nameComp.name.equals(name))
                continue;
            PositionComponent posComp = posMap.get(checkpoint);
            return new Vector2(posComp.getX_inMeters(), posComp.getY_inMeters());
        }
        return null;
    }

    private ImmutableArray<Entity> portals;

    public void init_PortalQuery() {
        Family portalFamily = Family.all(NameComponent.class, BodyComponent.class).get();
        portals = ashleyEngine.getEntitiesFor(portalFamily);
    }

    public Body getPortal(String name) {
        for (int i = 0; i < portals.size(); i++) {
            Entity portal = portals.get(i);
            NameComponent nameComp = nameMap.get(portal);
            if (!nameComp.name.equals(name))
                continue;
            BodyComponent bodyComp = bodyMap.get(portal);
            return bodyComp.body;
        }
        return null;
    }
    //endregion

    //region Ashley
    //todo...."ashley event system" can be used to dispose() resources that are not part of assets like PointLight

    private PooledEngine ashleyEngine = new PooledEngine();

    public PooledEngine getAshleyEngine() {
        return ashleyEngine;
    }

    int systemPriority;

    public int getSystemPriority() {
        return systemPriority++;
    }
    //endregion

    //region Input management
    private GestureDetector gestureDetector;
    //endregion

    //region Graphics
    private SpriteBatch batch;

    public SpriteBatch getBatch() {
        return batch;
    }

    private OrthographicCamera camera;

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Color backColor = Color.DARK_GRAY;
    //endregion

    //region Debug
    public OnScreenDisplay osd;
    //endregion

    //region Abstract methods
    protected abstract void onCreate();

    protected abstract void onDispose();

    protected abstract void onUpdate();

    protected abstract void onDraw();

    protected abstract void onResize(int width, int height);

    /**
     * When a Level is paused, Rendering continues
     * but other systems are stopped
     */
    protected abstract void onPause();

    /**
     * Restore Level's state to what
     * it was before pausing
     */
    protected abstract void onResume();
    //endregion

    //region Handling Engine requests

    void inResponseToEngine_create() {
        if (created)
            return;
        //debug
        osd = new OnScreenDisplay();
        Engine.log.info("");
        //initialze and load external assets
        if (manifestPath.equals(""))//<--- Condition for Silent loading
        {
            assetManager.loadAll(manifestPath);
        }
        else//<--- Show progress
        {
            if(assetManager.loadByStep(manifestPath) )
            return;
        }
        //init batch renderer
        batch = new SpriteBatch();
        //Set up camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Engine.screenWidth_InPixels(), Engine.screenHeight_InPixels());
        camera.update();
        //initialize physics
        world = new World(Def.get().gravity, false);
        world.setContactListener(new CollisionListener());
        debugRenderer = new DebugRenderer2D();
        //create Lights
        rayHandler = new RayHandler(world);
        //init background
        playGround = new PlayGround();

        // initialize input detector
        try {
            gestureDetector = new GestureDetector(this);
        } catch (Exception e) {
            gestureDetector = null;
            Engine.log.error(e.getMessage());
        }
        Gdx.input.setInputProcessor(gestureDetector);
        //make ready for queries
        init_CheckpointQuery();
        init_PortalQuery();
        //level specifics
        onCreate();
        //
        created = true;
    }

    /**
     * Package private:
     * must only get called by engine in response to applicationListener needs
     */
    void inResponseToEngine_dispose() {
        if (created) {
            Engine.log.info("");
            onDispose();
            //
            ashleyEngine.removeAllEntities();
            //
            batch.dispose();
            osd.dispose();
            //
            playGround.dispose();//todo .........assets?
            //
            debugRenderer.dispose();
            rayHandler.dispose();
            world.dispose();
            assetManager.dispose();
            loadingRenderer.dispose();
            //
            //collect all garbage memory
            System.gc();
            //
            created = false;
        }
    }

    /**
     * Package private:
     * must only get called by engine in response to applicationListener needs
     */
    void inResponseToEngine_render(float delta) {

        if (!created) {
            RenderLoadingProgress(assetManager.getProgress());
            inResponseToEngine_create();
        }

        //update box2d world
        world.step(deltaTime(), 6, 2);

        if (!paused) {
            onUpdate();
            //Limit camera to playground borders
            if (camera.position.x > playGround.getWidthInPixels() - Engine.screenWidth_InPixels() / 2)
                camera.position.x = playGround.getWidthInPixels() - Engine.screenWidth_InPixels() / 2;
            if (camera.position.x < Engine.screenWidth_InPixels() / 2)
                camera.position.x = Engine.screenWidth_InPixels() / 2;
            if (camera.position.y > playGround.getHeightInPixels() - Engine.screenHeight_InPixels() / 2)
                camera.position.y = playGround.getHeightInPixels() - Engine.screenHeight_InPixels() / 2;
            if (camera.position.y < Engine.screenHeight_InPixels() / 2)
                camera.position.y = Engine.screenHeight_InPixels() / 2;
            // Update camera matrix
            camera.update();
        }
        batch.setProjectionMatrix(camera.combined);

        //Clear background
        Gdx.gl.glClearColor(backColor.r, backColor.g, backColor.b, backColor.a);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //render background Layer
        playGround.render();
        //
        if(batch.isDrawing())
            batch.end();
        batch.begin();
        //ashley
        ashleyEngine.update(deltaTime());
        //level specific
        onDraw();
        batch.end();

        // render Light
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) //<---------todo: transfer to a level
            enableLights = !enableLights;
        if (enableLights) {
            rayHandler.setCombinedMatrix(camera.combined.cpy().scale(UnitConverter.toPixels(1), UnitConverter.toPixels(1), 1f));
            rayHandler.updateAndRender();
        }
        //render physics debug info
        debugRenderer.render();//<---- todo this changes transformation matrix!!!
        //draw on-screen debug texts
        osd.render();
    }

    /**
     * Package private
     * must only get called by engine in response to applicationListener needs
     */
    void inResponseToEngine_resize(int width, int height) {
        Engine.log.info("");
        camera.setToOrtho(false, width, height);
        camera.update();
        onResize(width, height);
    }

    /**
     * Package private
     * must only get called by engine in response to applicationListener needs
     */
    void inResponseToEngine_pause() {
        if (!paused) {
            Engine.log.info("");

            //<---todo: systems are resposible for handling pause/resume internally
            onPause();
            paused = true;
        }
    }

    /**
     * Package private
     * must only get called by engine in response to applicationListener needs
     */
    void inResponseToEngine_resume() {
        if (paused) {
            Engine.log.info("");
            Gdx.input.setInputProcessor(gestureDetector);
            if(batch.isDrawing())
                batch.end();
            //<---todo: systems are resposible for handling pause/resume internally
            onResume();
            paused = false;
        }
    }
    //endregion

    //region [Utilities to be transferred to engine?]
    public float deltaTime() {
        return (paused ? 0f : Gdx.graphics.getDeltaTime());
    }

    public Vector2 getTouchPosition_inMeters(float screenX, float screenY) {
        float mouseX = screenX + camera.position.x - Engine.screenWidth_InPixels() / 2;
        float mouseY = Engine.screenHeight_InPixels() - screenY + camera.position.y - Engine.screenHeight_InPixels() / 2;
        return new Vector2(UnitConverter.toMeters(mouseX), UnitConverter.toMeters(mouseY));
    }
    //endregion

    //region Utilities
    public String getName() {
        return this.getClass().getSimpleName();
    }

    public boolean areInContact(Body body1, Body body2) {
        CollisionStatus status = (CollisionStatus) body1.getUserData();
        if (status.numCollisions > 0 && status.other != null) {
            if (status.other == body2)
                return true;
        }
        return false;
    }
    //endregion

    //region Loading Progress
    private OrthographicCamera loadingCamera;
    private ShapeRenderer loadingRenderer;

    public void RenderLoadingProgress(float progress) {
        loadingCamera.update();
        loadingRenderer.setProjectionMatrix(loadingCamera.combined);
        //Clear background
        Color bkColor = Color.NAVY;
        Gdx.gl.glClearColor(bkColor.r, bkColor.g, bkColor.b, bkColor.a);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //Draw progress arc
        float x = Engine.screenWidth_InPixels() / 2;
        float y = Engine.screenHeight_InPixels() / 2;
        float r = 30;
        loadingRenderer.setColor(Color.LIGHT_GRAY);
        loadingRenderer.begin(ShapeRenderer.ShapeType.Filled);
        loadingRenderer.arc(x, y, r, 0, progress * 360);
        loadingRenderer.end();
        //Draw circle
        loadingRenderer.setColor(Color.LIGHT_GRAY);
        loadingRenderer.begin(ShapeRenderer.ShapeType.Line);
        loadingRenderer.circle(x, y, r);
        loadingRenderer.end();
    }
    //endregion
}