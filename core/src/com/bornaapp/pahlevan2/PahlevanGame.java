package com.bornaapp.pahlevan2;

import com.bornaapp.games.borna2d.game.Engine;
import com.bornaapp.games.borna2d.game.AppListener;
import com.bornaapp.pahlevan2.Levels.L21;
import com.bornaapp.pahlevan2.Levels.L211;
import com.bornaapp.pahlevan2.Levels.L212;
import com.bornaapp.pahlevan2.Menus.EntryMenu;
import com.bornaapp.pahlevan2.Menus.PauseMenu;

public class PahlevanGame extends AppListener {

    public PahlevanGame(){
        super();
        //nothing in constructor!!! as Gdx is not initialized yet.
    }

    @Override
    protected void addLevels() {
        Engine.addLevel(new EntryMenu());
        Engine.addLevel(new PauseMenu());
        Engine.addLevel(new L21());
        Engine.addLevel(new L211());
        Engine.addLevel(new L212());
    }

    @Override
    protected void setLevel() {
        Engine.setLevel(EntryMenu.class.getSimpleName());
    }
}
