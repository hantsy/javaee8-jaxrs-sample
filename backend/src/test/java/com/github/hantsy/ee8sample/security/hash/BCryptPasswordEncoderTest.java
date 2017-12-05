package com.github.hantsy.ee8sample.security.hash;

import com.github.hantsy.ee8sample.security.hash.bcrypt.BCryptPasswordEncoder;
import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author hantsy
 */
public class BCryptPasswordEncoderTest {

    public BCryptPasswordEncoderTest() {
    }

    PasswordEncoder instance;

    @Before
    public void setUp() {
        this.instance = new BCryptPasswordEncoder();
    }

    @After
    public void tearDown() {
        this.instance = null;
    }

    @Test
    public void testBCryptEncodeAndMatch() {
        String rawPassword = "test";
        String encoded = this.instance.encode(rawPassword);
        boolean matched = this.instance.matches(rawPassword, encoded);

        assertTrue(matched);
    }

    @Test
    public void testNotMatched() {
        String rawPassword = "test";
        boolean matched = this.instance.matches(rawPassword, "notmatched");

        assertFalse(matched);
    }
}
