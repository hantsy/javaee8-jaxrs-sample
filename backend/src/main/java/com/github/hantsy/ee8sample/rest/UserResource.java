/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest;

import com.github.hantsy.ee8sample.domain.User;
import com.github.hantsy.ee8sample.repository.UserRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author hantsy
 */
@Path("users")
@RequestScoped
//@Consumes(MediaType.APPLICATION_JSON)
//@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Context
    UriInfo uriInfo;

    @Inject
    UserRepository users;

    @Resource
    ManagedExecutorService executor;

//    @Inject
//    SecurityContext securityContext;

    @GET
    public CompletionStage<List<User>> allUsers() {
        return CompletableFuture.supplyAsync(
                () -> users.findAll(), executor
        );
    }

//    @GET
//    @Path("{username}")
//    //@RolesAllowed({ROLE_USER, ROLE_ADMIN})
//    public Response getUser(@PathParam("username") String username) {
//
//        if (securityContext.getCallerPrincipal() != null
//                && securityContext.getCallerPrincipal().getName().equals(username)) {
//            return users.findByUsername(username)
//                    .map(
//                            u -> Response.ok(u)
//                                    .link(uriInfo.getBaseUriBuilder().path("users/{username}").build(username), "self")
//                                    .build()
//                    )
//                    .orElse(Response.status(Response.Status.NOT_FOUND).build());
//        }
//
//        return Response.status(Response.Status.UNAUTHORIZED).build();
//    }

}
