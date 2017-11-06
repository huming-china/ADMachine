package com.fgecctv.lang;

import junit.framework.Assert;

import org.junit.Test;

public class ByteArrayTest {
    @Test
    public void testToHexString() throws Exception {
        byte[] bytes = new byte[]{0x3F, 0x33, (byte) 0xE8, 0x0D, 0x13};
        String expected = "3F33E80D13";
        String actual = ByteArray.toHexString(bytes);
        Assert.assertEquals(expected, actual);
    }
}