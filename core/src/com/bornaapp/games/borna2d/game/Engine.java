package com.bornaapp.games.borna2d.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.bornaapp.games.borna2d.Def;

public class Engine {

    //region singleton structure & constructor
    /**
     *
     */
    private static Engine engInstance = null;

    /**
     *
     */
    private Engine() {
        engInstance = this;
    }

    public static void start() {
        //privately constructs engine
        new Engine();
        //Loads Engine configurations
        LoadConfigFromFile();
        LoadDefFromFile();
        //todo: what if engineConfig is null? (Gdx.app.exit() ????)
    }
    //endregion

    //region Engine configurations
    private EngineConfig engineConfig = new EngineConfig();

    /**
     * Loads engine configurations from application working directory(asset dir)
     *
     * @return engine configuration data from file. If fails, returns null
     */
    private static void LoadConfigFromFile() {
        engInstance.engineConfig = null;
        try {
            FileHandle file = Gdx.files.internal("engconf.json");
            Json json = new Json();
            engInstance.engineConfig = json.fromJson(EngineConfig.class, file);
        } catch (Exception e) {
            Engine.log.error(e.getMessage());
        }
    }

    private static void LoadDefFromFile() {
        try {
            Json json = new Json();
            FileHandle file = Gdx.files.internal("def.json");
            Def.set(json.fromJson(Def.class, file));
        } catch (Exception e) {
            Engine.log.error(e.getMessage());
        }
    }

    public static EngineConfig getConfig() {
        return engInstance.engineConfig;
    }
    //endregion

    //region logger
    public static class log {

        private enum LogLevel {
            LOG_NONE, // Application.LOG_NONE=0. mutes all logging.
            LOG_ERROR,// Application.LOG_ERROR=1. logs only error messages.
            LOG_INFO, // Application.LOG_INFO=2. logs error & info messages(non debug messages).
            LOG_DEBUG // Application.LOG_DEBUG=3. logs all messages.
        }

        private static final char BACKSLASH = (char) 27;
        // ANSI scape codes
        private static final String ANSI_RESET = "[0m";
        private static final String ANSI_BLACK = "0";
        private static final String ANSI_RED = "1";
        private static final String ANSI_GREEN = "2";
        private static final String ANSI_YELLOW = "3";
        private static final String ANSI_BLUE = "4";
        private static final String ANSI_PURPLE = "5";
        private static final String ANSI_CYAN = "6";
        private static final String ANSI_WHITE = "7";

        private static void text(String textToPrint, String foreColor) {
            String ansiFormat = "[3" + foreColor + "m";
            System.out.println(BACKSLASH + ansiFormat + textToPrint + BACKSLASH + ANSI_RESET);
        }

        private static void text(String textToPrint, String foreColor, String backColor) {
            String ansiFormat = "[3" + foreColor + ";4" + backColor + "m";
            System.out.println(BACKSLASH + ansiFormat + textToPrint + BACKSLASH + ANSI_RESET);
        }

        private static LogLevel getEngineLogLevel() {
            try {
                switch (engInstance.engineConfig.logLevel) {
                    case 1:
                        return LogLevel.LOG_ERROR;
                    case 2:
                        return LogLevel.LOG_INFO;
                    case 3:
                        return LogLevel.LOG_DEBUG;
                    case 0:
                    default:
                        return LogLevel.LOG_NONE;
                }
            } catch (Exception e) {
                return LogLevel.LOG_NONE;
            }
        }

        private static String getCurrentLevelName() {
            try {
                return Engine.getCurrentLevel().getName();
            } catch (Exception e) {
                return "UnknownLevel";
            }
        }

        private static String getCurrentMethodName() {
            try {
                StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
                StackTraceElement e = stacktrace[3];
                //extract class name
                String[] classNameArray = e.getClassName().split("\\.", 0);
                String className = classNameArray[classNameArray.length - 1];
                //extract method name
                String methodName = e.getMethodName();

                return className + "." + methodName + "()";
            } catch (Exception e) {
                return "UnknownMethod";
            }
        }

        public static void error(String message) {
            if (getEngineLogLevel() == LogLevel.LOG_NONE)
                return;
            text(getCurrentLevelName() + "; " + getCurrentMethodName() + ": " + message, ANSI_RED);
        }

