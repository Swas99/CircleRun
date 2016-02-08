package com.archer.circle_run.game_logic.path_logic;


import android.content.Context;
import android.graphics.PointF;

import com.archer.circle_run.Helper;
import com.archer.circle_run.MainActivity;

import java.lang.ref.WeakReference;

public class Deltoid extends AllPaths
{
    MainActivity ct;
    float radius ;
    PointF mCenter = new PointF();
    public  Deltoid(WeakReference<MainActivity> _ct)
    {
        max_velocity = .06f;
        min_velocity = .011f;
        dir = 1;
        ct = _ct.get();
        radius  = Helper.SCREEN_WIDTH/2 - Helper.ConvertToPx(ct,10);

        mCenter.x = Helper.SCREEN_WIDTH/2;
        mCenter.y = Helper.SCREEN_HEIGHT/2 + Helper.ConvertToPx(ct,20);
    }

    public PointF calcNextPoint(float theta) {
        int t = (int)(theta*1000);

        return
                new PointF(
                         (radius * (( 2*ct.objMathHelper.Cos[t] * (1 - ct.objMathHelper.Cos[t]) +1) )/ 3) + mCenter.x,
                         (radius * ( 2*ct.objMathHelper.Sin[t] * (1 + ct.objMathHelper.Cos[t]) ) / 3) + mCenter.y
                );
    }
}