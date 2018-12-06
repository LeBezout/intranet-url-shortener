package com.github.lebezout.urlshortener.utils;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class IdGeneratorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdGeneratorTest.class);

    private static final IdGenerator DEFAULT_GENERATOR = new IdGenerator(new char[] {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
    });

    @Test
    public void test_generate_fixed_length() {
        String id = DEFAULT_GENERATOR.generate(7);
        LOGGER.debug(id);
        Assert.assertEquals(7, id.length());

        IntStream.range(0, 10).forEach(i -> LOGGER.debug(DEFAULT_GENERATOR.generate(7)));
    }

    @Test
    public void test_generate_bounded_length() {
        String id = DEFAULT_GENERATOR.generate(3, 7);
        LOGGER.debug(id);
        Assert.assertTrue(id.length() >= 3 && id.length() < 8);
    }

    @Test
    public void test_WrappedCharacters() {
        Character[] arr = { Character.valueOf('0'), Character.valueOf('A'), Character.valueOf('a') };
        IdGenerator generator = new IdGenerator(arr);
        String id = generator.generate(7);
        Assert.assertEquals(7, id.length());
        LOGGER.debug(id);
    }


    @Test
    @org.junit.Ignore
    public void test_if_collisions() {
        final int SIZE = 50000;
        List<String> history = new ArrayList<>(SIZE);
        IntStream.range(0, SIZE).forEach(i -> checkCollision(i, DEFAULT_GENERATOR, history));
    }

    private static void checkCollision(int current, IdGenerator generator, List<String> history) {
        String id = generator.generate(7);
        if (history.contains(id)) {
            LOGGER.error("# WARN # [" +current + "] " + id + " already generated !");
        } else {
            history.add(id);
        }
    }

}
