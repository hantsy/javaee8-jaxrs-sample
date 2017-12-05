/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample;

/**
 *
 * @author hantsy
 */
public class Constants {

    private Constants() {
    }

    // two roles will be used in this applicaiton.
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static final String API_PREFIX = "/api";
    public static final String APPLICATION_VND_JSON = "application/vnd.blog-api.v1+json";

    public static final String AUTHORIZATION_PREFIX = "Bearer ";

    public static final int TOKEN_VALIDITY_SECONDS = 24 * 60 * 60; //24hrs
    public static final int REMEMBERME_VALIDITY_SECONDS = 14 * 24 * 60 * 60; //2 weeks

}
