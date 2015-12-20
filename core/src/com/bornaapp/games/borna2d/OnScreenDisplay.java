package com.bornaapp.games.borna2d;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bornaapp.games.borna2d.game.Engine;

import java.util.ArrayList;
import java.util.List;

public class OnScreenDisplay implements iRender, iDispose {

    public OnScreenDisplay() {
        font = new BitmapFont();//Use LibGDX's default Arial font.
        font.setColor(Color.CYAN);
        populateF1List();
    }

    private boolean visible = true;
    private boolean f1 = false;

    private class LogData {
        String title;
        String value;

        LogData(String _title, String _value) {
            title = _title;
            value = _value;
        }
    }

    private List<LogData> logList = new ArrayList<LogData>();
    private List<String> f1List = new ArrayList<String>();

    private BitmapFont font;

    public void log(String title, int value) {
        log(title, Integer.toString(value));
    }

    public void log(String title, float value) {
        log(title, Float.toString(value));
    }

    public void log(String title, Vector2 value) {
        log(title, Float.toString(value.x) + "," + Float.toString(value.y));
    }

    public void log(String title, String value) {
        if (!visible)
            return;

        for (LogData data : logList) {
            if (data.title.equals(title)) {
                data.value = value;
                return;
            }
        }
        logList.add(new LogData(title, value));
    }

    @Override
    public void render() {
        if (!visible)
            return;

        float xOffset = 20f;
        float yOffset = 20f;
        float lineHeight = 20f;

        float x = xOffset - Engine.screenWidth_InPixels() / 2;
        float y = yOffset - Engine.screenHeight_InPixels() / 2;

        Camera camera = Engine.getCurrentLevel().getCamera();
        camera.unproject(new Vector3(x, y, 0));

        SpriteBatch batch = Engine.getCurrentLevel().getBatch();
        batch.setProjectionMatrix(camera.projection);
        if (!batch.isDrawing())
            batch.begin();
        if (f1) {
            for (String str : f1List) {
                font.draw(batch, str, x, y);
                y += lineHeight;
            }
        } else {
            for (LogData data : logList) {
                font.draw(batch, data.title + " : " + data.value, x, y);
                y += lineHeight;
            }
        }
        batch.end();
    }

    @Override
    public void dispose() {
        font.dispose();
        logList.clear();
    }

    //region utility methods
    public boolean isVisible() {
        return visible;
    }

    public void SetVisible(boolean _state) {
        visible = _state;
    }

    public boolean getF1() {
        return f1;
    }

    public void SetF1(boolean _state) {
        f1 = _state;
    }

    public void populateF1List() {
        f1List.add("F1   : Show/Hide instructions");
        f1List.add("F2   : Show/Hide Logs");
        f1List.add("NUM_1  Enable/Disable Lighting: ");
        f1List.add("NUM_2: Show/Hide bounding areas");
    }
    //endregion
}
