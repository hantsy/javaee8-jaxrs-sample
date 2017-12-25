/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest.user;

/**
 *
 * @author hantsy
 */
public class EmailWasTakenException extends RuntimeException {

    public EmailWasTakenException(String email) {
        super(email + " was already taken by others");
    }
    
}
