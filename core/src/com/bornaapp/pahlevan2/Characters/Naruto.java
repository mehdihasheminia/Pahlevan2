package com.bornaapp.pahlevan2.Characters;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.bornaapp.gamelib.borna2d.Build;
import com.bornaapp.gamelib.borna2d.components.AnimationComponent;
import com.bornaapp.gamelib.borna2d.components.BodyComponent;
import com.bornaapp.gamelib.borna2d.components.PathComponent;
import com.bornaapp.gamelib.borna2d.components.SoundComponent;
import com.bornaapp.gamelib.borna2d.components.TextureAtlasComponent;
import com.bornaapp.gamelib.borna2d.game.Engine;
import com.bornaapp.gamelib.borna2d.physics.BODYSHAPE;

public class Naruto {

    private Entity entity;
    private TextureAtlasComponent texComp;
    private AnimationComponent animComp;
    private Animation anim_walkDown;
    private Animation anim_walkUp;
    private Animation anim_walkLeft;
    private Animation anim_walkRight;
    private Animation anim_idle;
    public BodyComponent bodyComp;
    private SoundComponent soundComp_walk;
    public PathComponent pathComp;

    public Naruto(float posX, float posY) {
        PooledEngine ashleyEngine = Engine.getCurrentLevel().getAshleyEngine();
        //
        entity = ashleyEngine.createEntity();
        ashleyEngine.addEntity(entity);
        //
        texComp = Build.AshleyComponent.Texture("naruto.atlas");
        entity.add(texComp);
        // Walk-Down Animation
        TextureRegion[] walkDownFrames = new TextureRegion[8];
        walkDownFrames[0] = (texComp.textureAtlas.findRegion("WD01"));
        walkDownFrames[1] = (texComp.textureAtlas.findRegion("WD02"));
        walkDownFrames[2] = (texComp.textureAtlas.findRegion("WD03"));
        walkDownFrames[3] = (texComp.textureAtlas.findRegion("WD04"));
        walkDownFrames[4] = (texComp.textureAtlas.findRegion("WD05"));
        walkDownFrames[5] = (texComp.textureAtlas.findRegion("WD06"));
        walkDownFrames[6] = (texComp.textureAtlas.findRegion("WD07"));
        walkDownFrames[7] = (texComp.textureAtlas.findRegion("WD08"));
        anim_walkDown = new Animation(1 / 15f, walkDownFrames);
        anim_walkDown.setPlayMode(Animation.PlayMode.LOOP);
        // Walk-Up Animation
        TextureRegion[] walkUpFrames = new TextureRegion[8];
        walkUpFrames[0] = (texComp.textureAtlas.findRegion("WU01"));
        walkUpFrames[1] = (texComp.textureAtlas.findRegion("WU02"));
        walkUpFrames[2] = (texComp.textureAtlas.findRegion("WU03"));
        walkUpFrames[3] = (texComp.textureAtlas.findRegion("WU04"));
        walkUpFrames[4] = (texComp.textureAtlas.findRegion("WU05"));
        walkUpFrames[5] = (texComp.textureAtlas.findRegion("WU06"));
        walkUpFrames[6] = (texComp.textureAtlas.findRegion("WU07"));
        walkUpFrames[7] = (texComp.textureAtlas.findRegion("WU08"));
        anim_walkUp = new Animation(1 / 15f, walkUpFrames);
        anim_walkUp.setPlayMode(Animation.PlayMode.LOOP);
        // Walk-Left Animation
        TextureRegion[] walkLeftFrames = new TextureRegion[8];
        walkLeftFrames[0] = (texComp.textureAtlas.findRegion("WL01"));
        walkLeftFrames[1] = (texComp.textureAtlas.findRegion("WL02"));
        walkLeftFrames[2] = (texComp.textureAtlas.findRegion("WL03"));
        walkLeftFrames[3] = (texComp.textureAtlas.findRegion("WL04"));
        walkLeftFrames[4] = (texComp.textureAtlas.findRegion("WL05"));
        walkLeftFrames[5] = (texComp.textureAtlas.findRegion("WL06"));
        walkLeftFrames[6] = (texComp.textureAtlas.findRegion("WL07"));
        walkLeftFrames[7] = (texComp.textureAtlas.findRegion("WL08"));
        anim_walkLeft = new Animation(1 / 15f, walkLeftFrames);
        anim_walkLeft.setPlayMode(Animation.PlayMode.LOOP);
        // Walk-Right Animation
        TextureRegion[] walkRightFrames = new TextureRegion[8];
        walkRightFrames[0] = (texComp.textureAtlas.findRegion("WR01"));
        walkRightFrames[1] = (texComp.textureAtlas.findRegion("WR02"));
        walkRightFrames[2] = (texComp.textureAtlas.findRegion("WR03"));
        walkRightFrames[3] = (texComp.textureAtlas.findRegion("WR04"));
        walkRightFrames[4] = (texComp.textureAtlas.findRegion("WR05"));
        walkRightFrames[5] = (texComp.textureAtlas.findRegion("WR06"));
        walkRightFrames[6] = (texComp.textureAtlas.findRegion("WR07"));
        walkRightFrames[7] = (texComp.textureAtlas.findRegion("WR08"));
        anim_walkRight = new Animation(1 / 15f, walkRightFrames);
        anim_walkRight.setPlayMode(Animation.PlayMode.LOOP);
        // Idle Animation
        anim_idle = new Animation(1 / 15f, texComp.textureAtlas.findRegion("WD01"));
        anim_idle.setPlayMode(Animation.PlayMode.NORMAL);
        //
        animComp = Build.AshleyComponent.animation(anim_idle);
        entity.add(animComp);
        //
        Vector2 size = texComp.getSize();
        bodyComp = Build.AshleyComponent.Body(BodyDef.BodyType.DynamicBody, BODYSHAPE.RECTANGLE, posX, posY, size.x, size.y, true, 0.7f, 10f, 2f, false, 10f);
        entity.add(bodyComp);
        //
        pathComp = Build.AshleyComponent.PathFinder();
        entity.add(pathComp);
        //
        soundComp_walk = Build.AshleyComponent.Sound("walking.wav", true, true);
        entity.add(soundComp_walk);
    }

    public void update() {
        //idle
        if (bodyComp.body.getLinearVelocity().len() < 0.2f) {
            animComp.animation = anim_idle;
            soundComp_walk.playBackState = SoundComponent.PLAYBACK.STOP;
        } else {
            float angle = bodyComp.body.getLinearVelocity().angle();
            if (angle <= 45f || angle >= 315f) {
                animComp.animation = anim_walkRight;
                soundComp_walk.playBackState = SoundComponent.PLAYBACK.PLAY;
            } else if (angle > 45f && angle <= 135f) {
                animComp.animation = anim_walkUp;
                soundComp_walk.playBackState = SoundComponent.PLAYBACK.PLAY;
            } else if (angle > 135f && angle <= 225f) {
                animComp.animation = anim_walkLeft;
                soundComp_walk.playBackState = SoundComponent.PLAYBACK.PLAY;
            } else if (angle > 225f && angle < 315f) {
                animComp.animation = anim_walkDown;
                soundComp_walk.playBackState = SoundComponent.PLAYBACK.PLAY;
            } else {
                animComp.animation = anim_idle;
                soundComp_walk.playBackState = SoundComponent.PLAYBACK.STOP;
            }
        }
    }
}
