package com.ihc.tetris;

import android.hardware.SensorEventListener;

/**
 * Created by mhbackes on 19/10/16.
 */
public interface MotionController extends SensorEventListener {
    int AXIS_X = 0, AXIS_Y = 1, AXIS_Z = 2;
    int ORIENT_DOWN = 0, ORIENT_UP = 1, ORIENT_LEFT = 2, ORIENT_RIGHT = 3;

    int getOrientation();
    boolean isDropDown();
}
