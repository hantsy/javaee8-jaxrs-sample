/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest;

/**
 *
 * @author hantsy
 */
public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(String slug) {
        super(String.format("post:{0} is not found", slug));
    }

}
