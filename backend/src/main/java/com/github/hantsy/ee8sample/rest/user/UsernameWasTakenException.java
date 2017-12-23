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
public class UsernameWasTakenException extends RuntimeException {

    public UsernameWasTakenException(String username) {
        super(username + " was taken by others");
    }

}
