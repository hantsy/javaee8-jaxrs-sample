/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.security.hash;

import com.github.hantsy.ee8sample.security.hash.plain.PlainPasswordEncoder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hantsy
 */
public class PlainPasswordEncoderTest {

    public PlainPasswordEncoderTest() {
    }

    PlainPasswordEncoder instance;

    @Before
    public void setUp() {
        this.instance = new PlainPasswordEncoder();
    }

    @After
    public void tearDown() {
        this.instance = null;
    }

    /**
     * Test of encode method, of class PlainPasswordEncoder.
     */
    @Test
    public void testEncode() {
        System.out.println("encode");
        CharSequence rawPassword = "test";
        String expResult = "test";
        String result = instance.encode(rawPassword);
        assertEquals(expResult, result);
    }

    /**
     * Test of matches method, of class PlainPasswordEncoder.
     */
    @Test
    public void testMatches() {
        System.out.println("matches");
        CharSequence rawPassword = "test";
        String encodedPassword = "test";
        boolean result = instance.matches(rawPassword, encodedPassword);
        assertTrue(result);
    }

}
