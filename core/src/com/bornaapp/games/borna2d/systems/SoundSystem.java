package com.bornaapp.games.borna2d.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.bornaapp.games.borna2d.UnitConverter;
import com.bornaapp.games.borna2d.components.BodyComponent;
import com.bornaapp.games.borna2d.components.ParticleComponent;
import com.bornaapp.games.borna2d.components.PathComponent;
import com.bornaapp.games.borna2d.components.PositionComponent;
import com.bornaapp.games.borna2d.components.SoundComponent;
import com.bornaapp.games.borna2d.game.Engine;

/**
 * Created by Mehdi on 9/25/2015.
 * ...
 */
public class SoundSystem extends IteratingSystem {

    //region Component mappers
    private ComponentMapper<SoundComponent> soundMap = ComponentMapper.getFor(SoundComponent.class);
    private ComponentMapper<PositionComponent> posMap = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<ParticleComponent> partMap = ComponentMapper.getFor(ParticleComponent.class);
    private ComponentMapper<BodyComponent> bodyMap = ComponentMapper.getFor(BodyComponent.class);
    //endregion

    public SoundSystem() {
        super(Family.all(SoundComponent.class).get(), Engine.getCurrentLevel().getSystemPriority());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        SoundComponent soundComp = soundMap.get(entity);

        if (soundComp.playBackState == SoundComponent.PLAYBACK.PLAY) {
            if (soundComp.is3D) {
                //Calculating Pan value
                Camera camera = Engine.getCurrentLevel().getCamera();
                Vector2 distance = new Vector2(camera.position.x, camera.position.y);
                distance.sub(new Vector2(getX(entity), getY(entity)));
                //
                soundComp.volume = Math.min(UnitConverter.toPixels(soundComp.volumeDegradationDistance) / (Math.max(distance.len(), 1f)), 1f);
                soundComp.pan = -distance.x / (Engine.screenWidth_InPixels() / 2);
                //
                if (soundComp.pan > 1f)
                    soundComp.pan = 1f;
                else if (soundComp.pan < -1f)
                    soundComp.pan = -1f;
                //
                soundComp.sound.setPan(soundComp.soundID, soundComp.pan, soundComp.volume);
                //Calculationg Pitch Value
                //value of pich should be > 0.5 and < 2.0. Must be calculated by velocity
                soundComp.sound.setPitch(soundComp.soundID, soundComp.pitch);
            }
            if (soundComp.soundID == -1)
                soundComp.soundID = soundComp.sound.play();
            else
                soundComp.sound.resume(soundComp.soundID);
        }
        if (soundComp.playBackState == SoundComponent.PLAYBACK.PAUSE || deltaTime == 0f) {
            soundComp.sound.pause(soundComp.soundID);
        }
        if (soundComp.playBackState == SoundComponent.PLAYBACK.STOP) {
            soundComp.sound.stop(soundComp.soundID);
            soundComp.soundID = -1;
        }
    }

    //region Parametr extraction methods

    /**
     * This method, extract Y from entities, based on components it has
     *
     * @param entity
     * @return "Y" of entity in pixels, to compare with other entities.
     */
    private float getY(Entity entity) {

        if (posMap.has(entity)) {
            PositionComponent posComp = posMap.get(entity);
            return posComp.getY_inPixels();
        }

        if (bodyMap.has(entity)) {
            BodyComponent bodyComp = bodyMap.get(entity);
            return bodyComp.getPosition_inPixels().y;
        }

        if (partMap.has(entity)) {
            ParticleComponent partComp = partMap.get(entity);
            return partComp.getPosition_inPixels().y;
        }

        return Float.MAX_VALUE;
    }

    /**
     * This method, extract X from entities, based on components it has
     *
     * @param entity
     * @return "X" of entity in pixels, to compare with other entities.
     */
    private float getX(Entity entity) {

        if (posMap.has(entity)) {
            PositionComponent posComp = posMap.get(entity);
            return posComp.getX_inPixels();
        }

        if (bodyMap.has(entity)) {
            BodyComponent bodyComp = bodyMap.get(entity);
            return bodyComp.getPosition_inPixels().x;
        }

        if (partMap.has(entity)) {
            ParticleComponent partComp = partMap.get(entity);
            return partComp.getPosition_inPixels().x;
        }

        return Float.MAX_VALUE;
    }
    //endregion
}
