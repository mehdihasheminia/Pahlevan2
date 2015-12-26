package com.bornaapp.gamelib.borna2d;

public class UnitConverter {
    private static int ppm = 32; //pixel per meter //<----todo config?

    public static float toMeters(int pixels){
        return pixels/ppm;
    }

    public static float toMeters(float pixels){
        return pixels/ppm;
    }

    public static int toPixels(float meters){
        return (int)(meters*ppm);
    }
}
