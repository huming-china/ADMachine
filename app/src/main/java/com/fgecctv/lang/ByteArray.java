package com.fgecctv.lang;

public class ByteArray {
    private final static char[] digits = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F'
    };

    public static String toHexString(byte[] bytes) {
        final char[] chars = new char[bytes.length * 2];
        final int mask = 0xF;

        for (int i = 0; i < chars.length; i++) {
            final int shift = (i + 1) % 2 * 4;
            chars[i] = digits[bytes[i / 2] >>> shift & mask];
        }

        return new String(chars);
    }
}
