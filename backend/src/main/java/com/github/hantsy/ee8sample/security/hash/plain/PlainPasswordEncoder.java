package com.github.hantsy.ee8sample.security.hash.plain;

import com.github.hantsy.ee8sample.security.hash.PasswordEncoder;

/**
 *
 * @author hantsy
 */
public class PlainPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.equals(encodedPassword);
    }

}
