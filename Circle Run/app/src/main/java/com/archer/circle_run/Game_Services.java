package com.archer.circle_run;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.archer.circle_run.GameServices.BaseGameUtils;
import com.archer.circle_run.game_logic.path_logic.Astroid;
import com.archer.circle_run.game_logic.path_logic.Bicorn;
import com.archer.circle_run.game_logic.path_logic.Cornoid;
import com.archer.circle_run.game_logic.path_logic.Deltoid;
import com.archer.circle_run.game_logic.path_logic.EightCurve;
import com.archer.circle_run.game_logic.path_logic.Ellipse;
import com.archer.circle_run.game_logic.path_logic.EllipseEvolute;
import com.archer.circle_run.game_logic.path_logic.Lemniscate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

import java.lang.ref.WeakReference;

public class Game_Services
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    MainActivity mContext;

    // Client used to interact with Google APIs
    public GoogleApiClient mGoogleApiClient;

    // Are we currently resolving a connection failure?
    public boolean mResolvingConnectionFailure = false;

    // Has the user clicked the sign-in button?
    public boolean mSignInClicked = false;

    // Automatically start the sign-in flow when the Activity starts
    public boolean mAutoStartSignInFlow = true;

    // request codes we use when invoking an external activity
    public static final int RC_RESOLVE = 5000;
    public static final int RC_UNUSED = 5001;
    public static final int RC_SIGN_IN = 9001;


    public Game_Services(WeakReference<MainActivity> m_context)
    {
        mContext=m_context.get();
        // Create the Google API Client with access to Plus and Games
        mGoogleApiClient = new GoogleApiClient.Builder(mContext.getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
    }


    public void connectToServices() {
        mGoogleApiClient.connect();
    }

    public void disconnectServices() {
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    public boolean isSignedIn() {
        return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
    }

    @Override
    public void onConnected(Bundle bundle) {
//         Show sign-out button on main menu
//
//         Show "you are signed in" message on win screen, with no sign in button.
//
//         Set the greeting appropriately on main menu

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Log.d(TAG, "onConnectionFailed(): attempting to resolve");
        if (mResolvingConnectionFailure) {
//            Log.d(TAG, "onConnectionFailed(): already resolving");
            return;
        }
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(mContext,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, mContext.getString(R.string.signin_other_error));
        }
    }

    public void ShowAllLeaderBoards()
    {
        if (isSignedIn())
        {
            mContext.startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(
                    mGoogleApiClient), RC_UNUSED);
        }
        else
            BaseGameUtils.makeSimpleDialog(mContext, mContext.getString(R.string.leaderboards_not_available)).show();
    }

    private String getLeaderBoardId(int path_type)
    {
        switch (path_type) {
            case Helper.BICORN:
                return mContext.getString(R.string.leaderboard_bicorn);
            case Helper.CORNOID:
                return mContext.getString(R.string.leaderboard_cornoid);
            case Helper.ELLIPSE_EVOLUTE:
                return mContext.getString(R.string.leaderboard_ellipse_evolute);
            case Helper.ELLIPSE:
                return mContext.getString(R.string.leaderboard_ellipse);
            case Helper.EIGHT_CURVE:
                return mContext.getString(R.string.leaderboard_eight_curve);
            case Helper.LEMNISCATE:
                return mContext.getString(R.string.leaderboard_lemniscate);
            case Helper.ASTROID:
                return mContext.getString(R.string.leaderboard_astroid);
            case Helper.DELTOID:
                return mContext.getString(R.string.leaderboard_deltoid);
            default:
                return mContext.getString(R.string.leaderboard_top_score);
        }
    }

    public void onShowLeaderBoardRequested(int path_type) {
        String leader_board_id=getLeaderBoardId(path_type);
        if (isSignedIn())
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            if(prefs.getBoolean(String.valueOf(Helper.TOP_SCORE_UPDATE_REQUIRED),false))
            {
                long prev_data = Long.parseLong(Helper.readFromFile(new WeakReference<>(mContext), Helper.UPDATE_FILE));
                submitScore(leader_board_id, prev_data);
            }

            mContext.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                    leader_board_id), RC_UNUSED);
        } else
            BaseGameUtils.makeSimpleDialog(mContext, mContext.getString(R.string.leaderboards_not_available)).show();
    }

    public void submitScore(String leader_board_id,long score)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        WeakReference<MainActivity> m_context = new WeakReference<>(mContext);

        if (isSignedIn())
        {
            Games.Leaderboards.submitScore(mGoogleApiClient,leader_board_id,score);
            editor.putBoolean(String.valueOf(Helper.TOP_SCORE_UPDATE_REQUIRED), false);
            Helper.writeToFile(m_context, Helper.UPDATE_FILE,"0");
        }
        else
        {
            editor.putBoolean(String.valueOf(Helper.TOP_SCORE_UPDATE_REQUIRED), true);
            long prev_data = Long.parseLong(Helper.readFromFile(m_context, Helper.UPDATE_FILE));
            if(prev_data<score)
                Helper.writeToFile(m_context,Helper.UPDATE_FILE,String.valueOf(score));
        }

        editor.apply();
    }

    public void onSignInButtonClicked() {
        // start the sign-in flow
        mSignInClicked = true;
        mGoogleApiClient.connect();
    }

    public void onSignOutButtonClicked() {
        mSignInClicked = false;
        Games.signOut(mGoogleApiClient);
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    public void process_score(long Score,int path_type)
    {
        if(!isSignedIn())
            return;


        submitScore(mContext.getString(R.string.leaderboard_top_score), Score);
        switch (path_type) {
            case Helper.BICORN:
                submitScore(mContext.getString(R.string.leaderboard_bicorn), Score);
                break;
            case Helper.CORNOID:
                submitScore(mContext.getString(R.string.leaderboard_cornoid), Score);
                break;
            case Helper.ELLIPSE_EVOLUTE:
                submitScore(mContext.getString(R.string.leaderboard_ellipse_evolute), Score);
                break;
            case Helper.ELLIPSE:
                submitScore(mContext.getString(R.string.leaderboard_ellipse), Score);
                break;
            case Helper.EIGHT_CURVE:
                submitScore(mContext.getString(R.string.leaderboard_eight_curve), Score);
                break;
            case Helper.LEMNISCATE:
                submitScore(mContext.getString(R.string.leaderboard_lemniscate), Score);
                break;
            case Helper.ASTROID:
                submitScore(mContext.getString(R.string.leaderboard_astroid), Score);
                break;
            case Helper.DELTOID:
                submitScore(mContext.getString(R.string.leaderboard_deltoid), Score);
                break;
        }

        submitScore(mContext.getString(R.string.leaderboard_top_score), Score);

        if(mContext.TotalGames>20)
        {
            Games.Achievements.increment(mGoogleApiClient,
                    mContext.getString(R.string.achievement_the_fun_has_just_started),1);
        }

        if(mContext.TotalGames>50) {
            Games.Achievements.increment(mGoogleApiClient,
                    mContext.getString(R.string.achievement_somebody_is_getting_addicted),1);
        }

        if(mContext.BestScore>5) {
            Games.Achievements.unlock(mGoogleApiClient,
                    mContext.getString(R.string.achievement_pfftt_noob));
        }

        if(mContext.BestScore>10) {
            Games.Achievements.unlock(mGoogleApiClient,
                    mContext.getString(R.string.achievement_at_least_you_are_trying));
        }
        if(mContext.BestScore<=2) {
            Games.Achievements.unlock(mGoogleApiClient,
                    mContext.getString(R.string.achievement_you_can_do_better));
        }

        if(mContext.BestScore>=20) {
            Games.Achievements.unlock(mGoogleApiClient,
                    mContext.getString(R.string.achievement_getting_used_to_things));
        }
        if(mContext.BestScore>35) {
            Games.Achievements.unlock(mGoogleApiClient,
                    mContext.getString(R.string.achievement_you_are_becoming_a_pro));
        }
        if(mContext.BestScore>=50) {
            Games.Achievements.unlock(mGoogleApiClient,
                    mContext.getString(R.string.achievement_you_are_becoming_a_pro));
        }
    }

}
