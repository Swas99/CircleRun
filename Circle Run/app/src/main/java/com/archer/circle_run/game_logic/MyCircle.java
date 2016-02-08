package com.archer.circle_run.game_logic;

import android.content.Context;
import android.graphics.PointF;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.archer.circle_run.Helper;

import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Swastik on 27-01-2016.
 */
public class MyCircle extends View {
    Context ct;
    SinglePlayerGame objSinglePlayerGame;

    public MyCircle(Context context) {
        super(context);
        ct = context;
    }

    public MyCircle(final Context ct, final AttributeSet attrs) {
        super(ct, attrs);
        this.ct = ct;
    }

    public MyCircle(final Context ct, final AttributeSet attrs, final int defStyle) {
        super(ct, attrs, defStyle);
        this.ct = ct;
    }

    float radius;
    float def_radius;
    float min_radius;
    float centerX;
    float centerY;
    float base_velocity;
    float max_velocity;
    float theta_velocity;
    float theta_position;
    float start_position;
    int dir;
    Circle objCircle;
    public void init(WeakReference<SinglePlayerGame> _single_player_game)
    {
        def_radius = Helper.ConvertToPx(ct, 22);
        min_radius = def_radius/10;
        objCircle = new Circle();
        objSinglePlayerGame = _single_player_game.get();

        reset();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                move();
            }
        },100);
    }

    public void move()
    {
        float theta = theta_position+start_position;
        if(theta_position>2*Math.PI)
            theta-=2*Math.PI;
        PointF nextCirclePoint = objCircle.calcNextPoint(theta);
        setX(nextCirclePoint.x - def_radius);
        setY(nextCirclePoint.y - def_radius);
        theta_position+=theta_velocity;

        if(theta_position>2*Math.PI)
        {
            theta_position=0;
            objSinglePlayerGame.increment_score();
            objSinglePlayerGame.mContext.objSoundManager
                    .Play(objSinglePlayerGame.mContext.objSoundManager.PLING);
        }
    }


    public void recompute_position()
    {
        centerX = getX()+radius;
        centerY = getY()+radius;
    }

    public void shrink()
    {
        float scale = getScaleX();
        if(scale>.3)
        {
            radius/=1.1;
            if(radius<min_radius)
            {
                radius=min_radius;
            }
            else
            {
                setScaleX(scale/1.1f);
                setScaleY(scale/1.1f);
            }
        }
        recompute_position();
    }

    public void expand()
    {
        float scale = getScaleX();
        if(scale<1)
        {
            radius*=1.1;
            if(radius>def_radius)
            {
                radius=def_radius;
                setScaleX(1);
                setScaleY(1);
            }
            else
            {
                setScaleX(scale*1.1f);
                setScaleY(scale*1.1f);
            }
        }

        recompute_position();
    }

    public void increase_velocity() {
        theta_velocity*=1.1;
        if(theta_velocity>max_velocity)
            theta_velocity=max_velocity;

        shrink();
    }
    public void decrease_velocity() {
        theta_velocity/=1.1;
        if(theta_velocity<base_velocity)
            theta_velocity=base_velocity;

        expand();
    }

    public void reset()
    {
        radius = def_radius;
        base_velocity = .012f;
        theta_velocity = base_velocity;
        max_velocity = .17f;
        start_position = (float)Math.PI;
        theta_position =0;
        dir=1;

        setScaleX(.4f);
        setScaleY(.4f);
    }

    class Circle
    {
        float radius ;
        PointF mCenter = new PointF();
        public  Circle()
        {
            radius  = Helper.SCREEN_WIDTH/2 - Helper.ConvertToPx(ct,60);
            mCenter.x = Helper.SCREEN_WIDTH/2;
            mCenter.y = Helper.SCREEN_HEIGHT/2 + Helper.ConvertToPx(ct,20);
        }

        private PointF calcNextPoint(float theta) {
            return
                    new PointF(
                            (float)(radius * Math.sin(theta)) + mCenter.x,
                            (float) (radius * Math.cos(theta)) + mCenter.y
                    );

        }
    }
}
