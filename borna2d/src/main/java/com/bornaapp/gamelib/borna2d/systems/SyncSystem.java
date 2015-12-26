package com.bornaapp.gamelib.borna2d.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.bornaapp.gamelib.borna2d.components.BodyComponent;
import com.bornaapp.gamelib.borna2d.components.ParticleComponent;
import com.bornaapp.gamelib.borna2d.components.PathComponent;
import com.bornaapp.gamelib.borna2d.components.PositionComponent;
import com.bornaapp.gamelib.borna2d.game.Engine;

/**
 * Created by Mehdi on 08/29/2015.
 * Syncs different components with body component.
 */
public class SyncSystem extends IteratingSystem {

    //region Component mappers
    private ComponentMapper<ParticleComponent> partMap = ComponentMapper.getFor(ParticleComponent.class);
    private ComponentMapper<PositionComponent> posMap = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<BodyComponent> bodyMap = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<PathComponent> pathMap = ComponentMapper.getFor(PathComponent.class);
    //endregion

    public SyncSystem() {
        super(Family.all(BodyComponent.class).get(), Engine.getCurrentLevel().getSystemPriority());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        BodyComponent bodyComp = bodyMap.get(entity);
        Vector2 bodyPos = bodyComp.getPosition_inMeters();

        if (posMap.has(entity)) {
            PositionComponent posComp = posMap.get(entity);
            posComp.setX_fromMeters(bodyPos.x);
            posComp.setY_fromMeters(bodyPos.y);
        }

        if (partMap.has(entity)) {
            ParticleComponent partComp = partMap.get(entity);
            partComp.setPosition_fromMeters(bodyPos);
        }

        if (pathMap.has(entity)) {
            PathComponent pathComp = pathMap.get(entity);
            pathComp.pos = bodyPos;
            bodyComp.body.setLinearVelocity(pathComp.vel);
        }
    }
}
