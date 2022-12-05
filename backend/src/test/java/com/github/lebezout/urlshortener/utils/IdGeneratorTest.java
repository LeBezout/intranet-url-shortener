package com.github.lebezout.urlshortener.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

class IdGeneratorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdGeneratorTest.class);

    private static final IdGenerator ID_GENERATOR = new IdGenerator(5, new char[] {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
    });

    @Test
    void test_generate_fixed_length() {
        String id = ID_GENERATOR.generate(7);
        LOGGER.debug(id);
        Assertions.assertEquals(7, id.length());

        IntStream.range(0, 10).forEach(i -> LOGGER.debug(ID_GENERATOR.generate(7)));
    }

    @Test
    void test_generate_bounded_length() {
        String id = ID_GENERATOR.generate(3, 7);
        LOGGER.debug(id);
        Assertions.assertTrue(id.length() >= 3 && id.length() < 8);
    }

    @Test
    void test_WrappedCharacters() {
        Character[] arr = { Character.valueOf('0'), Character.valueOf('A'), Character.valueOf('a') };
        IdGenerator generator = new IdGenerator(5, arr);
        String id = generator.generate(7);
        Assertions.assertEquals(7, id.length());
        LOGGER.debug(id);
    }

    @Test
    void test_if_collisions() {
        final int SIZE = 20_000;
        List<String> history = new ArrayList<>(SIZE);
        IntStream.range(0, SIZE).forEach(i -> checkCollision(i, history));
    }

    private static void checkCollision(int current, List<String> history) {
        String id = ID_GENERATOR.generate(7);
        if (history.contains(id)) {
            throw new IllegalStateException("# WARN # [" +current + "] " + id + " already generated !");
        } else {
            history.add(id);
        }
    }
}
