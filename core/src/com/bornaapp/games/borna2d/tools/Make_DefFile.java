package com.bornaapp.games.borna2d.tools;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.bornaapp.games.borna2d.Def;

import java.nio.file.Paths;

/**
 * Created by Mehdi on 8/26/2015.
 * When run as a java application, this class will make a JSON file of
 * global definitions with default values as def.json in root of project.
 * To alter global definitions, one has to modify that file.
 */
public class Make_DefFile {
    public static void main(String[] arg) {
        //default definitions
        Def.get().gravity = new Vector2(0f, 0f);
        Def.get().collisionLayerName = "bornaapp_collision";
        Def.get().portalLayerName = "bornaapp_portals";
        Def.get().lightsLayerName = "bornaapp_lights";
        Def.get().particlesLayerName = "bornaapp_particles";
        Def.get().checkpointsLayerName = "bornaapp_checkpoints";
        Def.get().pathLayerName = "bornaapp_path";
        Def.get().propertyKey_Color = "color";
        Def.get().propertyKey_Distance = "distance";
        // makes def file & writes data to it.
        // it's user's responsibility to copy this file to asset directory.
        Json json = new Json();
        json.setUsePrototypes(false);
        json.setOutputType(JsonWriter.OutputType.json);
        FileHandle file = new FileHandle(Paths.get(".").toAbsolutePath().normalize().toString() + "/" + "def.json");
        file.writeString(json.prettyPrint( Def.get()) , false);
        //confirmation message
        System.out.println(file + " : created");
    }
}
