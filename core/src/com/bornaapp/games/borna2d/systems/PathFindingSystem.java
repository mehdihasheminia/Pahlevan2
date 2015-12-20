package com.bornaapp.games.borna2d.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.bornaapp.games.borna2d.ai.PathFindingDebugger;
import com.bornaapp.games.borna2d.components.BodyComponent;
import com.bornaapp.games.borna2d.components.PathComponent;
import com.bornaapp.games.borna2d.game.Engine;
import com.bornaapp.games.borna2d.physics.CollisionStatus;

/**
 * Created by Mehdi on 08/30/2015.
 * ...
 */
public class PathFindingSystem extends IteratingSystem {

    //region Component mappers
    private ComponentMapper<PathComponent> pathMap = ComponentMapper.getFor(PathComponent.class);
    private ComponentMapper<BodyComponent> bodyMap = ComponentMapper.getFor(BodyComponent.class);
    //endregion

    private PathFindingDebugger pathFindingDebugger = new PathFindingDebugger();

    public PathFindingSystem() {
        super(Family.all(PathComponent.class).get(), Engine.getCurrentLevel().getSystemPriority());
    }

    //todo: 1- doesn't have to be paired with a Box2D body
    //todo: 2- rendering problem
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PathComponent pathComp = pathMap.get(entity);

        if (isInCollision(entity)) {
            pathComp.gotoNextNode();
            pathComp.CalculateBounceVelocity(deltaTime);
            //here! why rendering problem on collision?
        }
        else
        if (pathComp.isDestinationValid()) {
            //
            if (pathComp.drawDebug)
                pathFindingDebugger.drawPath(pathComp.path);
            //
            if (pathComp.reachedDestination()) {
                //
                pathComp.cancelDestination();
                //
            } else {
                //
                if (!pathComp.reachedCurrentNode())
                    pathComp.CalculateVelocityToCurrentNode(deltaTime);
                else
                    pathComp.gotoNextNode();
            }
        }
    }

    private boolean isInCollision(Entity entity) {
        if (!bodyMap.has(entity))
            return false;

        boolean collisionHappened = false;
        BodyComponent bodyComp = bodyMap.get(entity);

        CollisionStatus cStatus = (CollisionStatus) bodyComp.body.getUserData();
        if (cStatus == null)
            Engine.log.error("cStatus == null");
        else
            collisionHappened = cStatus.numCollisions > 0 && !cStatus.other.getFixtureList().get(0).isSensor(); //<---todo: what if there are other fixtures?

//        if (!collisionHappened)
//            lastPosition = owner.getPosition_inMeters();
//
//        if (collisionHappened) {
//            Vector2 distance = lastPosition;
//            distance.sub(owner.getPosition_inMeters());
//            if (distance.len() < 0.05f)  //todo<------------actually always true !?
//                collisionHappened = false;
//        }
        return collisionHappened;
    }
}
