package com.ihc.tetris;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by mhbackes on 19/10/16.
 */
public abstract class MotionController implements SensorEventListener {
    public static final int AXIS_X = 0, AXIS_Y = 1, AXIS_Z = 2;
    public static final int ORIENT_DOWN = 0, ORIENT_UP = 1, ORIENT_LEFT = 2, ORIENT_RIGHT = 3;
    public static final int ROTATE_LEFT = 0, ROTATE_RIGHT = 1, START_DROP = 2, STOP_DROP = 3;

    protected SensorManager mSensorManager = null;
    protected Activity mActivity = null;
    protected BluetoothService mBluetoothService = null;

    protected int mOrientation = 0;
    protected boolean mDropDown = false;

    MotionController(Activity activity, BluetoothService bluetoothService) {
        mActivity = activity;
        mSensorManager = (SensorManager) mActivity.getSystemService(mActivity.SENSOR_SERVICE);
        mBluetoothService = bluetoothService;
        start();
    }

    public abstract void start();

    public abstract void stop();

    public int getOrientation() {
        return mOrientation;
    }

    public boolean isDropDown() {
        return mDropDown;
    }

    protected void setOrientation(int orientation) {
        switch(orientation) {
            case ORIENT_DOWN:
                if(mOrientation == ORIENT_LEFT)
                    mBluetoothService.sendMessage(ROTATE_RIGHT);
                else
                    mBluetoothService.sendMessage(ROTATE_LEFT);
                break;
            case ORIENT_UP:
                if(mOrientation == ORIENT_LEFT)
                    mBluetoothService.sendMessage(ROTATE_LEFT);
                else
                    mBluetoothService.sendMessage(ROTATE_RIGHT);
                break;
            case ORIENT_LEFT:
                if(mOrientation == ORIENT_UP)
                    mBluetoothService.sendMessage(ROTATE_RIGHT);
                else
                    mBluetoothService.sendMessage(ROTATE_LEFT);
                break;
            case ORIENT_RIGHT:
                if(mOrientation == ORIENT_UP)
                    mBluetoothService.sendMessage(ROTATE_LEFT);
                else
                    mBluetoothService.sendMessage(ROTATE_RIGHT);
                break;
        }
        mOrientation = orientation;
    }

    @Override
    public abstract void onSensorChanged(SensorEvent event);

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}

