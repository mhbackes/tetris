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
public class MotionControllerPrecise implements MotionController {
    private SensorManager mSensorManager = null;
    private Activity mActivity = null;

    MotionControllerPrecise(Activity activity) {
        mActivity = activity;
        mSensorManager = (SensorManager) mActivity.getSystemService(mActivity.SENSOR_SERVICE);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_GAME);
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

    float mR[] = new float[9];
    float mAngles[] = new float[3];
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(mR, event.values);
            SensorManager.getOrientation(mR, mAngles);
            double pitchAngle = Math.toDegrees(mAngles[1]);
            double rollAngle = Math.toDegrees(mAngles[2]);

            TextView textView = (TextView) mActivity.findViewById(R.id.fullscreen_content);
            boolean changed = false;

            switch(mOrientation) {
                case ORIENT_UP:
                case ORIENT_DOWN:
                    changed = changeOrientationY(pitchAngle, rollAngle);
                    break;
                case ORIENT_LEFT:
                case ORIENT_RIGHT:
                    changed = changeOrientationX(pitchAngle);
            }
            if(!changed) {
                if(mDropDown) {
                    mDropDown = Math.abs(pitchAngle) < 35.0; // TODO send stop drop message
                    if(!mDropDown)
                        Log.d("Tetris", "STOP DROP");
                } else {
                    mDropDown = Math.abs(pitchAngle) < 25.0 && Math.abs(rollAngle) < 20.0 ; // TODO send start drop message
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

    boolean changeOrientationY(double pitch, double roll) {
        if(Math.abs(pitch) < 30) {
            if (roll < -30.0) {
                mOrientation = ORIENT_LEFT; // TODO send rotation message
                Log.d("Tetris", "ORIENTATION LEFT");
                return true;
            }
            if (roll > 30.0) {
                mOrientation = ORIENT_RIGHT; // TODO send rotation message
                Log.d("Tetris", "ORIENTATION RIGHT");
                return true;
            }
        }
        return false;
    }

    boolean changeOrientationX(double pitch) {
        if(pitch < -60.0) {
            mOrientation = ORIENT_DOWN; // TODO send rotation message
            Log.d("Tetris", "ORIENTATION DOWN");
            return true;
        } else if(pitch > 60.0) {
            mOrientation = ORIENT_UP; // TODO send rotation message
            Log.d("Tetris", "ORIENTATION UP");
            return true;
        }
        return false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
