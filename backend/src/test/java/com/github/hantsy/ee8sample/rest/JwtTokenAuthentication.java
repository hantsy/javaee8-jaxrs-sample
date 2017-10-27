/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest;

import java.io.IOException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;

/**
 *
 * @author hantsy
 */
public class JwtTokenAuthentication implements ClientRequestFilter {
    
    private final String authHeader;

    public JwtTokenAuthentication(String token) {
        this.authHeader = "Bearer " + token;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, authHeader);
    }
    
}
