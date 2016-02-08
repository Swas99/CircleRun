package com.archer.circle_run.game_logic.path_logic;

import android.graphics.PointF;

/**
 * Created by Swastik on 27-01-2016.
 */
public abstract class AllPaths {
    public int dir = 1;
    public float max_velocity = 0.05f;
    public float min_velocity = max_velocity;

    public abstract PointF calcNextPoint(float theta);
}
