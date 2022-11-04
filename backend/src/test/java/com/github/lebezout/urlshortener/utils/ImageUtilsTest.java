package com.github.lebezout.urlshortener.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class ImageUtilsTest {
    private static final Path OUTPUT_ROOT_DIR = Paths.get("target/junit/ImageUtils");
    @BeforeAll
    static void init() throws IOException {
        Files.createDirectories(OUTPUT_ROOT_DIR);
    }

    @Test
    void test_pixel_blue() throws IOException {
        String color = "0074CC";
        byte[] data = ImageUtils.pixel(color);
        Files.write(OUTPUT_ROOT_DIR.resolve("pixel_blue.png"), data);
        Assertions.assertTrue(Files.exists(OUTPUT_ROOT_DIR.resolve("pixel_blue.png")));
    }

    @Test
    void test_fromText() throws IOException {
        String text = "10 789 456";
        String textColor = "2D3362";
        String bgColor = "000";
        byte[] data = ImageUtils.fromText(text, 22, true, textColor, bgColor);
        Files.write(OUTPUT_ROOT_DIR.resolve("text_bold.png"), data);
        Assertions.assertTrue(Files.exists(OUTPUT_ROOT_DIR.resolve("text_bold.png")));
        data = ImageUtils.fromText(text, 22, false, textColor, bgColor);
        Files.write(OUTPUT_ROOT_DIR.resolve("text_plain.png"), data);
        Assertions.assertTrue(Files.exists(OUTPUT_ROOT_DIR.resolve("text_plain.png")));
    }
}
