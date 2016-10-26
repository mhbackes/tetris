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
public class MotionControllerPrecise extends MotionController {
    public static final String TAG = "MotionConrollerPrecise";

    MotionControllerPrecise(Activity activity, BluetoothService bluetoothService) {
        super(activity, bluetoothService);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_GAME);
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
                mDropDown = Math.abs(pitchAngle) < 35.0 && Math.abs(rollAngle) < 35.0;
                if(!mDropDown) {
                    Log.d("PreciseController", "STOP DROP");
                    mBluetoothService.sendMessage(STOP_DROP);
                    changed = true;
                }
            } else {
                mDropDown = Math.abs(pitchAngle) < 25.0 && Math.abs(rollAngle) < 25.0 ;
                if(mDropDown) {
                    Log.d("PreciseController", "START DROP");
                    mBluetoothService.sendMessage(START_DROP);
                    changed = true;
                }
            }
            if(changed) {
                if (mDropDown)
                    textView.setText(".");
                else {
                    String arrows[] = {"v", "^", "<", ">", "*"};
                    textView.setText(arrows[mOrientation]);
                }
            }
        }
    }

    boolean changeOrientationY(double pitch, double roll) {
        if(Math.abs(pitch) < 35.0) {
            if (roll < -35.0) {
                setOrientation(ORIENT_LEFT);
                Log.d("PreciseController", "ORIENTATION LEFT");
                return true;
            }
            if (roll > 35.0) {
                setOrientation(ORIENT_RIGHT);
                Log.d("PreciseController", "ORIENTATION RIGHT");
                return true;
            }
        }
        return false;
    }

    boolean changeOrientationX(double pitch) {
        if(pitch < -55.0) {
            setOrientation(ORIENT_DOWN);
            Log.d("PreciseController", "ORIENTATION DOWN");
            return true;
        } else if(pitch > 55.0) {
            setOrientation(ORIENT_UP);
            Log.d("PreciseController", "ORIENTATION UP");
            return true;
        }
        return false;
    }
}
