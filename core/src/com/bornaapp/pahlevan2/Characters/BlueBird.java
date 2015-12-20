package com.bornaapp.pahlevan2.Characters;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.bornaapp.games.borna2d.Build;
import com.bornaapp.games.borna2d.components.AnimationComponent;
import com.bornaapp.games.borna2d.components.BodyComponent;
import com.bornaapp.games.borna2d.components.SoundComponent;
import com.bornaapp.games.borna2d.components.TextureAtlasComponent;
import com.bornaapp.games.borna2d.game.Engine;
import com.bornaapp.games.borna2d.physics.BODYSHAPE;

public class BlueBird {

    private Entity entity;
    private TextureAtlasComponent texComp;
    private AnimationComponent animComp;
    private Animation anim_idle;
    private Animation anim_fly;
    public BodyComponent bodyComp;
    private SoundComponent soundComp_idle;
    private SoundComponent soundComp_wingFlap;

    public State state;

    public enum State {
        IDLE,
        FLYING
    }

    //todo: ---------- in meters or pixels?
    public BlueBird(float posX, float posY) {
        //default character state
        state = State.IDLE;
        //
        PooledEngine ashleyEngine = Engine.getCurrentLevel().getAshleyEngine();
        //
        entity = ashleyEngine.createEntity();
        ashleyEngine.addEntity(entity);
        //
        texComp = Build.AshleyComponent.Texture("bird.atlas");
        entity.add(texComp);
        //
        TextureRegion[] flyFrames = new TextureRegion[texComp.textureAtlas.getRegions().size];
        for (int i = 0; i < flyFrames.length; i++) {
            flyFrames[i] = texComp.textureAtlas.getRegions().get(i);
        }
        anim_fly = new Animation(1 / 15f, flyFrames);
        anim_fly.setPlayMode(Animation.PlayMode.LOOP);
        //
        anim_idle = new Animation(1 / 15f, texComp.textureAtlas.findRegion("0001"));
        anim_idle.setPlayMode(Animation.PlayMode.NORMAL);
        //
        animComp = Build.AshleyComponent.animation(anim_idle);
        entity.add(animComp);
        //
        Vector2 size = texComp.getSize();
        bodyComp = Build.AshleyComponent.Body(BodyDef.BodyType.DynamicBody, BODYSHAPE.RECTANGLE, posX, posY, size.x, size.y, true, 0.9f, 10, 1, true, 10);
        entity.add(bodyComp);
        //
        soundComp_idle = Build.AshleyComponent.Sound("bird_idle.mp3", true, true);
        entity.add(soundComp_idle);
        //
        soundComp_wingFlap = Build.AshleyComponent.Sound("bird.wAV", true, true);
        entity.add(soundComp_wingFlap);
    }

//    public TextureRegion getCurrentFrame() {
//
//        TextureRegion currentFrame;
//
//        switch (state) {
//            case FLYING:
//                currentFrame = anim_fly.getKeyFrame(elapsedTime, true);
//                wingFlapSound.start();
//                idleSound.stop();
//                wingFlapSound.update();
//                break;
//            case IDLE:
//                currentFrame = anim_idle.getKeyFrame(elapsedTime, true);
//                wingFlapSound.stop();
//                idleSound.start();
//                idleSound.update();
//                break;
//            default:
//                currentFrame = anim_idle.getKeyFrame(elapsedTime, true);
//        }
//        return currentFrame;
//    }
}