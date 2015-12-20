package com.bornaapp.games.borna2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.bornaapp.games.borna2d.components.NameComponent;
import com.bornaapp.games.borna2d.game.Engine;
import com.bornaapp.games.borna2d.physics.BODYSHAPE;
import com.bornaapp.games.borna2d.components.AnimationComponent;
import com.bornaapp.games.borna2d.components.BodyComponent;
import com.bornaapp.games.borna2d.components.ParticleComponent;
import com.bornaapp.games.borna2d.components.PathComponent;
import com.bornaapp.games.borna2d.components.PositionComponent;
import com.bornaapp.games.borna2d.components.SoundComponent;
import com.bornaapp.games.borna2d.components.TextureAtlasComponent;
import com.bornaapp.games.borna2d.physics.CollisionStatus;

/**
 *
 */
public class Build {

    // can be specific to engine
    public static class AshleyComponent {

        public static NameComponent Name(String name) {
            PooledEngine ashleyEngine = Engine.getCurrentLevel().getAshleyEngine();
            //
            NameComponent nameComp = ashleyEngine.createComponent(NameComponent.class);
            nameComp.name = name;
            //
            return nameComp;
        }

        public static PositionComponent Position(float x_inMeters, float y_inMeters) {
            PooledEngine ashleyEngine = Engine.getCurrentLevel().getAshleyEngine();
            //
            PositionComponent posComp = ashleyEngine.createComponent(PositionComponent.class);
            posComp.setX_fromMeters(x_inMeters);
            posComp.setY_fromMeters(y_inMeters);
            return posComp;
        }

        public static ParticleComponent Particle(String filePath, float x, float y) {
            PooledEngine ashleyEngine = Engine.getCurrentLevel().getAshleyEngine();
            //
            ParticleComponent partComp = ashleyEngine.createComponent(ParticleComponent.class);
            partComp.particleEffect = new ParticleEffect(Engine.getCurrentLevel().getAssetManager().getParticleEffect(filePath));//<---todo: assets get cleaned on exit? I mean because of 'new' !
            partComp.particleEffect.start();
            partComp.particleEffect.setPosition(x, y);
            return partComp;
        }

        //todo...parameters like x,y,w,h in meter or pixels?
        public static BodyComponent Body(BodyDef.BodyType bodyType, BODYSHAPE bodyShape, float x, float y, float width, float height, boolean fixRotation, float shrinkFactor, float density, float bounciness, boolean isSensor, float friction) { //variables in pixels
            PooledEngine ashleyEngine = Engine.getCurrentLevel().getAshleyEngine();
            BodyComponent bodyComponent = ashleyEngine.createComponent(BodyComponent.class);
            //Box2D Def & fixture
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = bodyType;
            bodyDef.fixedRotation = fixRotation;

            Shape shape;
            switch (bodyShape) {
                case CIRCLE:
                    shape = new CircleShape();
                    ((CircleShape) shape).setRadius(shrinkFactor * Math.max(UnitConverter.toMeters(width) / 2, UnitConverter.toMeters(height) / 2)); //TODO why w/2 ?
                    bodyDef.position.set(UnitConverter.toMeters(x) + UnitConverter.toMeters(width) / 2, UnitConverter.toMeters(y) + UnitConverter.toMeters(height) / 2);
                    break;
                case EDGE:
                    shape = new EdgeShape();
                    ((EdgeShape) shape).set(new Vector2(UnitConverter.toMeters(x), UnitConverter.toMeters(y)), new Vector2(UnitConverter.toMeters(x) + UnitConverter.toMeters(width), UnitConverter.toMeters(y) + UnitConverter.toMeters(height)));
                    break;
                default:
                case RECTANGLE:
                    shape = new PolygonShape();
                    ((PolygonShape) shape).setAsBox(shrinkFactor * UnitConverter.toMeters(width) / 2, shrinkFactor * UnitConverter.toMeters(height) / 2); //TODO why w/2 ?
                    bodyDef.position.set(UnitConverter.toMeters(x) + UnitConverter.toMeters(width) / 2, UnitConverter.toMeters(y) + UnitConverter.toMeters(height) / 2);
                    break;
            }
            FixtureDef fixDef = new FixtureDef();
            fixDef.shape = shape;
            fixDef.density = density;
            fixDef.restitution = bounciness;
            fixDef.isSensor = isSensor;
            fixDef.friction = friction;
            // Box2D body
            bodyComponent.body = Engine.getCurrentLevel().getWorld().createBody(bodyDef);
            bodyComponent.body.setUserData(new CollisionStatus(bodyComponent.body, null, 0));
            bodyComponent.body.createFixture(fixDef);
            //deallocate unnecessary  elements
            shape.dispose();

            return bodyComponent;
        }

        public static PathComponent PathFinder() {
            PooledEngine ashleyEngine = Engine.getCurrentLevel().getAshleyEngine();
            //
            PathComponent pathComp = ashleyEngine.createComponent(PathComponent.class);
            return pathComp;
        }

        public static TextureAtlasComponent Texture(String spriteAtlasPath) {
            PooledEngine ashleyEngine = Engine.getCurrentLevel().getAshleyEngine();
            //
            TextureAtlasComponent texComp = ashleyEngine.createComponent(TextureAtlasComponent.class);
            texComp.textureAtlas = Engine.getCurrentLevel().getAssetManager().getTextureAtlas(spriteAtlasPath);
            return texComp;
        }

        public static AnimationComponent animation(Animation anim) {
            PooledEngine ashleyEngine = Engine.getCurrentLevel().getAshleyEngine();
            //
            AnimationComponent animComp = ashleyEngine.createComponent(AnimationComponent.class);
            animComp.animation = anim;
            return animComp;
        }

        public static SoundComponent Sound(String path, boolean is3D, boolean looping) {
            PooledEngine ashleyEngine = Engine.getCurrentLevel().getAshleyEngine();
            //
            SoundComponent soundComp = ashleyEngine.createComponent(SoundComponent.class);
            soundComp.sound = Engine.getCurrentLevel().getAssetManager().getSound(path);
            soundComp.is3D = is3D;
            soundComp.looping = looping;
            return soundComp;
        }
    }

    public static class AshleyEntity {

        //<---todo: is checkpoint an asset?
        public static Entity CheckPoint(String name, float x, float y) {
            PooledEngine ashleyEngine = Engine.getCurrentLevel().getAshleyEngine();
            //
            Entity entity = ashleyEngine.createEntity();
            //
            entity.add(AshleyComponent.Name(name));
            //
            entity.add(AshleyComponent.Position(x, y));
            //
            return entity;
        }
    }
}
