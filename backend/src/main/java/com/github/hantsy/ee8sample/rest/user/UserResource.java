/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest.user;

import com.github.hantsy.ee8sample.domain.repository.UserRepository;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author hantsy
 */
@Stateless
public class UserResource {

    @Context
    UriInfo uriInfo;

    @Inject
    UserRepository users;

    @Inject
    SecurityContext securityContext;

    @PathParam("username")
    String username;

    @GET
    public Response get() {

        return users.findByUsername(username)
            .map(
                u -> Response.ok(u)
                    .link(uriInfo.getBaseUriBuilder().path("users/{username}").build(username), "self")
                    .build()
            )
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    public Response update(UserForm form) {

        if (securityContext.getCallerPrincipal() != null
            && securityContext.getCallerPrincipal().getName().equals(username)) {
            return users.findByUsername(username)
                .map(
                    u -> {
                        u.setFirstName(form.getFirstName());
                        u.setLastName(form.getLastName());
                        u.setEmail(form.getEmail());

                        users.save(u);
                        return Response.noContent().build();
                    }
                )
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        }

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @DELETE
    public Response delete() {

        return users.findByUsername(username)
            .map(
                u -> {
                    users.delete(u);
                    return Response.noContent().build();
                }
            )
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

}
