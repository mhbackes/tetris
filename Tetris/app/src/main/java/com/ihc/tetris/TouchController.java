package com.ihc.tetris;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by mhbackes on 20/10/16.
 */
public class TouchController implements View.OnTouchListener {
    MotionController mMotionController = null;
    private Activity mActivity = null;

    TouchController(Activity activity, MotionController motionController) {
        mActivity = activity;
        mMotionController = motionController;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return onActionDown(event);
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_MOVE:
                return onActionMove(event);
        }
        return false;
    }

    int mPrevX, mPrevY;
    int mDistance;

    public boolean onActionDown(MotionEvent e) {
        mDistance = 0;
        mPrevX = (int) e.getX();
        mPrevY = (int) e.getY();
        TextView textView = (TextView) mActivity.findViewById(R.id.fullscreen_content);
        textView.setText(Integer.toString((int) mDistance));
        return true;
    }

    public boolean onActionMove(MotionEvent e) {
        int x = (int) e.getX();
        int y = (int) e.getY();
        int dx = x - mPrevX;
        int dy = y - mPrevY;
        int orientation = mMotionController.getOrientation();
        mPrevX = x;
        mPrevY = y;
        switch(orientation) {
            case MotionController.ORIENT_DOWN:
                mDistance += dx;
                Log.d("TouchController", "MOVE " + dx); //TODO send move message
                break;
            case MotionController.ORIENT_UP:
                mDistance -= dx;
                Log.d("TouchController", "MOVE " + dx); //TODO send move message
                break;
            case MotionController.ORIENT_LEFT:
                mDistance += dy;
                Log.d("TouchController", "MOVE " + dy); //TODO send move message
                break;
            case MotionController.ORIENT_RIGHT:
                mDistance -= dy;
                Log.d("TouchController", "MOVE " + dy); //TODO send move message
        }
        TextView textView = (TextView) mActivity.findViewById(R.id.fullscreen_content);
        textView.setText(Integer.toString((int) mDistance));
        return true;
    }
}
