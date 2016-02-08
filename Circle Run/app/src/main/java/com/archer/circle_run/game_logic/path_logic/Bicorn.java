package com.archer.circle_run.game_logic.path_logic;


import android.graphics.PointF;

import com.archer.circle_run.Helper;
import com.archer.circle_run.MainActivity;

import java.lang.ref.WeakReference;

public class Bicorn extends AllPaths
{
    MainActivity ct;
    float radius ;
    PointF mCenter = new PointF();
    public  Bicorn(WeakReference<MainActivity> _ct)
    {
        max_velocity = .045f;
        min_velocity = .005f;
        dir = -1;
        ct = _ct.get();
        radius  = Helper.SCREEN_WIDTH/2 - Helper.ConvertToPx(ct,25);

        mCenter.x = Helper.SCREEN_WIDTH/2;
        mCenter.y = Helper.SCREEN_HEIGHT/2 - radius + Helper.ConvertToPx(ct,20);
    }

    public PointF calcNextPoint(float theta) {
        int t = (int)(theta*1000);
        return
                new PointF(
                         (radius * ct.objMathHelper.Sin[t]) + mCenter.x,
                         ((radius * ct.objMathHelper.Cos[t] * ct.objMathHelper.Cos[t]
                                * (2+ct.objMathHelper.Cos[t]))
                                /
                                (3+ ct.objMathHelper.Sin[t] * ct.objMathHelper.Sin[t])) * 2 + mCenter.y
                );

    }
}