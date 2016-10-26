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
    private BluetoothService mBluetoothService = null;

    MotionControllerPrecise(Activity activity, BluetoothService bluetoothService) {
        mActivity = activity;
        mSensorManager = (SensorManager) mActivity.getSystemService(mActivity.SENSOR_SERVICE);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_GAME);
        mBluetoothService = bluetoothService;
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
            if(mDropDown) {
                mDropDown = Math.abs(pitchAngle) < 35.0 && Math.abs(rollAngle) < 35.0; // TODO send stop drop message
                if(!mDropDown) {
                    Log.d("PreciseController", "STOP DROP");
                    changed = true;
                }
            } else {
                mDropDown = Math.abs(pitchAngle) < 25.0 && Math.abs(rollAngle) < 25.0 ; // TODO send start drop message
                if(mDropDown) {
                    Log.d("PreciseController", "START DROP");
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

    boolean changeOrientationY(double pitch, double roll) {
        if(Math.abs(pitch) < 35.0) {
            if (roll < -35.0) {
                mOrientation = ORIENT_LEFT; // TODO send rotation message
                Log.d("PreciseController", "ORIENTATION LEFT");
                return true;
            }
            if (roll > 35.0) {
                mOrientation = ORIENT_RIGHT; // TODO send rotation message
                Log.d("PreciseController", "ORIENTATION RIGHT");
                return true;
            }
        }
        return false;
    }

    boolean changeOrientationX(double pitch) {
        if(pitch < -55.0) {
            mOrientation = ORIENT_DOWN; // TODO send rotation message
            Log.d("PreciseController", "ORIENTATION DOWN");
            return true;
        } else if(pitch > 55.0) {
            mOrientation = ORIENT_UP; // TODO send rotation message
            Log.d("PreciseController", "ORIENTATION UP");
            return true;
        }
        return false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
