package com.ihc.tetris;

import android.hardware.SensorEventListener;

/**
 * Created by mhbackes on 19/10/16.
 */
public interface MotionController extends SensorEventListener {
    public int getOrientation();
    public boolean isDropDown();
}
