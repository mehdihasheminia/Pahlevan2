package com.bornaapp.games.borna2d.physics;

import com.badlogic.gdx.physics.box2d.Body;

public class CollisionStatus {
    public int numCollisions = 0;
    public Body owner = null;
    public Body other = null;

    public CollisionStatus(Body _owner, Body _other, int _numCollisions) {
        owner = _owner;
        other = _other;
        numCollisions = _numCollisions;
    }
}
