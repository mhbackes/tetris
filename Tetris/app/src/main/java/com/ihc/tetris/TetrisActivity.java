package com.ihc.tetris;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TetrisActivity extends AppCompatActivity {
    // Debugging
    public static final String TAG = "TetrisActivity";

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    BluetoothService mBluetoothService = null;
    MotionController mMotionController = null;
    SensorManager mSensorManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tetris);

        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        mBluetoothService = BluetoothService.getInstance();
        mBluetoothService.setHandler(mHandler);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(!mSensorManager.getSensorList(Sensor.TYPE_ROTATION_VECTOR).isEmpty()) {
            mMotionController = new MotionControllerPrecise(this, mBluetoothService);
            Log.d(TAG, "Using PRECISE motion controller.");
            mContentView.setOnTouchListener(
                    new TouchController(this,mMotionController, mBluetoothService));
        }
        else if(!mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).isEmpty()) {
            mMotionController = new MotionControllerSimple(this, mBluetoothService);
            Log.d(TAG, "Using SIMPLE motion controller.");
            mContentView.setOnTouchListener(
                    new TouchController(this, mMotionController, mBluetoothService));
        }
        else {
            TextView textView = (TextView) mContentView;
            textView.setText("Incompatible\nDevice :(");
            Log.d(TAG, "Incompatible device.");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mMotionController != null) {
            mMotionController.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mMotionController != null) {
            mMotionController.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mMotionController != null) {
            mMotionController.stop();
        }
        mBluetoothService.stop();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        hide();
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            hide();
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    if(msg.arg1 == BluetoothService.STATE_NONE
                            || msg.arg1 == BluetoothService.STATE_LISTEN)
                        finish();
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    // TODO read message
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(TetrisActivity.this, msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
