package com.archer.circle_run.game_logic.path_logic;

/**
 * Created by Swastik on 27-01-2016.
 */
public class MathHelper {

    float[] Sin;
    float[] Cos;
    int MaxLim;


    public void compute_values()
    {
        float increment = 0.001f;
        float lim = 6.3f; //2*PI
        MaxLim = (int)(lim*1000)-1;
        int index;


        Sin = new float[(int)(lim*1000)];
        Cos = new float[(int)(lim*1000)];
        for (float i = 0; i<lim;i+=increment)
        {
            index = (int)(i*1000);
            Sin[index] = (float)Math.sin(i);
            Cos[index] = (float)Math.cos(i);
        }
    }
}
