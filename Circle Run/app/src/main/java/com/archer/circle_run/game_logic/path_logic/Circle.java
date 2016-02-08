package com.archer.circle_run.game_logic.path_logic;


import android.content.Context;
import android.graphics.PointF;

import com.archer.circle_run.Helper;
import com.archer.circle_run.MainActivity;

public class Circle extends AllPaths
{
    MainActivity ct;
    float radius ;
    PointF mCenter = new PointF();
    public  Circle(MainActivity _ct)
    {
        ct = _ct;
        radius  = Helper.SCREEN_WIDTH/2 - Helper.ConvertToPx(ct,60);

        mCenter.x = Helper.SCREEN_WIDTH/2;
        mCenter.y = Helper.SCREEN_HEIGHT/2 + Helper.ConvertToPx(ct,20);
    }

    public PointF calcNextPoint(float theta) {
        int t = (int)(theta*1000);
        return
                new PointF(
                        (radius * ct.objMathHelper.Sin[t]) + mCenter.x,
                         (radius *ct.objMathHelper.Cos[t]) + mCenter.y
                );

    }
}