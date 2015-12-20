package com.bornaapp.games.borna2d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.Vector2;
import com.bornaapp.games.borna2d.UnitConverter;

/**
 *
 */
public class ParticleComponent extends Component {
    public ParticleEffect particleEffect;

    //region Getters
    public Vector2 getPosition_inMeters() {
        ParticleEmitter firstEmitter = particleEffect.getEmitters().get(0);
        return new Vector2(UnitConverter.toMeters(firstEmitter.getX()), UnitConverter.toMeters(firstEmitter.getY()));
    }

    public Vector2 getPosition_inPixels() {
        ParticleEmitter firstEmitter = particleEffect.getEmitters().get(0);
        return new Vector2(firstEmitter.getX(), firstEmitter.getY());
    }
    //endregion

    //region Setters
    public void setPosition_fromMeters(Vector2 value_inMeters) {
        particleEffect.setPosition(UnitConverter.toPixels(value_inMeters.x), UnitConverter.toPixels(value_inMeters.y));
    }

    public void setPosition_fromPixels(Vector2 value_inPixels) {
        particleEffect.setPosition(value_inPixels.x, value_inPixels.y);
    }
    //endregion
}
