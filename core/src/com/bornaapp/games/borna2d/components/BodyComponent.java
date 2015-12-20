package com.bornaapp.games.borna2d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.bornaapp.games.borna2d.UnitConverter;

/**
 * Created by Mehdi on 08/29/2015.
 * ...
 */
public class BodyComponent extends Component {
    public Body body;

    //region Getters
    public Vector2 getPosition_inMeters() {
        return new Vector2(body.getPosition());
    }

    public Vector2 getPosition_inPixels() {
        Vector2 bodyPos = body.getPosition();
        return new Vector2(UnitConverter.toPixels(bodyPos.x), UnitConverter.toPixels(bodyPos.y));
    }
    //endregion

    //region Setters
    public void setPosition_fromMeters(Vector2 value_inMeters) {
        body.setTransform(value_inMeters, 0);
    }

    public void setPosition_fromPixels(Vector2 value_inPixels) {
        body.setTransform(UnitConverter.toMeters(value_inPixels.x), UnitConverter.toMeters(value_inPixels.y), 0);
    }
    //endregion
}