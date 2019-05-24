package com.archer.circle_run.game_logic;

import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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


public class SinglePlayerGame
{
    public MainActivity mContext;

    TextView tvScore;
    TextView tvAvgScore;
    TextView tvBestScore;

    Handler mHandler;
    Runnable game_driver;

    public long Score;
    long pathBestScore;
    int game_time;
    long prev_average;
    long FRAME_INTERVAL;
    boolean isAlive;
    boolean godMode;
    boolean onePathGame;
    boolean increaseVelocity;

    AllPaths objPath[];
    AllPaths obj_path;
    int max_path_index;
    public int path_index;
    public MyCircle objMyCircle;
    public DeathPath objDeathPath;
    View mainContainer;
    DeathCircle deathCircles[];
    int top;


    public SinglePlayerGame(WeakReference<MainActivity> m_context,int path_index)
    {
        this.path_index = path_index;
        onePathGame = true;
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
        objDeathPath.setAlpha(0);
        Runnable myRunnable = new Runnable(){
            public void run(){
                objDeathPath.drawPath(obj_path);
            }
        };
        Thread thread = new Thread(myRunnable);
        thread.start();
    }

    public SinglePlayerGame(WeakReference<MainActivity> m_context)
    {
        mContext = m_context.get();
        mHandler = new Handler();
        deathCircles = new DeathCircle[100];
        top=0;

        init_game();
        initializePaths();
        initialize_screen_controls();

        mContext.CollectGarbage();
    }

    public void initializePaths()
    {
        path_index = 0;
        max_path_index = 7;
        WeakReference<MainActivity> m_context = new WeakReference<>(mContext);



        objPath = new AllPaths[10];
        objPath[5]= new Bicorn(m_context);
        objPath[7] = new Ellipse(m_context);
        objPath[4] = new Cornoid(m_context);
        objPath[1] = new Deltoid(m_context);
        objPath[6] = new Astroid(m_context);
        objPath[2] = new EightCurve(m_context);
        objPath[0] = new Lemniscate(m_context);
        objPath[3] = new EllipseEvolute(m_context);

        objDeathPath = (DeathPath)mContext.findViewById(R.id.robot_path1);
        prepareInitialPath();
    }

    private void prepareInitialPath()
    {
        path_index = -1;
        objDeathPath.setAlpha(0);
        Runnable myRunnable = new Runnable(){
            public void run(){
                objDeathPath.drawPath(objPath[0]);
            }
        };
        Thread thread = new Thread(myRunnable);
        thread.start();
    }

    private void prepareNextPath()
    {
        if(onePathGame)
            return;
        Runnable myRunnable = new Runnable(){
            public void run(){
                int next_path_index = path_index+1;
                if(next_path_index>max_path_index)
                    next_path_index=0;
                objDeathPath.drawPath(objPath[next_path_index]);
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
        objMyCircle = (MyCircle)mContext.findViewById(R.id.white_circle);
        objMyCircle.init(new WeakReference<>(this));

        init_game_driver();
    }

    public void setBestScoreText()
    {
        if(onePathGame)
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            pathBestScore = prefs.getLong(String.valueOf(path_index), 0);
            tvBestScore.setText(String.valueOf(pathBestScore));
        }
        else
        {
            tvBestScore.setText(String.valueOf(mContext.BestScore));
            pathBestScore = mContext.BestScore;
        }
    }

    public void initialize_screen_controls()
    {
        tvScore = (TextView)mContext.findViewById(R.id.tvScore);
        tvBestScore = (TextView)mContext.findViewById(R.id.tvBestScore_single_player);
        tvAvgScore = (TextView)mContext.findViewById(R.id.tvAverageScore_single_player);

        tvScore.setText("0");
        tvAvgScore.setText("Avg: " + String.valueOf(mContext.getAverageScore()));
        tvScore.setTextScaleX(1.2f);
        setBestScoreText();

        mainContainer = mContext.findViewById(R.id.single_player_game);
        mainContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!isAlive)
                        startGame();
                }
                increaseVelocity = event.getAction() != MotionEvent.ACTION_UP;