        public static void info(String message) {
            if (getEngineLogLevel() == LogLevel.LOG_ERROR || getEngineLogLevel() == LogLevel.LOG_NONE)
                return;
            text(getCurrentLevelName() + "; " + getCurrentMethodName() + ": " + message, ANSI_BLUE);
        }

        public static void debug(String message) {
            if (getEngineLogLevel() == LogLevel.LOG_ERROR || getEngineLogLevel() == LogLevel.LOG_INFO || getEngineLogLevel() == LogLevel.LOG_NONE)
                return;
            text(getCurrentLevelName() + "; " + getCurrentMethodName() + ": " + message, ANSI_BLACK);
        }
    }
    //endregion

    //region level manager
    /**
     *
     */
    private Array<LevelBase> levels = new Array<LevelBase>();
    private LevelBase currentLevel = null;
    private LevelBase previousLevel = null;

    /**
     * @param nextLevelName     short class name of next level
     */
    public static void setLevel(String nextLevelName) {
        //validate next level
        LevelBase next = getLevel(nextLevelName);
        if (next == null) {
            Engine.log.error(nextLevelName + " ==null");
            return;
        }
        //edit current level if valid
        LevelBase current = Engine.getCurrentLevel();
        if (current == null)
            Engine.log.error("current == null");
        else {
            current.inResponseToEngine_pause();
            current.inResponseToEngine_render(0f);//Updates Ashley systems to "pause" state!
        }
        engInstance.previousLevel = current;
        //show next level
        engInstance.currentLevel = next;
        engInstance.currentLevel.inResponseToEngine_create();
        engInstance.currentLevel.inResponseToEngine_resume();
        engInstance.currentLevel.inResponseToEngine_resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());//todo: Engine.width
    }

    /**
     * @param _name
     * @return
     */
    public static LevelBase getLevel(String _name) {
        for (int i = 0; i < engInstance.levels.size; i++) {
            LevelBase l = engInstance.levels.get(i);
            if (l.getName().equals(_name))
                return l;
        }
        Engine.log.error("@return == null");
        return null;
    }

    /**
     * @return
     */
    public static LevelBase getCurrentLevel() {
        return engInstance.currentLevel;
    }

    public static LevelBase getPreviousLevel() {
        return engInstance.previousLevel;
    }

    /**
     * @param _level
     */
    public static void addLevel(LevelBase _level) {
        //Prevent addition of the same level
        for (int i = 0; i < engInstance.levels.size; i++) {
            if (engInstance.levels.get(i).equals(_level))
                return;
        }
        //Add level to list
        engInstance.levels.add(_level);
    }
    //endregion

    //region Handling application requests

    public static void dispose() {
        //Dispose all levels
        for (int i = 0; i < engInstance.levels.size; i++) {
            LevelBase l = engInstance.levels.get(i);
            if (l != null) {
                l.inResponseToEngine_dispose();
            }
        }
        //collect all garbage memory
        System.gc();
    }

    public static void resize(int width, int height) {
        try {
            engInstance.currentLevel.inResponseToEngine_resize(width, height);
        } catch (Exception e) {
            Engine.log.error(e.getMessage());
        }
    }

    public static void render() {
        try {
            engInstance.currentLevel.inResponseToEngine_render(Gdx.graphics.getDeltaTime());
        } catch (Exception e) {
            Engine.log.error(e.getMessage());
        }
    }

    public static void pause() {
        try {
            engInstance.currentLevel.inResponseToEngine_pause();
        } catch (Exception e) {
            Engine.log.error(e.getMessage());
        }
    }

    public static void resume() {
        try {
            engInstance.currentLevel.inResponseToEngine_resume();
        } catch (Exception e) {
            Engine.log.error(e.getMessage());
        }
    }
    //endregion

    //region Utilities
    public static float getJavaHeap() {
        return (Gdx.app.getJavaHeap() / 1048576);
    }

    public static float getNativeHeap() {
        return (Gdx.app.getNativeHeap() / 1048576);
    }

    public static int screenWidth_InPixels() {
        return Gdx.graphics.getWidth();
    }

    public static int screenHeight_InPixels() {
        return Gdx.graphics.getHeight();
    }

    public static int frameRate() {
        int frameRate = Gdx.graphics.getFramesPerSecond();
        return (frameRate == 0 ? 60 : frameRate);
    }
    //endregion
}
