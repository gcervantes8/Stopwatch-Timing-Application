package com.example.jerryc.stopwatchtimer;


/**
 * Created by Gerardo C on 1/27/2018.
 *
 * Handles time operations, like parsing the time as a string or formatting the time from miliseconds
 */

public class TimeOperations {

    public TimeOperations() {
    }

    /**
     * Formats the time given in ms to a string
     *
     * @param time is in ms, converts how much time has passed in ms to a formatted string
     * @param showMS if true displays the amount of milliseconds, otherwise doesn't
     * @param useDecimalForMS separates the amount of milliseconds and seconds by a "." instead of a ":"
     * @param shortenTime if true, then returned string will be shorter
     *                    ex. instead of hh:mm.ss, it will return mm:ss if hours is 0, h:mm:ss if hours less than 10
     * @return a readable String from the time parameter
     * One possible format is: "hh:mm:ss.ms", when showMS is true, useDecimal is true, shortenTime is false
     */
    public String formatTime(long time, boolean showMS, boolean useDecimalForMS, boolean shortenTime) {

        String formattedTime = "";

        String msSeparator = ":";
        String otherTimeSeparators = ":";

        if(useDecimalForMS){
            msSeparator = ".";
        }

        time /= 10;
        long ms = time % 100;
        time /= 100;
        long seconds = time % 60;
        time /= 60;
        long minutes = time % 60;
        time /= 60;
        long hours = time % 60;



        if(showMS){
            formattedTime = msSeparator + convertTime(ms);
        }

        if(minutes == 0 && shortenTime){
            return seconds + formattedTime;
        }
        formattedTime = convertTime(seconds) + formattedTime;

        if(hours == 0 && shortenTime){
            return minutes + otherTimeSeparators + formattedTime;
        }

        formattedTime = convertTime(minutes) + otherTimeSeparators + formattedTime;

        if (hours == 0) {
            return formattedTime;
        }
        formattedTime = convertTime(hours) + otherTimeSeparators + formattedTime;


        return formattedTime;
    }

    /**
     * Converts time to string, string will have at least 2 char
     * If time is between 0 to 9 then returns that time as string with leading 0, ex. convertTime(5) returns "05"
     * Otherwise returns the time as a string, ex. convertTime(20) returns "20"
     *
     * @return the time given as a String, with leading 0 if time is less than 9
     */
    private String convertTime(long time) {
        if (time <= 9) {
            return ("0" + time);
        }
        return "" + time;
    }

    /**
     * Currently doesn't handle msSupport
     * @return time in millisecond that was inputted
     * */
    public long parseTimeInput(String timeInputted, boolean msSupport){
        long totalTime = 0;

        //In centisecond
        int timeConstraintIn = 10;
        Integer firstDigit = null;
        Integer secondDigit;
        boolean readMS = !msSupport;
        if(!msSupport){
            //Changes to seconds
            timeConstraintIn = 1000;
        }

        for(int i = timeInputted.length() - 1; i >= 0; i--){

            char c = timeInputted.charAt(i);
            if(c == ':'){

                //If haven't taken into account the firstDigit found and found a ':' symbol, then add it to total time
                if(firstDigit != null){
                    totalTime += timeConstraintIn * firstDigit;
                }
                //If done reading milliseconds then, transition to seconds and above
                if(readMS) {
                    timeConstraintIn *= 60;
                }
                else{
                    timeConstraintIn *= 100;
                    readMS = true;
                }
                firstDigit = null;
            }

            //If character is a digit
            if(Character.isDigit(c)){
                //If this is the first digit we found set it as the first digit
                if(firstDigit == null) {
                    firstDigit = Character.getNumericValue(c);
                }
                else{
                    //Set as second digit
                    secondDigit = Character.getNumericValue(c);
                    //Convert to a 2 digit number
                    int timeSegment = firstDigit + (secondDigit*10);
                    //Add to total time
                    totalTime += timeConstraintIn * timeSegment;
                    firstDigit = null;
                }
            }


            //If in seconds we add 1000 for every second
            //If in minutes we add 60000 for every minute
            //If in hours we add 3600000 for every hour
        }

        //If there is a digit we didn't take into account, add it to total time
        if(firstDigit != null){
            totalTime += firstDigit * timeConstraintIn;
        }

        return totalTime;
    }
}
