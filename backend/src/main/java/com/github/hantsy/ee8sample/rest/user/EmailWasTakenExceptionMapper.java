/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest.user;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author hantsy
 */
@Provider
public class EmailWasTakenExceptionMapper implements ExceptionMapper<EmailWasTakenException> {

    @Override
    public Response toResponse(EmailWasTakenException exception) {
        return Response.status(Response.Status.CONFLICT)
            .entity(exception.getMessage())
            .build();
    }

}