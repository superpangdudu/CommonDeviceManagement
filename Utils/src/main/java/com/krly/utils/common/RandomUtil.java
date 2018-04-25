package com.krly.utils.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RandomUtil {
    private static final int BOUND = 62;
    private static final List<String> mBaseStringList = new ArrayList<>(BOUND);

    static {
        for (int n = 0; n < 10; n++)
            mBaseStringList.add(new String(new byte[] {(byte) ('0' + n)}));
        for (int n = 0; n < 26; n++)
            mBaseStringList.add(new String(new byte[] {(byte) ('A' + n)}));
        for (int n = 0; n < 26; n++)
            mBaseStringList.add(new String(new byte[] {(byte) ('a' + n)}));
    }

    public static String getRandomString(int length) {
        StringBuilder sb = new StringBuilder();

        Random random = new Random();
        for (int n = 0; n < length; n++)
            sb.append(mBaseStringList.get(random.nextInt(BOUND)));

        return sb.toString();
    }

    public static String getRandomNumber(int length) {
        StringBuilder sb = new StringBuilder();

        Random random = new Random();
        for (int n = 0; n < length; n++)
            sb.append(mBaseStringList.get(random.nextInt(9)));

        return sb.toString();
    }
}
