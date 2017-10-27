/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author hantsy
 */
@Provider
public class PostNotFoundExceptionMapper implements ExceptionMapper<PostNotFoundException> {

    @Override
    public Response toResponse(PostNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
            .entity(exception.getMessage())
            .build();
    }

}