package com.archer.circle_run.game_logic;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.archer.circle_run.Helper;
import com.archer.circle_run.MainActivity;
import com.archer.circle_run.R;
import com.archer.circle_run.game_logic.path_logic.AllPaths;
import com.archer.circle_run.game_logic.path_logic.Astroid;
import com.archer.circle_run.game_logic.path_logic.Bicorn;
import com.archer.circle_run.game_logic.path_logic.Cornoid;
import com.archer.circle_run.game_logic.path_logic.Deltoid;
import com.archer.circle_run.game_logic.path_logic.EightCurve;
import com.archer.circle_run.game_logic.path_logic.Ellipse;
import com.archer.circle_run.game_logic.path_logic.EllipseEvolute;
import com.archer.circle_run.game_logic.path_logic.Lemniscate;

import java.lang.ref.WeakReference;

/**
 * Created by Swastik on 17-03-2016.
 */
public class MultiPlayerGame
{
    public MainActivity mContext;

    TextView tv_P1_Score;
    TextView tv_P2_Score;

    Handler mHandler;
    Runnable game_driver;

    public long P1_Score;
    public long P2_Score;
    int game_time;
    long FRAME_INTERVAL;
    boolean isP1_Alive;
    boolean isP2_Alive;
    boolean godMode;
    boolean increase_P1_velocity;
    boolean increase_P2_velocity;

    AllPaths obj_path;
    public int path_index;
    public MyCircle obj_P1;
    public MyCircle obj_P2;
    public DeathPath objDeathPath;
    View mainContainer;
    DeathCircle deathCircles[];
    int top;
    long ScoreToReach;


    public MultiPlayerGame(WeakReference<MainActivity> m_context, int path_index, long score_limit)
    {
        ScoreToReach = score_limit;
        this.path_index = path_index;
        mContext = m_context.get();
        mHandler = new Handler();
        deathCircles = new DeathCircle[100];
        top=0;

        init_game();
        initializePath(path_index);
        initialize_screen_controls();

        mContext.CollectGarbage();
    }

    private void initializePath(int path_index) {
        WeakReference<MainActivity> m_context = new WeakReference<>(mContext);
        switch (path_index)
        {
            case Helper.BICORN:
                obj_path = new Bicorn(m_context);
                break;
            case Helper.CORNOID:
                obj_path = new Cornoid(m_context);
                break;
            case Helper.ELLIPSE_EVOLUTE:
                obj_path = new EllipseEvolute(m_context);
                break;
            case Helper.ELLIPSE:
                obj_path = new Ellipse(m_context);
                break;
            case Helper.EIGHT_CURVE:
                obj_path = new EightCurve(m_context);
                break;
            case Helper.LEMNISCATE:
                obj_path = new Lemniscate(m_context);
                break;
            case Helper.ASTROID:
                obj_path = new Astroid(m_context);
                break;
            case Helper.DELTOID:
                obj_path = new Deltoid(m_context);
                break;
        }
        objDeathPath = (DeathPath)mContext.findViewById(R.id.robot_path1);
        Runnable myRunnable = new Runnable(){
            public void run(){
                objDeathPath.drawPath(obj_path);
            }
        };
        Thread thread = new Thread(myRunnable);
        thread.start();
    }


    private void init_game_driver() {
        game_driver = new Runnable() {
            @Override
            public void run() {
                reDrawGame();
            }
        };
    }
    private void init_game() {
        FRAME_INTERVAL = 33;
        obj_P1 = (MyCircle)mContext.findViewById(R.id.P1_circle);
        obj_P1.init(new WeakReference<>(this), 0);
        obj_P2 = (MyCircle)mContext.findViewById(R.id.P2_circle);
        obj_P2.init(new WeakReference<>(this), 0);
        obj_P2.setBackgroundResource(R.drawable.background_black_circle);
        obj_P2.player_2 = true;

        init_game_driver();
    }


