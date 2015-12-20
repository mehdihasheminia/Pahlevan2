package com.bornaapp.games.borna2d.components;

import com.badlogic.ashley.core.Component;
import com.bornaapp.games.borna2d.UnitConverter;

public class PositionComponent extends Component {
    private float x, y;

    //region Getters
    public float getX_inMeters() {
        return x;
    }

    public int getX_inPixels() {
        return UnitConverter.toPixels(x);
    }

    public float getY_inMeters() {
        return y;
    }

    public int getY_inPixels() {
        return UnitConverter.toPixels(y);
    }
    //endregion

    //region Setters
    public void setX_fromMeters(float value_inMeters) {
        x = value_inMeters;
    }

    public void setX_fromPixels(float value_inPixels) {
        x = UnitConverter.toMeters(value_inPixels);
    }

    public void setY_fromMeters(float value_inMeters) {
        y = value_inMeters;
    }

    public void setY_fromPixels(float value_inPixels) {
        y = UnitConverter.toMeters(value_inPixels);
    }
    //endregion
}
