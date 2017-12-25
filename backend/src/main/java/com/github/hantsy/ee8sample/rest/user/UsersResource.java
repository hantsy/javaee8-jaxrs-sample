/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest.user;

import com.github.hantsy.ee8sample.domain.Count;
import com.github.hantsy.ee8sample.domain.Existed;
import com.github.hantsy.ee8sample.domain.User;
import com.github.hantsy.ee8sample.domain.repository.UserRepository;
import com.github.hantsy.ee8sample.security.hash.Crypto;
import static com.github.hantsy.ee8sample.security.hash.Crypto.Type.BCRYPT;
import com.github.hantsy.ee8sample.security.hash.PasswordEncoder;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.ok;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author hantsy
 */
@Path("users")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
public class UsersResource {

    @Context
    UriInfo uriInfo;

    @Inject
    UserRepository users;

    @Context
    ResourceContext resourceContext;

    @Inject
    SecurityContext securityContext;

    @Inject
    @Crypto(BCRYPT)
    PasswordEncoder passwordEncoder;

    @GET
    public Response all() {
        return ok(users.findAll()).build();
    }

    @GET
    @Path("count")
    public Response count() {
        return ok(
            Count.builder().count(users.stream().count()).build()
        ).build();
    }

    @GET
    @Path("exists")
    public Response exists(@QueryParam("username") String username, @QueryParam("email") String email) {
        if (username != null && username.length() > 0) {
            return ok(Existed.builder().existed(users.findByUsername(username).isPresent()).build()).build();
        }

        if (email != null && email.length() > 0) {
            return ok(Existed.builder().existed(users.findByEmail(email).isPresent()).build()).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).entity("username or email query params is required").build();
    }

    @POST
    // there is a bug when adding @Valid to request form data
    // https://github.com/javaee/glassfish/issues/22317
    public Response createUser(RegisterForm form) {

        if (users.findByUsername(form.getUsername()).isPresent()) {
            throw new UsernameWasTakenException(form.getUsername());
        }

        if (users.findByEmail(form.getEmail()).isPresent()) {
            throw new EmailWasTakenException(form.getEmail());
        }

        User user = User.builder()
            .username(form.getUsername())
            .password(passwordEncoder.encode(form.getPassword()))
            .firstName(form.getFirstName())
            .lastName(form.getLastName())
            .email(form.getEmail())
            .build();

        User saved = users.save(user);
        return Response.created(uriInfo.getBaseUriBuilder().path("users/{username}").build(saved.getUsername())).build();
    }

    @Path("{username}")
    public UserResource user() {
        return resourceContext.getResource(UserResource.class);
    }

}
