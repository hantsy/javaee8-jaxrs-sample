/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest.user;

import com.github.hantsy.ee8sample.domain.User;
import com.github.hantsy.ee8sample.domain.repository.UserRepository;
import com.github.hantsy.ee8sample.security.hash.Crypto;
import static com.github.hantsy.ee8sample.security.hash.Crypto.Type.BCRYPT;
import com.github.hantsy.ee8sample.security.hash.PasswordEncoder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author hantsy
 */
@Path("users")
@Stateless
public class UsersResource {

    @Context
    UriInfo uriInfo;

    @Inject
    UserRepository users;

    @Resource
    ManagedExecutorService executor;

    @Context
    ResourceContext resourceContext;
    
    @Inject 
    @Crypto(BCRYPT)
    PasswordEncoder passwordEncoder;
    
    @GET
    public CompletionStage<List<User>> allUsers() {
        return CompletableFuture.supplyAsync(
            () -> users.findAll(), executor
        );
    }

    @POST
    public Response createUser(@Valid RegisterForm form) {
        
        if(users.findByUsername(form.getUsername()).isPresent()){
            throw new UsernameWasTakenException(form.getUsername());
        }
        
        if(users.findByEmail(form.getEmail()).isPresent()){
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
