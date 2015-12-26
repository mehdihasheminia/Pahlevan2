package com.bornaapp.gamelib.borna2d.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Json;
import com.bornaapp.gamelib.borna2d.game.Engine;

/**
 * Created by Mehdi on 08/26/2015.
 * ...
 */
public class Assets {

    private AssetManifest manifest;
    private final AssetManager assetManager = new AssetManager();

    //region Loading methods

    /**
     * Loads external assets from "assetManifest.json" all at once
     *
     * @param manifestPath path to asset manifest file
     */
    public void loadAll(String manifestPath) {
        //checks if manifest is already loaded
        if (manifest != null)
            return;
        //checks if manifest path is valid
        if (!manifestPath.toLowerCase().endsWith(".json".toLowerCase()))
            return;
        //load manifest list
        try {
            Json json = new Json();
            FileHandle file = Gdx.files.internal(manifestPath);
            manifest = json.fromJson(AssetManifest.class, file);
        } catch (Exception e) {
            Engine.log.error("Loading Manifest from JSON failed:" + e.getMessage());
            manifest = null;
            return;
        }
        //registering listed assets in AssetManager's loading queue.
        for (String s : manifest.textures)
            assetManager.load(s, TextureAtlas.class);

        for (String s : manifest.images)
            assetManager.load(s, Texture.class);

        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        for (String s : manifest.levels)
            assetManager.load(s, TiledMap.class);

        for (String s : manifest.particles)
            assetManager.load(s, ParticleEffect.class);

        for (String s : manifest.sounds)
            assetManager.load(s, Sound.class);

        //AssetManager begins loading all listed assets...
        try {
            while (!assetManager.update()) {
                assetManager.getProgress();
            }
        } catch (Exception e) {
            Engine.log.error("Some assets failed to load: " + e.getMessage());
        }
    }

    /**
     * Loads external assets from "assetManifest.json" progressively
     *
     * @param manifestPath path to asset manifest file
     * @return true if loading is in progress & false if finished or failed
     */
    public boolean loadByStep(String manifestPath) {
        //checks if manifest is already loaded
        if (manifest == null) {
            //checks if manifest path is valid
            if (!manifestPath.toLowerCase().endsWith(".json".toLowerCase()))
                return false;
            //load manifest list
            try {
                Json json = new Json();
                FileHandle file = Gdx.files.internal(manifestPath);
                manifest = json.fromJson(AssetManifest.class, file);
            } catch (Exception e) {
                Engine.log.error("Loading Manifest from JSON failed:" + e.getMessage());
                manifest = null;
                return false;
            }
            //registering listed assets in AssetManager's loading queue.
            for (String s : manifest.textures)
                assetManager.load(s, TextureAtlas.class);

            for (String s : manifest.images)
                assetManager.load(s, Texture.class);

            assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
            for (String s : manifest.levels)
                assetManager.load(s, TiledMap.class);

            for (String s : manifest.particles)
                assetManager.load(s, ParticleEffect.class);

            for (String s : manifest.sounds)
                assetManager.load(s, Sound.class);
        }
        //AssetManager begins loading one Asset on each call...
        try {
            return !assetManager.update();
        } catch (Exception e) {
            Engine.log.error("Some assets failed to load: " + e.getMessage());
            return false;
        }
    }
    //endregion

    //region get Assets
    public TextureAtlas getTextureAtlas(String name) {
        TextureAtlas textureAtlas = null;
        name = "textures/" + name;

        if (assetManager.isLoaded(name))
            textureAtlas = assetManager.get(name);

        if (textureAtlas == null)
            Engine.log.error("Texture Atlas not found: " + name);

        return textureAtlas;
    }

    public Texture getImage(String name) {
        Texture img = null;
        name = "images/" + name;

        if (assetManager.isLoaded(name))
            img = assetManager.get(name);

        if (img == null)
            Engine.log.error("Texture image not found: " + name);

        return img;
    }

    public TiledMap getTiledMap(String name) {
        TiledMap tMap = null;
        name = "levels/" + name;

        if (assetManager.isLoaded(name))
            tMap = assetManager.get(name);

        if (tMap == null)
            Engine.log.error("TiledMap not found: " + name);

        return tMap;
    }

    public Sound getSound(String name) {
        Sound sound = null;
        name = "sounds/" + name;

        if (assetManager.isLoaded(name))//todo: sensitive to caps/lower case letters!
            sound = assetManager.get(name);

        if (sound == null)
            Engine.log.error("sound not found: " + name);

        return sound;
    }

    public ParticleEffect getParticleEffect(String name) {
        ParticleEffect particleEffect = null;
        name = "particles/" + name;

        if (assetManager.isLoaded(name))//todo: sensitive to caps/lower case letters!
            particleEffect = assetManager.get(name);

        if (particleEffect == null)
            Engine.log.error("particleEffect not found: " + name);

        return particleEffect;
    }
    //endregion

    //region Utilities
    public float getProgress(){
        return assetManager.getProgress();
    }
    //endregion

    public void dispose() {
        assetManager.dispose();
    }
}
