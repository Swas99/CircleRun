package com.archer.circle_run.game_logic.path_logic;


import android.content.Context;
import android.graphics.PointF;

import com.archer.circle_run.Helper;
import com.archer.circle_run.MainActivity;

import java.lang.ref.WeakReference;

public class Astroid extends AllPaths
{
    MainActivity ct;
    float radius ;
    PointF mCenter = new PointF();
    public  Astroid(WeakReference<MainActivity> _ct)
    {
        max_velocity = .04f;
        min_velocity = .01f;
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
                         (radius * ct.objMathHelper.Cos[t]*ct.objMathHelper.Cos[t]*ct.objMathHelper.Cos[t])/1.2f + mCenter.x,
                         (radius * ct.objMathHelper.Sin[t]*ct.objMathHelper.Sin[t]*ct.objMathHelper.Sin[t])/1.2f + mCenter.y
                );

    }
}