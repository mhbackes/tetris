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

    private int mOrientation;
    private boolean mDropDown;

    @Override
    public int getOrientation() {
        return mOrientation;
    }

    @Override
    public boolean isDropDown() {
        return mDropDown;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            TextView textView = (TextView) mActivity.findViewById(R.id.fullscreen_content);
            boolean changed = false;
            switch(mOrientation) {
                case ORIENT_UP:
                case ORIENT_DOWN:
                    changed = changeOrientationY(event.values);
                    break;
                case ORIENT_LEFT:
                case ORIENT_RIGHT:
                    changed = changeOrientationX(event.values);
            }
            if(mDropDown) {
                mDropDown = event.values[AXIS_Z] > 6.0f; // TODO send stop drop message
                if(!mDropDown) {
                    Log.d("SimpleController", "STOP DROP");
                    changed = true;
                }
            } else {
                mDropDown = event.values[AXIS_Z] > 9.0f; // TODO send start drop message
                if(mDropDown) {
                    Log.d("SimpleController", "START DROP");
                    changed = true;
                }
            }
            if(changed) {
                if (mDropDown)
                    textView.setText(".");
                else {
                    String arrows[] = {"v", "^", "<", ">"};
                    textView.setText(arrows[mOrientation]);
                }
            }
        }
    }

    boolean changeOrientationY(float axis[]) {
        boolean isGrtrThanCurr = Math.abs(axis[AXIS_X]) > Math.abs(axis[AXIS_Y]) + 3.0f;
        boolean isHighEnough = axis[AXIS_X] > 4.0f;
        boolean isLowEnough = axis[AXIS_X] < -4.0f;
        if(!isGrtrThanCurr)
            return false;
        if(isHighEnough) {
            mOrientation = ORIENT_LEFT; // TODO send rotation message
            Log.d("SimpleController", "ORIENTATION LEFT");
            return true;
        }
        if(isLowEnough) {
            mOrientation = ORIENT_RIGHT; // TODO send rotation message
            Log.d("SimpleController", "ORIENTATION RIGHT");
            return true;
        }
        return false;
    }

    boolean changeOrientationX(float axis[]) {
        boolean isGrtrThanCurr = Math.abs(axis[AXIS_Y]) > Math.abs(axis[AXIS_X]) + 3.0f;
        boolean isHighEnough = axis[AXIS_Y] > 4.0f;
        boolean isLowEnough = axis[AXIS_Y] < -4.0f;
        if(isGrtrThanCurr) {
            if(isHighEnough) {
                mOrientation = ORIENT_DOWN; // TODO send rotation message
                Log.d("Tetris", "ORIENTATION DOWN");
                return true;
            } else if(isLowEnough) {
                mOrientation = ORIENT_UP; // TODO send rotation message
                Log.d("Tetris", "ORIENTATION UP");
                return true;
            }
        }
        return false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
