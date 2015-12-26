package com.bornaapp.pahlevan2.Levels;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.bornaapp.gamelib.borna2d.UnitConverter;
import com.bornaapp.gamelib.borna2d.game.Engine;
import com.bornaapp.gamelib.borna2d.game.LevelBase;
import com.bornaapp.gamelib.borna2d.systems.PathFindingSystem;
import com.bornaapp.gamelib.borna2d.systems.RenderingSystem;
import com.bornaapp.gamelib.borna2d.systems.SoundSystem;
import com.bornaapp.gamelib.borna2d.systems.SyncSystem;
import com.bornaapp.pahlevan2.Characters.Naruto;
import com.bornaapp.pahlevan2.Characters.Pedestrian;
import com.bornaapp.pahlevan2.Menus.PauseMenu;

public class L212 extends LevelBase {
    public L212() {
        super("assetManifest_L212.json");
    }

    private Naruto naruto1;
    private Pedestrian chef;
    private boolean chase = true;

    @Override
    protected void onCreate() {
        getPlayGround().load("L212.tmx");
        //
        //Ashley
        PooledEngine ashleyEngine = getAshleyEngine();
        //
        //registering ashley system
        ashleyEngine.addSystem(new PathFindingSystem());
        ashleyEngine.addSystem(new SyncSystem());
        ashleyEngine.addSystem(new RenderingSystem());
        ashleyEngine.addSystem(new SoundSystem());
        //
        naruto1 = new Naruto(100, 100);
        chef = new Pedestrian("girl9.atlas", 200, 200);
        //
        naruto1.bodyComp.body.setTransform(getCheckpoint("naruto_start"), 0);
        //
        chef.bodyComp.body.setTransform(getCheckpoint("chef_start"), 0);
        chef.addCheckPoint(getCheckpoint("chef_start"));
        chef.addCheckPoint(getCheckpoint("chef_stop"));
        //other initializations
        this.backColor = Color.BLACK;
        enableLightEffects(false);
    }

    @Override
    protected void onDispose() {
    }

    @Override
    public void onUpdate() {
        //queryInput
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            Engine.setLevel(PauseMenu.class.getSimpleName());

        naruto1.update();
        chef.update();

        if (areInContact(naruto1.bodyComp.body, getPortal("exit"))) {
            naruto1.pathComp.cancelDestination();
            Engine.setLevel(L21.class.getSimpleName());
        }

        //camera chases the naruto
        if (chase) {
            Vector2 cameraPosition = new Vector2(UnitConverter.toMeters(getCamera().position.x), UnitConverter.toMeters(getCamera().position.y));
            Vector2 dir = new Vector2(naruto1.bodyComp.getPosition_inMeters());
            dir.sub(cameraPosition).scl(35f);//<---todo: camera speed
            if (dir.len() > 1) {//<----todo: camera offcenter
                Vector2 cameraTranslate = new Vector2();
                cameraTranslate.mulAdd(dir, deltaTime());
                getCamera().translate(cameraTranslate);
            }
        }

        //Update on-screen debug texts
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1))
            osd.SetF1(!osd.getF1());
        if (Gdx.input.isKeyJustPressed(Input.Keys.F2))
            osd.SetVisible(!osd.isVisible());
        osd.log("FPS", Integer.toString(Engine.frameRate()));
        //render physics debug info
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2))
            debugRenderer.SetVisible(!debugRenderer.isVisible());
    }

    @Override
    protected void onDraw() {
    }

    @Override
    protected void onResize(int width, int height) {
    }

    @Override
    protected void onPause() {
    }

    @Override
    protected void onResume() {
        naruto1.bodyComp.body.setTransform(getCheckpoint("naruto_start"), 0);
    }

    //region Gesture Overrided methods
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        chase = true;
        Vector2 dest = getTouchPosition_inMeters(x, y);
        naruto1.pathComp.setDestination_inMeters(dest.x, dest.y);
        return true;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        chase = false;
        getCamera().translate(new Vector2(-deltaX, deltaY));
        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }
    //endregion
}
