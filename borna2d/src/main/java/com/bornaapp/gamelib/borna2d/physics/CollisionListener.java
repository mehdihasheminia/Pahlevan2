package com.bornaapp.gamelib.borna2d.physics;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.bornaapp.gamelib.borna2d.game.Engine;

public class CollisionListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        try {
            //Read Data of Objects
            CollisionStatus statusA = (CollisionStatus) contact.getFixtureA().getBody().getUserData();
            CollisionStatus statusB = (CollisionStatus) contact.getFixtureB().getBody().getUserData();
            //Set new data for objects
            contact.getFixtureA().getBody().setUserData(new CollisionStatus(statusA.owner, statusB.owner, statusA.numCollisions + 1));
            contact.getFixtureB().getBody().setUserData(new CollisionStatus(statusB.owner, statusA.owner, statusB.numCollisions + 1));

//            Engine.log.debug( "A: "+Integer.toString(statusA.numCollisions + 1)+"B: "+Integer.toString(statusB.numCollisions + 1));

        } catch (Exception e) {
            Engine.log.error(e.getMessage());
        }
    }

    @Override
    public void endContact(Contact contact) {
        try {
            //Read Data of Objects
            CollisionStatus statusA = (CollisionStatus) contact.getFixtureA().getBody().getUserData();
            CollisionStatus statusB = (CollisionStatus) contact.getFixtureB().getBody().getUserData();
            //Set new data for objects
            contact.getFixtureA().getBody().setUserData(new CollisionStatus(statusA.owner, null, statusA.numCollisions - 1));
            contact.getFixtureB().getBody().setUserData(new CollisionStatus(statusB.owner, null, statusB.numCollisions - 1));

//            Engine.log.debug( "A: " + Integer.toString(statusA.numCollisions -1) + "B: " + Integer.toString(statusB.numCollisions - 1));
        } catch (Exception e) {
            Engine.log.error(e.getMessage());
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
