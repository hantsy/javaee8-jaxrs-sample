/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest.user;

import com.github.hantsy.ee8sample.domain.repository.FavoriteRepository;
import com.github.hantsy.ee8sample.domain.repository.PostRepository;
import com.github.hantsy.ee8sample.domain.repository.UserRepository;
import com.github.hantsy.ee8sample.security.hash.Crypto;
import static com.github.hantsy.ee8sample.security.hash.Crypto.Type.BCRYPT;
import com.github.hantsy.ee8sample.security.hash.PasswordEncoder;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author hantsy
 */
@Path("user")
@Stateless
public class CurrentUserResource {

    @Context
    UriInfo uriInfo;

    @Inject
    UserRepository users;

    @Inject
    PostRepository posts;

    @Inject
    FavoriteRepository favorites;

    @Context
    ResourceContext resourceContext;

    @Inject
    SecurityContext securityContext;

//    @Inject
//    @Crypto(BCRYPT)
//    PasswordEncoder passwordEncoder;
    @GET
    @Path("profile")
    public Response user() {
        return users.findByUsername(securityContext.getCallerPrincipal().getName())
            .map(p -> ok(p).build())
            .orElse(status(Response.Status.NOT_FOUND).build());

    }

    @GET
    @Path("favorites")
    public Response favoritedPosts() {
        List<String> slugs = favorites.findByUsername(securityContext.getCallerPrincipal().getName())
            .stream().map(f -> f.getPost().getSlug()).collect(toList());
        return ok(slugs).build();
    }

    @GET
    @Path("posts")
    public Response posts() {
        return ok(
            posts.findByCreatedBy(securityContext.getCallerPrincipal().getName())
        ).build();
    }

}
