package com.example.jerryc.stopwatchtimer;


/**
 * Created by Jerry C on 4/7/2017.
 *
 * The Stopwatch model that is used to keep track of how much time has passed
 */

public class Stopwatch implements TimeInterface {

    /**
     * Contains the name of the stopwatch
     * */
    final static public String name = "Stopwatch";

    /**
     * The time that the timer started in milliseconds.
     * will be in System.currentTimeMillis() system format
     */
    private long startTime = System.currentTimeMillis();

    /**
     * If the stopwatch is paused, paused time contains the time that has passed since before it was paused
     * So if timer starts and 10 seconds pass, and then pause is pressed,
     * pausedTime will have 10 seconds in milliseconds
     */
    private long pausedTime = 0;

    /**
     * Keeps track of run status of stopwatch
     * If the timer is running then isPaused will be false
     * */
    private boolean isPaused = true;


    /**
     * Makes a new stopwatch from knowing how long it has been running
     * */
    Stopwatch(long timeRunning){
        startTime = System.currentTimeMillis() - timeRunning;
        pausedTime = timeRunning;
    }

    /**
     * Makes a new stopwatch from knowing how long it has been running
     * */
    Stopwatch(long timeRunning, boolean isRunning){
        startTime = System.currentTimeMillis() - timeRunning;
        pausedTime = timeRunning;
        if(isRunning) {
            isPaused = false;
            pausedTime = 0;
        }
    }

    /**Starts the stopwatch*/
    public void start(){
        startTime = System.currentTimeMillis() - pausedTime;
        isPaused = false;
        pausedTime = 0;
    }

    /**
     * Pauses the stopwatch
     * */
    public void pause() {
        long endTime = System.currentTimeMillis();

        pausedTime = endTime - startTime;
        isPaused = true;
    }

    /**
     * Resumes the Stopwatch, should only be called if pause() was called
     * */
    public void resume() {
        startTime = System.currentTimeMillis() - pausedTime;
        isPaused = false;
        pausedTime = 0;
    }

    /**
     * Resets the stopwatch
     * */
    public void reset() {
        isPaused = true;
        pausedTime = 0;
    }

    public long getCountdownTime() {
        return 0;
    }

    public long getTime() {
        if (isPaused) {
            return pausedTime;
        }
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Returns whether the time is paused
     * */
    public boolean isPaused() {
        return isPaused;
    }


}
