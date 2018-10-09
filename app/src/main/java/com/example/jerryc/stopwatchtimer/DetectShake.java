package com.example.jerryc.stopwatchtimer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Jerry C on 5/1/2017.
 */

public class DetectShake implements SensorEventListener {

    /**
     * Uses gyroscope features of android to setup a shake listener.
     * Android Studio documentation was used to help make shake listeners
     */


    /**
     * If change in rotations is less than SHAKE_LIMIT then it will not detect it as a shake
     * If it's more, it will detect it as a shake
     * */
    private static final float SHAKE_LIMIT =  7.0F;

    /**
     * Delay before it will detect another shake after the previous shake
     */
    private static final int SHAKE_DELAY_MS = 550;

    /**
     * Keeps track of the time using System.current.millis of the last time a shake was detected
     * Used to prevent too many frequent shake detections
     */
    private long mShakeTimestamp = 0;

    /**
     * The listener that will be called when it has detected a shake
     */
    private OnShakeListener mListener;


    /**
     * Sets a listener so shake listener can be called when a shake is detected
     */
    void setOnShakeListener(OnShakeListener listener) {
        this.mListener = listener;
    }

    /**
     * Listener for when a shake is detected on the phone
     */
    interface OnShakeListener {
        void onShake();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Called after the gyroscope sensor detected a movement on the phone
     * Will be used to detect a phone shake
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (mListener != null) {

            //Gets absolute value of the changes in rotation axis
            float axisX = Math.abs(event.values[0]);
            float axisY = Math.abs(event.values[1]);
            float axisZ = Math.abs(event.values[2]);

            float totalRotationChange = axisX + axisY + axisZ;
            //If there is enough rotation and it hasn't sent a shake notification in 500ms/SHAKE_DELAY_MS, then sends a shake notification
            if (totalRotationChange > SHAKE_LIMIT && System.currentTimeMillis() > mShakeTimestamp + SHAKE_DELAY_MS) {

                //Saves last time a shake was detected
                mShakeTimestamp = System.currentTimeMillis();

                //Detects shake
                mListener.onShake();
            }
        }


    }
}

