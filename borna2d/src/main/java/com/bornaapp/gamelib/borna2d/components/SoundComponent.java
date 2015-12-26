package com.bornaapp.gamelib.borna2d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.audio.Sound;
import com.bornaapp.gamelib.borna2d.game.Engine;

/**
 * Created by Mehdi on 9/2/2015.
 * ...
 */
public class SoundComponent extends Component {
    public Sound sound;
    public long soundID = -1;

    public float volumeDegradationDistance = 1.0f;
    public float pan = 0.0f;
    public float pitch = 1.0f;
    public float volume = 1.0f;
    public boolean is3D = true;
    public boolean looping = true;

    public enum PLAYBACK{
        PLAY,
        PAUSE,
        STOP
    }
    public PLAYBACK playBackState = PLAYBACK.PAUSE;
}
