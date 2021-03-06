package com.bornaapp.gamelib.borna2d.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

public abstract class AppListener implements ApplicationListener {

    public AppListener(){
        //nothing in constructor!!! as Gdx is not initialized yet.
    }

    protected abstract void addLevels();
    protected abstract void setLevel();

    /**
     * gets called by application automatically when application window is started for the first time.
     */
    @Override
    public void create() {
        Engine.start();
        addLevels();
        setLevel();
    }

    /**
     * gets called by application automatically when application window is getting closed, after pause().
     */
    @Override
    public void dispose() {
        Engine.dispose();
        Gdx.app.exit();//<----todo: should always be called! here or somewhere else?
    }

    /**
     * gets called by application automatically and periodically after application window is created.
     */
    @Override
    public void render() {
        Engine.render();
    }

    /**
     * gets called by application automatically when application window is resized.
     * consequently, it also runs after application window is created for the first time.
     *
     * @param width
     * @param height
     */
    @Override
    public void resize(int width, int height) {
        Engine.resize(width, height);
    }

    /**
     * gets called by application automatically when application window is paused.
     * tconsequently, it also runs when application window loses focus, minimized, or closed.
     */
    @Override
    public void pause() {
        Engine.pause();
    }

    /**
     * gets called by application automatically when application window comes out of a paused state.
     */
    @Override
    public void resume() {
        Engine.resume();
    }
}