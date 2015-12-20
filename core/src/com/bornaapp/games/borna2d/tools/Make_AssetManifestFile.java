package com.bornaapp.games.borna2d.tools;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.bornaapp.games.borna2d.asset.AssetManifest;

import java.io.File;
import java.nio.file.Paths;

/**
 * Created by Mehdi on 08/25/2015.
 * When run as a java application, this class will scan the assets folder and
 * lists all files into an assetmanifest.json file that matches the format of
 * our AssetManifest class and used when loading assets.
 * <p/>
 * If a specific assetmanifest.json for each level is required, keep only the
 * specific level assets in path and repeat this procedure for each level.
 */

public class Make_AssetManifestFile {

    public static void main(String[] arg) {

        AssetManifest manifest = new AssetManifest();

        String relativePath = Paths.get(".").toAbsolutePath().normalize().toString() + "/android/assets/",
                texturePath = "textures/",
                imagePath = "images/",
                levelPath = "levels/",
                particlePath = "particles/",
                soundPath = "sounds/";

        File textureDir = new File(relativePath + texturePath),
                imageDir = new File(relativePath + imagePath),
                levelDir = new File(relativePath + levelPath),
                particleDir = new File(relativePath + particlePath),
                soundDir = new File(relativePath + soundPath);

        for (String f : textureDir.list()) {
            if (f.toLowerCase().endsWith(".atlas".toLowerCase()))
                manifest.textures.add(texturePath + f);
        }
        for (String f : imageDir.list()) {
            if (f.toLowerCase().endsWith(".png".toLowerCase()))
                manifest.images.add(imagePath + f);
        }
        for (String f : levelDir.list()) {
            if (f.toLowerCase().endsWith(".tmx".toLowerCase()))
                manifest.levels.add(levelPath + f);
        }
        for (String f : particleDir.list()) {
            if (f.toLowerCase().endsWith(".prc".toLowerCase()))
                manifest.particles.add(particlePath + f);
        }
        for (String f : soundDir.list()) {
            if (f.toLowerCase().endsWith(".mp3") || f.toLowerCase().endsWith(".wav".toLowerCase()))
                manifest.sounds.add(soundPath + f);
        }
        // makes manifest file & writes data to it.
        Json json = new Json();
        json.setUsePrototypes(false);
        json.setOutputType(JsonWriter.OutputType.json);
        FileHandle file = new FileHandle(relativePath + "assetManifest.json");
        file.writeString(json.prettyPrint(manifest), false);
        //confirmation message
        System.out.println(file + " created");
    }
}
