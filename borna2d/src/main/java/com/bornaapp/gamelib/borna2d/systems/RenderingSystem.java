package com.bornaapp.gamelib.borna2d.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bornaapp.gamelib.borna2d.components.AnimationComponent;
import com.bornaapp.gamelib.borna2d.components.BodyComponent;
import com.bornaapp.gamelib.borna2d.components.ParticleComponent;
import com.bornaapp.gamelib.borna2d.components.PositionComponent;
import com.bornaapp.gamelib.borna2d.game.Engine;
import com.bornaapp.gamelib.borna2d.components.SoundComponent;
import com.bornaapp.gamelib.borna2d.components.TextureAtlasComponent;

import java.util.Comparator;

/**
 *
 */
public class RenderingSystem extends IteratingSystem {

    private Array<Entity> renderQueue = new Array<Entity>();

    //region Component Mappers
    private ComponentMapper<PositionComponent> posMap = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<ParticleComponent> partMap = ComponentMapper.getFor(ParticleComponent.class);
    private ComponentMapper<BodyComponent> bodyMap = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<TextureAtlasComponent> texMap = ComponentMapper.getFor(TextureAtlasComponent.class);
    private ComponentMapper<AnimationComponent> animMap = ComponentMapper.getFor(AnimationComponent.class);
    private ComponentMapper<SoundComponent> soundMap = ComponentMapper.getFor(SoundComponent.class);
    //endregion

    //region constructor
    public RenderingSystem() {
        super(Family.all().get(), Engine.getCurrentLevel().getSystemPriority());
    }
    //endregion

    //region overrided methods
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        renderQueue.sort(comparator);

        for (Entity entity : renderQueue) {
            //
            renderTexture(entity, deltaTime);
            //
            renderParticle(entity, deltaTime);
        }
        renderQueue.clear();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        renderQueue.add(entity);
    }
    //endregion

    //region Render & Update methods
    private void renderParticle(Entity entity, float deltaTime) {
        if (!partMap.has(entity))
            return;
        ParticleComponent partComp = partMap.get(entity);
        partComp.particleEffect.update(deltaTime);
        partComp.particleEffect.draw(Engine.getCurrentLevel().getBatch());
        if (partComp.particleEffect.isComplete())
            partComp.particleEffect.reset();
    }

    private void renderTexture(Entity entity, float deltaTime) {
        if (!texMap.has(entity))
            return;
        TextureAtlasComponent texComp = texMap.get(entity);
        //calculating texture position & size
        float x, y, w, h;
        x = getX(entity);
        y = getY(entity);
        if (x == Float.MAX_VALUE) x = 0;
        if (y == Float.MAX_VALUE) y = 0;
        //
        Vector2 size = texComp.getSize();
        w = size.x;
        h = size.y;
        //draw texture
        SpriteBatch batch = Engine.getCurrentLevel().getBatch();
        if (animMap.has(entity)) {
            AnimationComponent animComp = animMap.get(entity);
            animComp.elapsedTime += deltaTime;
            batch.draw(animComp.animation.getKeyFrame(animComp.elapsedTime), x - w / 2, y - h / 2);
        } else {
            batch.draw(texComp.getRegion(0), x - w / 2, y - h / 2); //<----todo: offseting origin to center. check this for every other class!
        }
    }
    //endregion

    //region Sort
    private Comparator<Entity> comparator = new Comparator<Entity>() {
        @Override
        public int compare(Entity e1, Entity e2) {

            return (int) Math.signum(getY(e2) - getY(e1));
        }
    };
    //endregion

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
