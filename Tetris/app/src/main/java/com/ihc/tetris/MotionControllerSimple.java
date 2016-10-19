package com.ihc.tetris;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by mhbackes on 19/10/16.
 */
public class MotionControllerSimple implements MotionController {
    private SensorManager mSensorManager = null;
    private Activity mActivity = null;

    MotionControllerSimple(Activity activity) {
        mActivity = activity;
        mSensorManager = (SensorManager) mActivity.getSystemService(mActivity.SENSOR_SERVICE);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private static final int X_AXIS = 0, Y_AXIS = 1, Z_AXIS = 2;
    private static final int DOWN = 0, UP = 1, LEFT = 2, RIGHT = 3;
    private int mOrientation;
    private boolean mDropDown;

    public int getOrientation() {
        return mOrientation;
    }

    public boolean isDropDown() {
        return mDropDown;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            TextView textView = (TextView) mActivity.findViewById(R.id.fullscreen_content);
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
                if(mDropDown) {
                    mDropDown = event.values[Z_AXIS] > 6.0f; // TODO send stop drop message
                    if(!mDropDown)
                        Log.d("Tetris", "STOP DROP");
                } else {
                    mDropDown = event.values[Z_AXIS] > 9.0f; // TODO send start drop message
                    if(mDropDown)
                        Log.d("Tetris", "START DROP");
                }
            }
            if(mDropDown)
                textView.setText(".");
            else {
                String arrows[] = {"v", "^", "<", ">"};
                textView.setText(arrows[mOrientation]);
            }
        }
    }

    boolean changeOrientationY(float axis[]) {
        boolean isGrtrThanCurr = Math.abs(axis[X_AXIS]) > Math.abs(axis[Y_AXIS]) + 3.0f;
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
        boolean isGrtrThanCurr = Math.abs(axis[Y_AXIS]) > Math.abs(axis[X_AXIS]) + 3.0f;
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
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
