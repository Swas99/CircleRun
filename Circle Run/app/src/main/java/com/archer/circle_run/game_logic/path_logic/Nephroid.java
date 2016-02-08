package com.archer.circle_run.game_logic.path_logic;

import android.content.Context;
import android.graphics.PointF;

import com.archer.circle_run.Helper;
import com.archer.circle_run.MainActivity;

import java.lang.ref.WeakReference;

public class Nephroid extends AllPaths
{
    MainActivity ct;
    float radius ;
    PointF mCenter = new PointF();
    public  Nephroid(WeakReference<MainActivity> _ct)
    {
        dir = 1;
        ct = _ct.get();
        radius  = Helper.SCREEN_WIDTH/2 - Helper.ConvertToPx(ct,122);

        mCenter.x = Helper.SCREEN_WIDTH/2;
        mCenter.y = Helper.SCREEN_HEIGHT/2 + Helper.ConvertToPx(ct,20);
    }

    public PointF calcNextPoint(float theta) {
        int t = (int)(theta*1000);

        int t_2 = (int)(3*theta*1000);
        int PI = (int)(2*Math.PI*1000);
        if(t_2 > ct.objMathHelper.MaxLim)
            t_2%=PI+1;

        return
                new PointF(
                         (radius * ( 3*ct.objMathHelper.Cos[t] - ct.objMathHelper.Cos[t_2]) / 2 ) + mCenter.x,
                         (4*radius * ct.objMathHelper.Sin[t]*ct.objMathHelper.Sin[t]*ct.objMathHelper.Sin[t]) + mCenter.y
                );

    }

}