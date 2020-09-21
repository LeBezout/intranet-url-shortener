package com.github.lebezout.urlshortener.utils;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A simple random-based ID Generator for our shortened links
 */
public class IdGenerator {
    private final char[] alphabet;

    /** Constructor with array of primitives
     * @param pChars array of chars
     */
    public IdGenerator(final char... pChars) {
        alphabet = Objects.requireNonNull(pChars, "Alphabet array cannot be null");
    }

    /** Constructor with array of wrapped characters
     * @param pChars array of characters
     */
    public IdGenerator(final Character... pChars) {
        Objects.requireNonNull(pChars, "Alphabet array cannot be null");
        alphabet = new char[pChars.length];
        for (int i = 0; i < pChars.length; i++) {
            alphabet[i] = pChars[i];
        }
    }

    /** generate a new id with a bounded length
     * @param minLength min length
     * @param maxLength max length
     * @return new Id
     */
    public String generate(final int minLength, final int maxLength) {
        final int max = alphabet.length - 1;
        final int length = computeLength(minLength, maxLength);
        // 1st random step
        final char[] shuffledAlphabet = shuffleArray(alphabet);
        // Generate (2nd random step)
        final char[] buff = new char[length];
        int count = 0;
        while (count < length) {
            // take a char in the alphabet
            buff[count++] = shuffledAlphabet[generateInt(0, max)];
        }
        return new String(buff);
    }

    private static int computeLength(final int minLength, final int maxLength) {
        if (maxLength == minLength) {
            return maxLength;
        }
        return generateInt(Math.min(maxLength, minLength), Math.max(maxLength, minLength));
    }

    /** generate a new id with a fixed length
     * @param length the fixed length
     * @return new Id
     */
    public String generate(final int length) {
        return generate(length, length);
    }

    private static int generateInt(final int min, final int max) {
        return ThreadLocalRandom.current().nextInt(1 + max - min) + min;
    }

    private static char[] shuffleArray(char... input) {
        char[] result = Arrays.copyOf(input, input.length);
        for (int i = result.length; i > 1; i--) {
            swap(result, i - 1, ThreadLocalRandom.current().nextInt(i));
        }
        return result;
    }
    private static void swap(char[] arr, int i, int j) {
        char tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