    public void initialize_screen_controls()
    {
        tv_P1_Score = (TextView)mContext.findViewById(R.id.tvP1Score);
        tv_P2_Score = (TextView)mContext.findViewById(R.id.tvP2Score);

        tv_P1_Score.setText("0");
        tv_P1_Score.setTextScaleX(1.2f);
        tv_P2_Score.setText("0");
        tv_P2_Score.setTextScaleX(1.2f);

        mainContainer = mContext.findViewById(R.id.multiplayer_game);
        View placeHolder1 = new View(mContext);
        View placeHolder2 = new View(mContext);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                (int)Helper.SCREEN_HEIGHT/2);
        placeHolder1.setLayoutParams(layoutParams);
        placeHolder2.setLayoutParams(layoutParams);
        ((ViewGroup)mainContainer).addView(placeHolder1);
        ((ViewGroup)mainContainer).addView(placeHolder2);
        placeHolder1.setY(Helper.SCREEN_HEIGHT / 2);

        placeHolder1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!isP1_Alive && !isP2_Alive)
                        startGame();
                }

                increase_P1_velocity = event.getAction() != MotionEvent.ACTION_UP;

                return true;
            }
        });
        placeHolder2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!isP2_Alive && !isP1_Alive)
                        startGame();
                }
                increase_P2_velocity = event.getAction() != MotionEvent.ACTION_UP;
                return true;
            }
        });

        float y =Helper.SCREEN_HEIGHT/2
                -  Helper.SCREEN_WIDTH/2
                + Helper.ConvertToPx(mContext,90);
        mContext.findViewById(R.id.img_flag).setX(Helper.SCREEN_WIDTH / 2);
        mContext.findViewById(R.id.img_flag).setY(y - Helper.ConvertToPx(mContext, 55));
    }


    private void addDeathCircle()
    {
        final DeathCircle objDeathCircle ;
        //swsahu - here
        if((P1_Score+P2_Score)%2==0)
            objDeathCircle = new DeathCircle(mContext,obj_path,1);
        else
            objDeathCircle = new DeathCircle(mContext,obj_path,-1);

        ((ViewGroup) obj_P1.getParent()).addView(objDeathCircle);
        deathCircles[top++] = objDeathCircle;
        objDeathCircle.setScaleX(.4f);
        objDeathCircle.setScaleY(.4f);
        objDeathCircle.animate()
                .scaleX(1)
                .scaleY(1)
                .setDuration(1000);

        new CountDownTimer(1600, 1600) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                objDeathCircle.activated=true;
            }
        }.start();
    }
    private void reset_data()
    {
        P1_Score = 0;
        P2_Score = 0;
        game_time =0;
        increment_score(0, 0);
        mainContainer.findViewById(R.id.region_you_win).setVisibility(View.INVISIBLE);
        setGameObjectVisibility(View.VISIBLE);
    }

    boolean firstRun = true;

    public void drawDeathPath()
    {
        objDeathPath.setAlpha(1);
        if(firstRun)
        {
            firstRun = false;
            objDeathPath.invalidate();
        }

    }
    public void startGame() {

        reset_data();

        addDeathCircle();
        obj_P1.reset();
        obj_P2.reset();
        drawDeathPath();

        isP1_Alive = true;
        isP2_Alive = true;
        reDrawGame();
        startGodMode(900);
    }




    public void reDrawGame()
    {
        if(!isP1_Alive )
            return;

        if(increase_P1_velocity)
            obj_P1.increase_velocity();
        else
            obj_P1.decrease_velocity();

        if(increase_P2_velocity)
            obj_P2.increase_velocity();
        else
            obj_P2.decrease_velocity();

        moveDeathCircles();
        obj_P1.move();
        obj_P2.move();

        if(!godMode)
        {
            if(!checkIfAlive(obj_P1))
            {
                obj_P1.reset();
            }
            if(!checkIfAlive(obj_P2))
            {
                obj_P2.reset();
            }
        }

        if(isP1_Alive)
            mHandler.postDelayed(game_driver, FRAME_INTERVAL);
        else
            flushAllTokens();
    }

    private void moveDeathCircles()
    {
        for(int i=0;i<top;i++)
            deathCircles[i].move();
    }
    public boolean checkIfAlive(MyCircle objPlayer)
    {
        objPlayer.recompute_position();
        float distance;
        float r1,r2;
        r2= objPlayer.radius;

        for (int i=0;i<top;i++){
            if(!deathCircles[i].activated)
                continue;

            deathCircles[i].recompute_position();
            r1 = deathCircles[i].radius;

            float deltaX = deathCircles[i].centerX - objPlayer.centerX;
            float deltaY = deathCircles[i].centerY - objPlayer.centerY;

            distance =deltaX*deltaX + deltaY*deltaY;
            if (distance < (r1 + r2)*(r1 + r2)  )
            {
                if(mContext.vibration_on)
                    mContext.objVibrator.vibrate(45);

                startGodMode(360);
                mContext.objSoundManager.Play(mContext.objSoundManager.POP);
                return false;
            }
        }
        return true;
    }

    public void stop_game() {
        if(!isP1_Alive && !isP2_Alive)
            return;

        isP1_Alive = false;
        isP2_Alive = false;
        objDeathPath.setAlpha(0);
        setGameObjectVisibility(View.INVISIBLE);

        mContext.objMainViewManager.reloadBackground();
        if(mContext.vibration_on)
            mContext.objVibrator.vibrate(100);

        mHandler.removeCallbacks(game_driver);
        mContext.objSoundManager.Play(mContext.objSoundManager.YAY);
        showEndGameUI();
    }

    public void setGameObjectVisibility(int visibility) {
        mainContainer.findViewById(R.id.circle_path).setVisibility(visibility);
        mainContainer.findViewById(R.id.P1_circle).setVisibility(visibility);
        mainContainer.findViewById(R.id.P2_circle).setVisibility(visibility);
        mainContainer.findViewById(R.id.img_flag).setVisibility(visibility);
    }

    private void showEndGameUI() {

        String you_lose = "You\nLose";
        String you_win = "You\nWin!";
        mainContainer.findViewById(R.id.region_you_win).setVisibility(View.VISIBLE);
        if(P1_Score<P2_Score)
        {
            ((TextView)mainContainer.findViewById(R.id.tvP2Result)).setText(you_win);
            ((TextView)mainContainer.findViewById(R.id.tvP1Result)).setText(you_lose);
        }
        else
        {
            ((TextView)mainContainer.findViewById(R.id.tvP1Result)).setText(you_win);
            ((TextView)mainContainer.findViewById(R.id.tvP2Result)).setText(you_lose);
        }
    }

    public void stopGame()
    {
        isP1_Alive = false;
        isP2_Alive = false;
        mHandler.removeCallbacks(game_driver);
    }

    public void increment_score(int p1,int p2)
    {
        if(P2_Score<ScoreToReach)
            P1_Score+=p1;
        if(P1_Score<ScoreToReach)
            P2_Score+=p2;
        if(P1_Score>ScoreToReach || P2_Score > ScoreToReach)
            return;

        tv_P1_Score.setText(String.valueOf(P1_Score));
        tv_P2_Score.setText(String.valueOf(P2_Score));

        if(P1_Score + P2_Score > 0 && (P1_Score+P2_Score)%10==0)
            addDeathCircle();


        if(P1_Score>=ScoreToReach)
            stop_game();
        else if(P2_Score>=ScoreToReach)
            stop_game();
    }

    public void removeToken(View v)
    {
        if(v.getParent()!=null)
            ((ViewGroup)v.getParent()).removeView(v);
    }

    public void flushAllTokens()
    {
        for (int i=0;i<top;i++)
            removeToken(deathCircles[i]);
        top=0;
    }

    public void startGodMode(long duration)
    {
        godMode = true;
        new CountDownTimer(duration, duration) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                godMode=false;
            }
        }.start();
    }

}
