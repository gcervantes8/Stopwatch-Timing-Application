package com.example.jerryc.stopwatchtimer;


/**
 * Created by Jerry C on 4/24/2017.
 *
 * Interface tha is implemented by Stopwatch and Timer.
 */

interface TimeInterface {

    void pause();

    boolean isPaused();

    void reset();

    void resume();

    long getCountdownTime();

    long getTime();

//    String formatTime(long time, boolean showMS);
}
