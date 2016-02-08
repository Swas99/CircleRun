package com.archer.circle_run.game_logic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import com.archer.circle_run.Helper;

/**
 * Created by Swastik on 27-01-2016.
 */
public class CirclePath extends View
{

    Context ct;
    public CirclePath(Context context) {
        super(context);
        this.ct=context;
        init();
    }

    public CirclePath(final Context ct, final AttributeSet attrs) {
        super(ct, attrs);
        this.ct=ct;
        init();
    }

    public CirclePath(final Context ct, final AttributeSet attrs, final int defStyle) {
        super(ct, attrs, defStyle);
        this.ct=ct;
        init();
    }


    Paint paint = new Paint();
    Path path = new Path();


    public void init()
    {
        float increment = .01f;
        float lim = (float)(2*Math.PI + 4*increment);
        Circle objCircle = new Circle();
        PointF nextCirclePoint = objCircle.calcNextPoint((float)(2*Math.PI));
        for (float i = 0; i <= lim; i+=increment) {
            path.moveTo(nextCirclePoint.x, nextCirclePoint.y);
            nextCirclePoint = objCircle.calcNextPoint(i);
            path.lineTo(nextCirclePoint.x, nextCirclePoint.y);
        }
        path.close();
    }


    public void onDraw(Canvas c){
        c.drawARGB(0,0,0,0);

        paint.setStrokeWidth(Helper.ConvertToPx(ct, 5));
        paint.setPathEffect(null);
        paint.setColor(Color.argb(0xEA, 0xFF, 0xFF, 0xFF));
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        c.drawPath(path, paint);

    }


    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    public class Circle
    {
        float radius ;
        PointF mCenter = new PointF();
        public  Circle()
        {
            radius  = Helper.SCREEN_WIDTH/2 - Helper.ConvertToPx(ct,60);

            mCenter.x = Helper.SCREEN_WIDTH/2;
            mCenter.y = Helper.SCREEN_HEIGHT/2 + Helper.ConvertToPx(ct,20);
        }

        public PointF calcNextPoint(float theta) {
            return
                    new PointF(
                            (float)(radius * Math.sin(theta)) + mCenter.x,
                            (float) (radius * Math.cos(theta)) + mCenter.y
                    );

        }
    }
}
