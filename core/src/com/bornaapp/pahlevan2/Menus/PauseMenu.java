package com.bornaapp.pahlevan2.Menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.bornaapp.gamelib.borna2d.game.Engine;
import com.bornaapp.gamelib.borna2d.game.LevelBase;

public class PauseMenu extends LevelBase {
    public PauseMenu() {
        super("");
    }

    private BitmapFont font;

    @Override
    public void onCreate() {
        font = new BitmapFont();//Use LibGDX's default Arial font.
    }

    @Override
    public void onDispose() {
        font.dispose();
    }

    @Override
    public void onUpdate() {
        //queryInput
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Engine.setLevel(Engine.getPreviousLevel().getName());
        }
    }

    @Override
    public void onDraw() {
        font.draw(getBatch(), "PAUSED", Engine.screenWidth_InPixels()/2f, Engine.screenHeight_InPixels()/2f);
    }

    @Override
    public void onResize(int width, int height) {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
    }

    //region Gesture Overrided methods
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
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
        return false;
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
