package com.example.jerryc.stopwatchtimer;

import android.graphics.Color;
import android.util.Log;

import java.util.Random;

/**
 * Created by Gerardo C on 1/15/2018.
 */

public class ColorHelper {


    /**
     * Random number generator used to get random colors
     * */
    private Random rng = new Random();

    /**
     * Keeps track of the alternating color, variable alternates between true and false,
     * toggles when a new fragment is added
     */
    private boolean alternating = false;



    /**
     * Returns all the colors specified in settings
     * */
    Integer[] getSettingColors(){
        Integer alternateColor = Settings.alternatingColors ? 1 : 0;
        return new Integer[]{Settings.bgColor, Settings.textColor, Settings.textShadowColor, alternateColor};
    }

    /**
     * Generates colors
     * */
    int[] generateColors(Integer[] colors){
        Integer bgColor = colors[0];
        Integer textColor = colors[1];
        Integer textShadowColor = colors[2];
        Integer alternateColor = colors[3];

        if(bgColor == null){
            bgColor = getRandomColor();
        }

        if(alternateColor == 1){
            bgColor = alternateColor(bgColor);
        }

        if(textColor == null){
            textColor = makeTextColor(bgColor);
        }

        if(textShadowColor == null){
            textShadowColor = getOppositeColor(textColor);
        }

        return new int[]{bgColor, textColor, textShadowColor};
    }

    /**
     * Generates random design colors
     * */
    int[] generateRandomColors(){
        return generateColors(new Integer[]{null, null, null, 0});
    }


    /**Changes the color slightly if the global variable alternating color is true
     * and then reverses/toggles the boolean value of alternating,
     * so if alternating was true then changes to false*/
    private int alternateColor(int color){
        final int colorChange = 10;
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int[] colorComponents = new int[]{red,green,blue};

        if(alternating){
            for(int i = 0; i < colorComponents.length; i++) {
                if (colorComponents[i] > 126) {
                    colorComponents[i] -= colorChange;
                } else {
                    colorComponents[i] += colorChange;
                }
            }
        }
        alternating = !alternating;

        Log.d("TimeTime", "Returned alternating color");
        return Color.argb(255, colorComponents[0], colorComponents[1], colorComponents[2]);
    }

    /**
     * Returns the inverse of a color, if the color given is white then returns black
     * */
    private int getOppositeColor(int color){
        if(color == Color.WHITE){
            Log.d("TimeTime", "Shadow color: black");
            return Color.BLACK;
        }
        else if(color == Color.BLACK){
            Log.d("TimeTime", "Shadow color: white");
            return Color.WHITE;
        }
        int darkColor = Color.WHITE;

        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        Log.d("TimeTime", "Color was not white or black");
        return Color.argb(255, Color.red(darkColor)-red,Color.green(darkColor)-green,Color.blue(darkColor)-blue);
    }


    /**
     * @param color is an int generated using the Android Color class
     * Color.luminance requires API level min to be 21, so made custom method to find if the color is dark color for more compatibility
     * @return boolean true if the color given was a dark color, false if it was a light color
     * */
    boolean isDarkColor(int color){
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        //Gets avg of color
        int avgColor = (red+green+blue)/3;

        return avgColor < 128;
    }


    /**
     * @param bgColor is the background color of the fragment
     * If that background is found to be dark color, return white color for text.
     * If the background is found to be a light color, returns a black color for text.
     * @return black or white color from the Color class
     * */
    private int makeTextColor(int bgColor){
        int color;

        if(isDarkColor(bgColor)){
            color = Color.WHITE;
            Log.d("TimeTime", "Chosen text color: white");
        }
        else{
            color = Color.BLACK;
            Log.d("TimeTime", "Chosen text color: black");
        }
        return color;
    }


    /**
     * @param lightColor determines whether it will return a dark color or a light color
     * Returns a dark color or a light color from the Color class.
     * */
    private int getRandomColor(boolean lightColor){
        final int colorHalf = 127; //Colors are between 0 to 255, this is the half point
        int balance = 20; //Balance, makes it so color isn't too close between light and dar
        int red = rng.nextInt(colorHalf-balance);
        int green = rng.nextInt(colorHalf-balance);
        int blue = rng.nextInt(colorHalf-balance);
        int startIndex = 0;
        if (lightColor){
            startIndex = colorHalf+balance; //Should be 128, but we prefer to pick an even lighter color
        }

        return Color.argb(255, startIndex + red, startIndex + green, startIndex + blue);
    }

    /**
     * Returns a random color from the Color class
     * */
    public int getRandomColor(){
        return Color.argb(255, rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
    }

    public void randomizeDesignColors(){

    }
}
