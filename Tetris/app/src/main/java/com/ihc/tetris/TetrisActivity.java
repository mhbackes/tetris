package com.ihc.tetris;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TetrisActivity extends AppCompatActivity implements SensorEventListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private SensorManager mSensorManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tetris);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this,
                                        mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                                        SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private static final int X_AXIS = 0, Y_AXIS = 1, Z_AXIS = 2;
    private static final int DOWN = 0, UP = 1, LEFT = 2, RIGHT = 3;
    private int mOrientation;
    private boolean mBack;

    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            TextView textView = (TextView) findViewById(R.id.fullscreen_content);
            boolean changed = false;
            switch(mOrientation) {
                case UP:
                case DOWN:
                    changed = changeOrientationY(event.values);
                    break;
                case LEFT:
                case RIGHT:
                    changed = changeOrientationX(event.values);
            }
            if(!changed) {
                if(mBack) {
                    mBack = event.values[Z_AXIS] > 5.0f; // TODO send stop drop message
                    if(!mBack)
                        Log.d("Tetris", "STOP DROP");
                } else {
                    mBack = event.values[Z_AXIS] > 9.0f; // TODO send start drop message
                    if(mBack)
                        Log.d("Tetris", "START DROP");
                }
            }
            if(mBack)
                textView.setText(".");
            else {
                String arrows[] = {"v", "^", "<", ">"};
                textView.setText(arrows[mOrientation]);
            }
        }
    }

    boolean changeOrientationY(float axis[]) {
        boolean isGrtrThanCurr = Math.abs(axis[X_AXIS]) > Math.abs(axis[Y_AXIS]) + 2.0f;
        boolean isHighEnough = axis[X_AXIS] > 4.0f;
        boolean isLowEnough = axis[X_AXIS] < -4.0f;
        if(!isGrtrThanCurr)
            return false;
        if(isHighEnough) {
            mOrientation = LEFT; // TODO send rotation message
            Log.d("Tetris", "ORIENTATION LEFT");
            return true;
        }
        if(isLowEnough) {
            mOrientation = RIGHT; // TODO send rotation message
            Log.d("Tetris", "ORIENTATION RIGHT");
            return true;
        }
        return false;
    }

    boolean changeOrientationX(float axis[]) {
        boolean isGrtrThanCurr = Math.abs(axis[Y_AXIS]) > Math.abs(axis[X_AXIS]) + 2.0f;
        boolean isHighEnough = axis[Y_AXIS] > 4.0f;
        boolean isLowEnough = axis[Y_AXIS] < -4.0f;
        if(isGrtrThanCurr) {
            if(isHighEnough) {
                mOrientation = DOWN; // TODO send rotation message
                Log.d("Tetris", "ORIENTATION DOWN");
                return true;
            } else if(isLowEnough) {
                mOrientation = UP; // TODO send rotation message
                Log.d("Tetris", "ORIENTATION UP");
                return true;
            }
        }
        return false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
