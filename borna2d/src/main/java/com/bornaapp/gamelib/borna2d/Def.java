package com.bornaapp.gamelib.borna2d;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Mehdi on 8/26/2015.
 * ...
 */
//<----todo: why not in the engine? engine is a singleton itself!
public class Def  {
    private Def(){}
    private static Def defInstance = new Def( );
    public static Def get(){return defInstance;}
    public static void set(Def d){
        defInstance = d;
    }

    public Vector2 gravity = new Vector2();
    public String collisionLayerName;//each object: property x & y & width & height(Float)
    public String portalLayerName;//each object: property x & y & width & height(Float)
    public String lightsLayerName; //each object: property x & y(Float), custom property"distance"(String) & "color"(String[4])
    public String particlesLayerName; //each object: property x & y(Float)
    public String checkpointsLayerName; //each object: custom property"name"(String), property x & y(Float)
    public String pathLayerName;
    public String propertyKey_Color;
    public String propertyKey_Distance;
}
