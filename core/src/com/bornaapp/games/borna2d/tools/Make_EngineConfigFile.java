package com.bornaapp.games.borna2d.tools;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.bornaapp.games.borna2d.game.EngineConfig;

import java.io.File;
import java.nio.file.Paths;

/**
 * Created by Mehdi on 08/25/2015.
 * When run as a java application, this class will make a JSON file of
 * EngineConfig with default values as engconf.json in root of project.
 * To alter configurations of engine, one has to modify that file.
 */
public class Make_EngineConfigFile {

    public static void main(String[] arg) {
        //default configuration
        EngineConfig engConf = new EngineConfig();
        engConf.logLevel = 0;
        // makes config file & writes data to it.
        // it's user's responsibility to copy this file to asset folder.
        Json json = new Json();
        json.setUsePrototypes(false);
        json.setOutputType(JsonWriter.OutputType.json);
        FileHandle file = new FileHandle(Paths.get(".").toAbsolutePath().normalize().toString() + "/" + "engconf.json");
        file.writeString(json.prettyPrint(engConf), false);
        //confirmation message
        System.out.println(file + " : created");
    }
}