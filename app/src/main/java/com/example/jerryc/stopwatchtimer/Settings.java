package com.example.jerryc.stopwatchtimer;


/**
 * Created by Jerry C on 4/30/2017.
 *
 *
 * Note: If a new setting is added, make sure it is also saved and loaded using shared preferences
 * This class contains all the settings that user has saved
 * Settings are applied only when user selects a s
 * */

class Settings {





    /**
     * The color of the layout that the fragment will be in
     * */
    static Integer bgColor;

    /**
     * The color the text
     * */
    static Integer textColor;

    /**
     * The color of the text shadow
     * */
    static Integer textShadowColor;

    /**
     * Setting says if milliseconds should be displayed on the fragments
     * */
    static Boolean showMS = false;

    /**
     * If setting is true, then the design of the fragments will randomize every time the phone is shaken
     * */
    static Boolean randomizeOnShake = false;

    /**If true then if you use solid colors, after every color it will alternate each one and make
     * the other color a darker tint, this is to differentiate colors*/
    static Boolean alternatingColors = true;

    /**If true then when a timer is finished then there will be a notification*/
    static Boolean timerNotifications = true;
}
