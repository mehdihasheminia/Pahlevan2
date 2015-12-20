package com.bornaapp.pahlevan2.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        int fps = 60;
        config.title = "PahlevanGame";
        config.width = 800;
        config.height = 480;
        config.foregroundFPS = fps;
        config.backgroundFPS = fps;
        config.vSyncEnabled = false;
		new LwjglApplication(new com.bornaapp.pahlevan2.PahlevanGame(), config);
	}
}
