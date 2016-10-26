package com.ihc.tetris;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by mhbackes on 20/10/16.
 */
public class TouchController implements View.OnTouchListener {
    public static final int MOVE_LEFT = 4, MOVE_RIGHT = 5;

    private MotionController mMotionController = null;
    private Activity mActivity = null;
    private BluetoothService mBluetoothService = null;


    TouchController(Activity activity, MotionController motionController,
                    BluetoothService bluetoothService) {
        mActivity = activity;
        mMotionController = motionController;
        mBluetoothService = bluetoothService;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return onActionDown(event);
            case MotionEvent.ACTION_MOVE:
                return onActionMove(event);
        }
        return false;
    }

    int mPrevX, mPrevY;
    int mPrevDivDistance;
    int mDistance;

    public boolean onActionDown(MotionEvent e) {
        mPrevDivDistance = mDistance = 0;
        mPrevX = (int) e.getX();
        mPrevY = (int) e.getY();
        TextView textView = (TextView) mActivity.findViewById(R.id.fullscreen_content);
        textView.setText(Integer.toString(mDistance));
        return true;
    }

    @SuppressLint("SetTextI18n")
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
                break;
            case MotionController.ORIENT_UP:
                mDistance -= dx;
                break;
            case MotionController.ORIENT_LEFT:
                mDistance += dy;
                break;
            case MotionController.ORIENT_RIGHT:
                mDistance -= dy;
        }
        int divDistance = mDistance / 100;
        int dDistance = divDistance - mPrevDivDistance;
        if(Math.abs(dDistance) >= 1 && Math.abs(divDistance) <= 9) {
            mPrevDivDistance = divDistance;
            Log.d("TouchController", "MOVE " + dDistance);
            sendMoveMessage(dDistance);
            TextView textView = (TextView) mActivity.findViewById(R.id.fullscreen_content);
            textView.setText(Integer.toString(divDistance));
        }
        return true;
    }

    void sendMoveMessage(int distance) {
        int posDist = distance;
        int negDist = distance;
        while(posDist-- > 0)
            mBluetoothService.sendMessage(MOVE_RIGHT);
        while(negDist++ < 0)
            mBluetoothService.sendMessage(MOVE_LEFT);
    }
}
