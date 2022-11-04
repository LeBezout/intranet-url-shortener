package com.github.lebezout.urlshortener.utils;

import lombok.experimental.UtilityClass;

import java.text.DecimalFormat;
import java.text.NumberFormat;

@UtilityClass
public class CounterFormater {

    /**
     * Format the counter value with our default format
     * @param value counter value
     * @return a string formatted with {@code #00,000,000} pattern
     */
    public static String format(long value) {
        final NumberFormat format = new DecimalFormat("#00,000,000");
        return format.format(value);
    }
}
