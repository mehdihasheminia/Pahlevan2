package com.bornaapp.pahlevan2.Characters;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.bornaapp.gamelib.borna2d.Build;
import com.bornaapp.gamelib.borna2d.components.AnimationComponent;
import com.bornaapp.gamelib.borna2d.components.BodyComponent;
import com.bornaapp.gamelib.borna2d.components.PathComponent;
import com.bornaapp.gamelib.borna2d.components.TextureAtlasComponent;
import com.bornaapp.gamelib.borna2d.game.Engine;
import com.bornaapp.gamelib.borna2d.physics.BODYSHAPE;

public class Pedestrian {

    private Entity entity;
    private TextureAtlasComponent texComp;
    private AnimationComponent animComp;
    private Animation anim_walkDown;
    private Animation anim_walkUp;
    private Animation anim_walkLeft;
    private Animation anim_walkRight;
    private Animation anim_idle;
    public BodyComponent bodyComp;
    private PathComponent pathComp;

    private Array<Vector2> destinations = new Array<Vector2>();
    private int currentDestIndex = 0;
    private float waitTime = 0f;

    public Pedestrian(String texturePath, float posX, float posY) {
        PooledEngine ashleyEngine = Engine.getCurrentLevel().getAshleyEngine();
        //
        entity = ashleyEngine.createEntity();
        ashleyEngine.addEntity(entity);
        //
        texComp = Build.AshleyComponent.Texture(texturePath);
        entity.add(texComp);
        // Walk-Down Animation
        TextureRegion[] walkDownFrames = new TextureRegion[3];
        walkDownFrames[0] = (texComp.textureAtlas.findRegion("WD1"));
        walkDownFrames[1] = (texComp.textureAtlas.findRegion("WD2"));
        walkDownFrames[2] = (texComp.textureAtlas.findRegion("WD3"));
        anim_walkDown = new Animation(1 / 15f, walkDownFrames);
        anim_walkDown.setPlayMode(Animation.PlayMode.LOOP);
        // Walk-Up Animation
        TextureRegion[] walkUpFrames = new TextureRegion[3];
        walkUpFrames[0] = (texComp.textureAtlas.findRegion("WU1"));
        walkUpFrames[1] = (texComp.textureAtlas.findRegion("WU2"));
        walkUpFrames[2] = (texComp.textureAtlas.findRegion("WU3"));
        anim_walkUp = new Animation(1 / 15f, walkUpFrames);
        anim_walkUp.setPlayMode(Animation.PlayMode.LOOP);
        // Walk-Left Animation
        TextureRegion[] walkLeftFrames = new TextureRegion[3];
        walkLeftFrames[0] = (texComp.textureAtlas.findRegion("WL1"));
        walkLeftFrames[1] = (texComp.textureAtlas.findRegion("WL2"));
        walkLeftFrames[2] = (texComp.textureAtlas.findRegion("WL3"));
        anim_walkLeft = new Animation(1 / 15f, walkLeftFrames);
        anim_walkLeft.setPlayMode(Animation.PlayMode.LOOP);
        // Walk-Right Animation
        TextureRegion[] walkRightFrames = new TextureRegion[3];
        walkRightFrames[0] = (texComp.textureAtlas.findRegion("WR1"));
        walkRightFrames[1] = (texComp.textureAtlas.findRegion("WR2"));
        walkRightFrames[2] = (texComp.textureAtlas.findRegion("WR3"));
        anim_walkRight = new Animation(1 / 15f, walkRightFrames);
        anim_walkRight.setPlayMode(Animation.PlayMode.LOOP);
        // Idle Animation
        anim_idle = new Animation(1 / 15f, texComp.textureAtlas.findRegion("WD2"));
        anim_idle.setPlayMode(Animation.PlayMode.NORMAL);
        //
        animComp = Build.AshleyComponent.animation(anim_idle);
        entity.add(animComp);
        //
        Vector2 size = texComp.getSize();
        bodyComp = Build.AshleyComponent.Body(BodyDef.BodyType.DynamicBody, BODYSHAPE.RECTANGLE, posX, posY, size.x, size.y, true, 0.7f, 10, 1, false, 10);
        entity.add(bodyComp);
        //
        pathComp = Build.AshleyComponent.PathFinder();
        entity.add(pathComp);
        //
        pathComp.setMaxLinearVelocity(2.5f);
    }

    //    public void updatePhysics() {
//        super.updatePhysics();
//
//        //<---------------------------------------------------
//        if (this.hasStory()) {
//            if (Gdx.input.justTouched()) {
//                Vector2 mouse = Engine.getCurrentLevel().getTouchPosition_inMeters(Gdx.input.getX(), Gdx.input.getY());
//                Vector2 position = this.getPosition_inMeters();
//                Vector2 size = this.getSize_inMeters();
//                //Check if mouse picked object//<-------- todo: make it a method with considerinf z
//                if (mouse.x >= position.x - size.x / 2 && mouse.x <= position.x + size.x / 2) {
//                    if (mouse.y >= position.y - size.y / 2 && mouse.y <= position.y + size.y / 2) {
//                        this.story.active = true;
//                    }
//                }
//            }
//        }
//    }
//
    public void update() {
        //Animation
        if (bodyComp.body.getLinearVelocity().len() < 0.2f)
            animComp.animation = anim_idle;
        else {
            float angle = bodyComp.body.getLinearVelocity().angle();
            if (angle <= 45f || angle >= 315f)
                animComp.animation = anim_walkRight;
            else if (angle > 45f && angle <= 135f)
                animComp.animation = anim_walkUp;
            else if (angle > 135f && angle <= 225f)
                animComp.animation = anim_walkLeft;
            else if (angle > 225f && angle < 315f)
                animComp.animation = anim_walkDown;
            else
                animComp.animation = anim_idle;
        }
        //behaviour
        if (!pathComp.isDestinationValid()) {
            waitTime += Engine.getCurrentLevel().deltaTime();
            if (waitTime > 5f) {
                waitTime = 0f;
                currentDestIndex++;
                if (currentDestIndex >= destinations.size)
                    currentDestIndex = 0;
                pathComp.setDestination_inMeters(destinations.get(currentDestIndex));
            }
        }
    }

    public void addCheckPoint(Vector2 checkpoint) {
        destinations.add(checkpoint);
    }
}