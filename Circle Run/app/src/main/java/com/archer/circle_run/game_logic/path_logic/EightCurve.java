package com.archer.circle_run.game_logic.path_logic;

import android.content.Context;
import android.graphics.PointF;

import com.archer.circle_run.Helper;
import com.archer.circle_run.MainActivity;

import java.lang.ref.WeakReference;

/**
 * Created by Swastik on 27-01-2016.
 */
public class EightCurve extends AllPaths
{
    MainActivity ct;
    float radius ;
    PointF mCenter = new PointF();

    public  EightCurve(WeakReference<MainActivity> _ct)
    {
        max_velocity = .054f;
        min_velocity = .01f;
        dir = 1;
        ct = _ct.get();
        radius  = Helper.SCREEN_WIDTH/2 - Helper.ConvertToPx(ct,10);

        mCenter.x = Helper.SCREEN_WIDTH/2;
        mCenter.y = Helper.SCREEN_HEIGHT/2 + Helper.ConvertToPx(ct,20);
    }

    public PointF calcNextPoint(float theta) {
        int t = (int)(theta*1000);
        float temp = (radius * ct.objMathHelper.Sin[t]);
        return
                new PointF(
                        temp + mCenter.x,
                         (temp * ct.objMathHelper.Cos[t]) + mCenter.y
                );

    }
}