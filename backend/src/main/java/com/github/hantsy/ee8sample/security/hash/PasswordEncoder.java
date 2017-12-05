package com.github.hantsy.ee8sample.security.hash;

/**
 *
 * @author hantsy
 */
public interface PasswordEncoder {

    public String encode(CharSequence rawPassword);

    public boolean matches(CharSequence rawPassword, String encodedPassword);

}
