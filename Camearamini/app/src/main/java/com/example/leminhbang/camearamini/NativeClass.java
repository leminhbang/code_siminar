package com.example.leminhbang.camearamini;

/**
 * Created by LE MINH BANG on 3/29/2018.
 */

public class NativeClass {
    public native static String getMessage();
    public native static int convertToGray(long rgb, long gray);
    public native static int convertToNegative(long rgb, long negative);
}
