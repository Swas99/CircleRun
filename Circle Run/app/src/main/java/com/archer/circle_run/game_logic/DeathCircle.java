package com.archer.circle_run.game_logic;

import android.content.Context;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.archer.circle_run.Helper;
import com.archer.circle_run.R;
import com.archer.circle_run.game_logic.path_logic.AllPaths;
import com.archer.circle_run.game_logic.path_logic.MathHelper;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Swastik on 27-01-2016.
 */
public class DeathCircle extends View {
    Context ct;
    AllPaths objPath;
    int direction;
    boolean activated;

    int backgrounds[] = { R.drawable.background_black_circle,R.drawable.background_red_circle,
            R.drawable.background_maroon_circle,
            R.drawable.background_yellow_circle};

    public DeathCircle(Context context, AllPaths _path, int _dir) {
        super(context);
        ct = context;
        objPath = _path;
        direction = _dir*_path.dir;
        radius = Helper.ConvertToPx(ct, 14);
        reset();

    }


    public DeathCircle(final Context ct, final AttributeSet attrs) {
        super(ct, attrs);
        this.ct = ct;
        radius = Helper.ConvertToPx(ct, 14);
        reset();
    }

    public DeathCircle(final Context ct, final AttributeSet attrs, final int defStyle) {
        super(ct, attrs, defStyle);
        this.ct = ct;
        radius = Helper.ConvertToPx(ct, 14);
        reset();
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size =  Helper.ConvertToPx(ct, 28);
        getLayoutParams().height = size;
        getLayoutParams().width = size;
        setLayoutParams(getLayoutParams());

    }

    float radius;
    float centerX;
    float centerY;
    float theta_velocity;
    float theta_position;

    public void recompute_position()
    {
        centerX = getX()+radius;
        centerY = getY()+radius;
    }

    public void reset()
    {
        Random rnd;
        if(Build.VERSION.SDK_INT >= 21 )
            rnd = ThreadLocalRandom.current();
        else
            rnd = new Random();

        setBackgroundResource(backgrounds[rnd.nextInt(backgrounds.length)]);
        theta_position = (float)rnd.nextInt((int)(2* Math.PI*1000+1))/1000f;

        theta_velocity = (float)rnd.nextInt(
                (int)(1000*objPath.max_velocity+1 - 1000*objPath.min_velocity))
                /1000f + objPath.min_velocity;

        move();
    }

    public void move()
    {
        PointF nextPoint = objPath.calcNextPoint(theta_position);
        setX(nextPoint.x - radius);
        setY(nextPoint.y - radius);

        theta_position+= (direction *theta_velocity);
        if(theta_position>2*Math.PI)
            theta_position=0;
        if(theta_position<0)
            theta_position=(float)(2*Math.PI);
    }

}
