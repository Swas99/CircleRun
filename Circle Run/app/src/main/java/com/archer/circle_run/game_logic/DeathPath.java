package com.archer.circle_run.game_logic;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.archer.circle_run.Helper;
import com.archer.circle_run.R;
import com.archer.circle_run.game_logic.path_logic.AllPaths;

/**
 * Created by Swastik on 26-01-2016.
 */
public class DeathPath extends View
{

    Context ct;
    public DeathPath(Context context) {
        super(context);
        this.ct=context;
    }

    public DeathPath(final Context ct, final AttributeSet attrs) {
        super(ct, attrs);
        this.ct=ct;
    }

    public DeathPath(final Context ct, final AttributeSet attrs, final int defStyle) {
        super(ct, attrs, defStyle);
        this.ct=ct;
    }


    Paint paint = new Paint();
    Path path = new Path();


    public void drawPath(AllPaths objPath)
    {
        path.rewind();

        float increment = .01f;
        float lim = (float)(2*Math.PI + .01);
        PointF nextPoint = objPath.calcNextPoint((float)(2*Math.PI));
        for (float i = 0; i <= lim; i+=increment) {
            path.moveTo(nextPoint.x, nextPoint.y);
            nextPoint = objPath.calcNextPoint(i);
            path.lineTo(nextPoint.x, nextPoint.y);
        }
        path.close();

        paint.setStrokeWidth(Helper.ConvertToPx(ct, 5));
        paint.setPathEffect(null);
        paint.setColor(Color.argb(0xEA, 0xFF, 0xFF, 0xFF));
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }


    public void onDraw(Canvas c){
        c.drawARGB(0, 0, 0, 0);
        c.drawPath(path, paint);

    }


    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

}
