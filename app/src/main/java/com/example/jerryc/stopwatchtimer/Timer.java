package com.example.jerryc.stopwatchtimer;

import android.util.Log;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jerry C on 4/7/2017.
 *
 * Timer model has basic running operations.  Time counts down in a timer. Stopwatch counts up.
 * Start
 * Stop
 * Pause
 * Reset
 * getTime
 *
 * Timer Model can take a notification listener for when the timer reaches 0:00
 */

class Timer implements TimeInterface {

    final static public String name = "Timer";

    /**
     * Uses a stopwatch instance to keep track of how much time has passed
     * */
    private Stopwatch stopwatch = new Stopwatch(0);

    /**
     * The time it's counting down from.
     * */
    private long countdownTime = 0;

    private boolean hasFinished = false;

    private NotificationListener listener;


    interface NotificationListener{
        void notifyTimerFinished();
    }

        /**Sets time and paused time, used when you want the stopwatch
         * to start at a certain time already
         * @param timeRunning is how much time the timer has been running for
         * @param countdownTime is the time in MS that the user inputted to countdown from
         * @param isRunning should the timer start out running or paused?*/
    Timer(long timeRunning, long countdownTime, boolean isRunning, boolean newHasFinished, NotificationListener listener){

        hasFinished = newHasFinished;
        //If given time has time running equal to countdown time given, then notification has
        //been played for timer already
        /*if(timeRunning == countdownTime){
            hasFinished = true;
        }*/
        this.countdownTime = countdownTime;
        //stopwatch = new Stopwatch(timeRunning, isRunning);
        stopwatch = new Stopwatch(countdownTime-timeRunning, isRunning);
        this.listener = listener;
    }

    /**Starts timer with given countdown time start*/
    public void start(long countdownTime){
        this.countdownTime = countdownTime;
        stopwatch.reset();
        stopwatch.start();
        hasFinished = false;
    }

    public void pause() {
        stopwatch.pause();
    }

    public void resume(){
        stopwatch.resume();
    }

    public void reset(){
        //Doesn't reset the countdown variable so it remembers what last time inputted was
        stopwatch.reset();
        hasFinished = false;
    }

    public boolean isPaused(){
        return stopwatch.isPaused();
    }

    /**
     * Returns the time that the timer is currently on in milliseconds
     * @return time in milliseconds
     * */
    public long getTime(){
        long timeRemaining = countdownTime - stopwatch.getTime();
        Log.d("TimeTime", "Time remaining: " + timeRemaining + " countdownTime: " +  countdownTime + " stopwatch time: " + stopwatch.getTime());
        if(hasFinished){
            return 0;
        }

        if(timeRemaining < 0){
            Log.d("TimeTime", "Time has reached 0 for first time");
            listener.notifyTimerFinished();
            hasFinished = true;
            return 0;
        }
        return timeRemaining;
    }

    boolean hasFinished(){
        return hasFinished;
    }

    public long getCountdownTime(){
        return countdownTime;
    }

/**
 * Converts from time in milliseconds to a String in format of time
 * @param ms time in milliseconds that will be converted to a string
 * @param showMS if true string will be in format hh:mm:ss:ms, if not then it will be in the format of hh:mm:ss
 * */
//    public String formatTime(long ms, boolean showMS){
//
//        String formattedTime = "";
//        if(TimeUnit.MILLISECONDS.toHours(ms) > 0){
//            formattedTime = formattedTime + TimeUnit.MILLISECONDS.toHours(ms) + ":";
//        }
//
//        long minutes = TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms));
//
//        long seconds = TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms));
//
//        formattedTime = formattedTime + String.format(Locale.ENGLISH,"%02d:%02d",
//                minutes,
//                seconds
//        );
//
//        long milliseconds = (ms/10) % 100;
//        if(showMS){
//            formattedTime = formattedTime + ":" + String.format(Locale.ENGLISH,"%02d", milliseconds);
//        }
//        return formattedTime;
//    }
}
