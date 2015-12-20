package com.bornaapp.games.borna2d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Mehdi on 09/02/2015.
 * ...
 */
public class TextureAtlasComponent extends Component {

    public TextureAtlas textureAtlas;

    public Vector2 getSize() {
        TextureAtlas.AtlasRegion region = textureAtlas.getRegions().get(0);
        return new Vector2(region.getRegionWidth(), region.getRegionHeight());
    }

    public TextureAtlas.AtlasRegion getRegion(int i) {
        return textureAtlas.getRegions().get(i);
    }
}
