package com.archer.circle_run.game_logic.path_logic;

import android.content.Context;
import android.graphics.PointF;

import com.archer.circle_run.Helper;
import com.archer.circle_run.MainActivity;

import java.lang.ref.WeakReference;


public class Ellipse extends AllPaths
{
    MainActivity ct;
    float radius ;
    float radius_2;
    PointF mCenter = new PointF();
    public  Ellipse(WeakReference<MainActivity> _ct)
    {
        max_velocity = .08f;
        min_velocity = .01f;
        dir = 1;
        ct = _ct.get();
        radius  = Helper.SCREEN_WIDTH/2 - Helper.ConvertToPx(ct,18);
        radius_2  = Helper.SCREEN_WIDTH/2 - Helper.ConvertToPx(ct,100);

        mCenter.x = Helper.SCREEN_WIDTH/2;
        mCenter.y = Helper.SCREEN_HEIGHT/2 + Helper.ConvertToPx(ct,20);
    }

    public PointF calcNextPoint(float theta) {
        int t = (int)(theta*1000);
        return
                new PointF(
                         (radius * ct.objMathHelper.Cos[t]) + mCenter.x,
                         (radius_2 * ct.objMathHelper.Sin[t]) + mCenter.y
                );

    }
}