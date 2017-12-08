/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest;

import com.github.hantsy.ee8sample.security.Authenticated;
import com.github.hantsy.ee8sample.security.UserInfo;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 *
 * @author hantsy
 */
@Path("auth")
@RequestScoped
public class AuthResource {

    @Inject
    Logger LOGGER;

    @Inject
    private SecurityContext securityContext;

    @Inject
    @Authenticated
    UserInfo userInfo;

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login() {
        LOGGER.log(Level.INFO, "login");
        if (securityContext.getCallerPrincipal() != null) {
            JsonObject result = Json.createObjectBuilder()
                .add("user", securityContext.getCallerPrincipal().getName())
                .build();
            return Response.ok(result).build();
        }
        return Response.status(UNAUTHORIZED).build();
    }

    @GET
    @Path("user")
    public Response userInfo() {
        LOGGER.log(Level.INFO, "user info:{0}", userInfo);
        if (securityContext.getCallerPrincipal() != null) {
            return Response.ok(userInfo).build();
        }
        return Response.status(UNAUTHORIZED).build();
    }

}
