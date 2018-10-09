package com.example.jerryc.stopwatchtimer;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Gerardo Cervantes on 4/26/2017.
 *
 *
 * FragmentInfo save information about the fragment so that it can be recreated when an orientation change happens
 * Implements Parcelable so that the FragmentInfo can be sent through the Bundle
 */

class FragmentInfo implements Parcelable {


    /**
     * The layout/background color that will be used by the fragment
     * */
    private Integer layoutColor;

    /**The color of the text.
     * Uses the Color class to create the color*/
    private Integer textColor = Color.BLACK;

    /**The color of the text shadow.
     * Uses the Color class to create the color*/
    private Integer textShadowColor = Color.WHITE;

    /**
     * The type fragment it is, it could either be Timer.name or Stopwatch.name
     * */
    private String type;

    /**
     * For timer stores how much time is remaining
     * For stopwatch it stores how much time has passed
     * */
    private Long time = 60000L;

    /**
     * Stores what timer countdown was set to
     * */
    private Long countdownTime = 0L;

    /**
     * If the fragment was running, then is true
     * So if the stopwatch or timer were paused, then it should be false
     * */
    private boolean isRunning = false;

    /**
     * Is true if the timer has finished playing and should not play notification again
     * */
    private boolean hasFinished = false;



    FragmentInfo(int layoutC, String type, int textC, int textShadowC) {
        layoutColor = layoutC;
        this.type = type;
        setColor(textC, textShadowC);
    }

    public void setTime(long newTime) {
        time = newTime;
    }

    void setTime(long running, long countdown, boolean isRunning){
        time = running;
        countdownTime = countdown;
        this.isRunning = isRunning;
    }

    void setCountdownTime(long newCountdownTime){
        countdownTime = newCountdownTime;
    }

    void setColor(int newTextColor, int newShadowColor){
        textColor = newTextColor;
        textShadowColor = newShadowColor;
    }

    void setColor(int newLayoutColor, int newTextColor, int newShadowColor){
        layoutColor = newLayoutColor;
        textColor = newTextColor;
        textShadowColor = newShadowColor;
    }

    long getCountdownTime(){
        return countdownTime;
    }

    Integer getLayoutColor(){
        return layoutColor;
    }

    Integer getTextColor(){
        return textColor;
    }

    Integer getTextShadowColor(){
        return textShadowColor;
    }

    String getType(){
        return type;
    }

    Long getTime(){
        return time;
    }

    boolean isRunning(){
        return isRunning;
    }

    boolean hasFinished(){
        return hasFinished;
    }

    void setHasFinished(boolean newHasFinished){
        hasFinished = newHasFinished;
    }




/**
 * The next lines of code are done to be able to implement Parcelable.
 * They were taken from Android studio documentation and are done so that the FragmentInfo object
 * can be sent through bundle as a Parcelable object.  This was done to help make it easier to
 * handle orientation change and restoring the fragments.
 *
 * Parcelable was chosen over Serializable because Parcelable is known to be faster, with the downside
 * that the code below this has to be added.
 *
 * */

    private int mData;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mData);
    }

    public static final Parcelable.Creator<FragmentInfo> CREATOR
            = new Parcelable.Creator<FragmentInfo>() {
        public FragmentInfo createFromParcel(Parcel in) {
            return new FragmentInfo(in);
        }

        public FragmentInfo[] newArray(int size) {
            return new FragmentInfo[size];
        }
    };

    private FragmentInfo(Parcel in) {
        mData = in.readInt();
    }




}
