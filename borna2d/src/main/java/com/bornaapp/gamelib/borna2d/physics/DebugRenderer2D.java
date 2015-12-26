package com.bornaapp.gamelib.borna2d.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.bornaapp.gamelib.borna2d.UnitConverter;
import com.bornaapp.gamelib.borna2d.game.Engine;
import com.bornaapp.gamelib.borna2d.game.LevelBase;
import com.bornaapp.gamelib.borna2d.iDispose;
import com.bornaapp.gamelib.borna2d.iRender;

public class DebugRenderer2D implements iRender, iDispose {
    public DebugRenderer2D() {
        debugRenderer = new Box2DDebugRenderer();
    }

    private Box2DDebugRenderer debugRenderer;
    private boolean visible = false;

    @Override
    public void render() {
        if (!visible)
            return;
        LevelBase l = Engine.getCurrentLevel();
        Matrix4 debugMatrix = l.getBatch().getProjectionMatrix().cpy().scale(UnitConverter.toPixels(1), UnitConverter.toPixels(1), 0);
        debugRenderer.render(l.getWorld(), debugMatrix);
    }

    @Override
    public void dispose() {
        debugRenderer.dispose();
    }

    public boolean isVisible() {
        return visible;
    }

    public void SetVisible(boolean _state) {
        visible = _state;
    }
}
