package com.bornaapp.pahlevan2.Levels;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.bornaapp.games.borna2d.UnitConverter;
import com.bornaapp.games.borna2d.game.Engine;
import com.bornaapp.games.borna2d.game.LevelBase;
import com.bornaapp.games.borna2d.Build;
import com.bornaapp.games.borna2d.systems.SoundSystem;
import com.bornaapp.pahlevan2.Characters.BlueBird;
import com.bornaapp.pahlevan2.Characters.Naruto;
import com.bornaapp.pahlevan2.Characters.Pedestrian;
import com.bornaapp.games.borna2d.systems.PathFindingSystem;
import com.bornaapp.games.borna2d.systems.RenderingSystem;
import com.bornaapp.games.borna2d.systems.SyncSystem;
import com.bornaapp.pahlevan2.Menus.EntryMenu;
import com.bornaapp.pahlevan2.Menus.PauseMenu;

public class L21 extends LevelBase {
    public L21() {
        super("assetManifest_L21.json");
    }

    private BlueBird bird1;
    private Naruto naruto1;
    private Pedestrian oldMan;
    private Pedestrian boy;
    private Pedestrian girl;
    private boolean chase = true;

    @Override
    protected void onCreate() {

        getPlayGround().load("L21.tmx");
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
        //creating ashley entities
        //
        bird1 = new BlueBird(300, 300);
        //
        naruto1 = new Naruto(500, 500);
        //
        oldMan = new Pedestrian("oldman1.atlas", 100, 100);
        //
        boy = new Pedestrian("man3.atlas", 1000, 400);
        //
        girl = new Pedestrian("girl6.atlas", 1100, 450);

        //Initial positioning
        //
        naruto1.bodyComp.body.setTransform(getCheckpoint("naruto_start"), 0);

        bird1.bodyComp.body.setTransform(getCheckpoint("bird1_stop1"), 0);

        oldMan.bodyComp.body.setTransform(getCheckpoint("master_start"), 0);
        oldMan.addCheckPoint(getCheckpoint("master_start"));
        oldMan.addCheckPoint(getCheckpoint("master_stop1"));
        oldMan.addCheckPoint(getCheckpoint("master_stop2"));
        oldMan.addCheckPoint(getCheckpoint("master_stop3"));

        boy.bodyComp.body.setTransform(getCheckpoint("boy_stop1"), 0);
        boy.addCheckPoint(getCheckpoint("boy_stop1"));
        boy.addCheckPoint(getCheckpoint("boy_stop2"));

        girl.bodyComp.body.setTransform(getCheckpoint("girl_stop1"), 0);
        girl.addCheckPoint(getCheckpoint("girl_stop1"));
        girl.addCheckPoint(getCheckpoint("girl_stop2"));

        //Set force and velocity
        bird1.bodyComp.body.setLinearVelocity(0f, -30f);

        if (Engine.getPreviousLevel().getName().equals(L211.class.getSimpleName()))
            naruto1.bodyComp.body.setLinearVelocity(0, 0);

        //other initializations
        this.backColor = Color.RED;
        this.enableLightEffects(false);
    }

    @Override
    protected void onDispose() {
    }

    @Override
    protected void onUpdate() {
        //queryInput
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            Engine.setLevel(PauseMenu.class.getSimpleName());

        naruto1.update();
        oldMan.update();
        boy.update();
        girl.update();

        //start update
        if (areInContact(naruto1.bodyComp.body, getPortal("shop"))) {
            naruto1.pathComp.cancelDestination();
            Engine.setLevel(L211.class.getSimpleName());
        } else if (areInContact(naruto1.bodyComp.body, getPortal("restaurant"))) {
            naruto1.pathComp.cancelDestination();
            Engine.setLevel(L212.class.getSimpleName());
        }

        //<----update Dialogs todo????? .................story & dialogs
//        for (int i = 0; i < actors.size; i++) {
//            if (actors.get(i) instanceof Pedestrian) {
//                Pedestrian ped = (Pedestrian) actors.get(i);
//                if (ped.hasStory())
//                    ped.story.updateStory(deltaTime());
//            }
//        }
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
        //Resume previous positions
        if (Engine.getPreviousLevel().getName().equals(L211.class.getSimpleName())) {
            naruto1.bodyComp.body.setTransform(getCheckpoint("naruto_211"), 0);
        } else if (Engine.getPreviousLevel().getName().equals(L212.class.getSimpleName()))
            naruto1.bodyComp.body.setTransform(getCheckpoint("naruto_212"), 0);
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
