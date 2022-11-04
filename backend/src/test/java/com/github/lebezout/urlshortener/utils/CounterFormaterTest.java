package com.github.lebezout.urlshortener.utils;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class CounterFormaterTest {

    @Test
    void test_01_units() {
        Assertions.assertEquals("00 000 001", CounterFormater.format(1L));
    }

    @Test
    void test_02_tens() {
        Assertions.assertEquals("00 000 010", CounterFormater.format(10L));
    }

    @Test
    void test_03_hundreds() {
        Assertions.assertEquals("00 000 100", CounterFormater.format(100L));
    }

    @Test
    void test_04_thousands() {
        Assertions.assertEquals("00 001 000",CounterFormater.format(1_000L));
    }

    @Test
    void test_05_tens_of_thousands() {
        Assertions.assertEquals("00 010 000", CounterFormater.format(10_000L));
    }

    @Test
    void test_06_hundreds_of_thousands() {
        Assertions.assertEquals("00 100 000", CounterFormater.format(100_000L));
    }

    @Test
    void test_07_millions() {
        Assertions.assertEquals("01 000 000", CounterFormater.format(1_000_000L));
    }
}
