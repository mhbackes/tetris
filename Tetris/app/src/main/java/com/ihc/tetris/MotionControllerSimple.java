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
public class MotionControllerSimple extends MotionController {
    MotionControllerSimple(Activity activity, BluetoothService bluetoothService) {
        super(activity, bluetoothService);
    }

    public void start() {
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        mSensorManager.unregisterListener(this);
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
                    mBluetoothService.sendMessage(STOP_DROP);
                    changed = true;
                }
            } else {
                mDropDown = event.values[AXIS_Z] > 9.0f; // TODO send start drop message
                if(mDropDown) {
                    Log.d("SimpleController", "START DROP");
                    mBluetoothService.sendMessage(START_DROP);
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
            Log.d("SimpleController", "ORIENTATION LEFT");
            setOrientation(ORIENT_LEFT);
            return true;
        }
        if(isLowEnough) {
            Log.d("SimpleController", "ORIENTATION RIGHT");
            setOrientation(ORIENT_RIGHT);
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
                Log.d("Tetris", "ORIENTATION DOWN");
                setOrientation(ORIENT_DOWN);
                return true;
            } else if(isLowEnough) {
                Log.d("Tetris", "ORIENTATION UP");
                setOrientation(ORIENT_UP);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