                return true;
            }
        });

        float y =Helper.SCREEN_HEIGHT/2
                -  Helper.SCREEN_WIDTH/2
                + Helper.ConvertToPx(mContext,90);
        mContext.findViewById(R.id.img_flag).setX(Helper.SCREEN_WIDTH / 2);
        mContext.findViewById(R.id.img_flag).setY(y - Helper.ConvertToPx(mContext, 55));
        mContext.findViewById(R.id.logo).setY(y);
        mContext.findViewById(R.id.region_share).setY(y + Helper.ConvertToPx(mContext,280));
    }


    public void hideLogo()
    {
        mContext.findViewById(R.id.logo).setVisibility(View.INVISIBLE);
    }

    public  void swapLogoAndScorePosition()
    {
        if(onePathGame)
            return;

        float y1,y2;
        y1 =  mContext.findViewById(R.id.logo).getY();
        y2 =  mContext.findViewById(R.id.region_score).getY();

        mContext.findViewById(R.id.logo).setY(y2);
        mContext.findViewById(R.id.region_score).setY(y1);

        mContext.findViewById(R.id.logo).setVisibility(View.VISIBLE);
    }

    private void addDeathCircle()
    {
        final DeathCircle objDeathCircle ;
        if(Score%2==0)
            objDeathCircle = new DeathCircle(mContext,obj_path,1);
        else
            objDeathCircle = new DeathCircle(mContext,obj_path,-1);
        ((ViewGroup)objMyCircle.getParent()).addView(objDeathCircle);
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
        Score = -1;
        game_time =0;
        increment_score();
        mContext.TotalGames++;
        play_flag_top_score=true;
        prev_average=mContext.getAverageScore();
        mContext.findViewById(R.id.region_share).setVisibility(View.INVISIBLE);
        tvAvgScore.setText("Avg: " + String.valueOf(mContext.getAverageScore()));
    }

    private void loadNextPath()
    {
        if(onePathGame)
            return;

        path_index++;
        if(path_index>max_path_index)
            path_index=0;
        obj_path = objPath[path_index];
    }

    boolean firstRun = true;

    public void swapTextViews()
    {
        hideLogo();
        if(onePathGame)
            return;

        if(!firstRun)
            swapLogoAndScorePosition();
        else
            firstRun = false;
    }

    public void drawDeathPath()
    {
        if(!onePathGame || firstRun)
        {
            firstRun = false;
            objDeathPath.invalidate();
            objDeathPath.setAlpha(1);
        }

    }
    public void startGame() {

        reset_data();
        loadNextPath();
        swapTextViews();

        addDeathCircle();
        objMyCircle.reset();
        drawDeathPath();

        isAlive=true;
        reDrawGame();
        startGodMode(900);
    }




    public void reDrawGame()
    {
        if(!isAlive)
            return;

        if(increaseVelocity)
            objMyCircle.increase_velocity();
        else
            objMyCircle.decrease_velocity();

        moveDeathCircles();
        objMyCircle.move();

        if(!godMode)
            checkIfAlive();
        if(isAlive)
            mHandler.postDelayed(game_driver, FRAME_INTERVAL);
        else
            flushAllTokens();
    }

    private void moveDeathCircles()
    {
        for(int i=0;i<top;i++)
            deathCircles[i].move();
    }
    public void checkIfAlive()
    {
        objMyCircle.recompute_position();
        float distance;
        float r1,r2;
        r2= objMyCircle.radius;

        for (int i=0;i<top;i++){
            if(!deathCircles[i].activated)
                continue;

            deathCircles[i].recompute_position();
            r1 = deathCircles[i].radius;

            float deltaX = deathCircles[i].centerX - objMyCircle.centerX;
            float deltaY = deathCircles[i].centerY - objMyCircle.centerY;

            distance =deltaX*deltaX + deltaY*deltaY;
            if (distance < (r1 + r2)*(r1 + r2)  )  // No overlap
            {
                stop_game();
                break;
            }
        }

    }

    public void stop_game() {
        isAlive = false;
        prepareNextPath();
        swapLogoAndScorePosition();
        mContext.objMainViewManager.reloadBackground();
        mContext.findViewById(R.id.region_share).setVisibility(View.VISIBLE);

        if(!onePathGame)
            objDeathPath.setAlpha(0);

        if(mContext.vibration_on)
            mContext.objVibrator.vibrate(100);

        updateScoringData();
        mHandler.removeCallbacks(game_driver);
        mContext.objSoundManager.Play(mContext.objSoundManager.POP);


        //Show Ad
        if(mContext.gameTime>540 && !mContext.objManageAds.adFreeVersion)
        {
            mContext.objManageAds.ShowRemoveAdDialog();
            mContext.gameTime = 0;
        }
    }



    public void stopGame()
    {
        isAlive = false;
        updateScoringData();
        mHandler.removeCallbacks(game_driver);
    }

    public void updateScoringData()
    {
        Runnable myRunnable = new Runnable(){
            public void run(){
                WeakReference<MainActivity> m_context = new WeakReference<>(mContext);
                String score_data = String.valueOf(mContext.BestScore)
                        + "_"
                        + String.valueOf(mContext.TotalGames)
                        + "_"
                        + String.valueOf(mContext.TotalScore);

                Helper.writeToFile(m_context, Helper.TOP_SCORE_FILE, score_data);

                mContext.objGameServices.process_score(Score, path_index);
                updatePathTopScore();

            }

            private void updatePathTopScore()
            {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = preferences.edit();
                if(Score>preferences.getLong(String.valueOf(path_index),0))
                {
                    editor.putLong(String.valueOf(path_index), Score);

                    editor.apply();
                }
            }
        };
        Thread thread = new Thread(myRunnable);
        thread.start();
    }


    boolean play_flag_top_score;
    private void play_top_score_music()
    {
        if(play_flag_top_score)
        {
            play_flag_top_score = false;
            mContext.objSoundManager.Play(mContext.objSoundManager.YAY);
        }
    }

    public void increment_score() {
        Score++;
        mContext.TotalScore++;
        tvScore.setText(String.valueOf(Score));
        if(Score>pathBestScore)
        {
            mContext.BestScore = Score;
            pathBestScore = Score;
            tvBestScore.setText(String.valueOf(Score));
            play_top_score_music();
        }

        if(Score > 0 && Score%9==0)
            addDeathCircle();

        long avg = mContext.getAverageScore();
        if(prev_average < avg) {
            prev_average=avg;
            tvAvgScore.setText("Avg: " + String.valueOf(avg));
            if(mContext.TotalGames>27)
                mContext.objSoundManager.Play(mContext.objSoundManager.YAY);
        }
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
