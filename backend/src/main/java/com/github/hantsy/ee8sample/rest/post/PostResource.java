/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest.post;

import com.github.hantsy.ee8sample.domain.repository.PostRepository;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
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
@Stateless
public class PostResource {

    @Context
    UriInfo uriInfo;

    @Context
    ResourceContext resourceContext;

    @Inject
    PostRepository posts;

    @PathParam("slug")
    String slug;

    @GET
    public Response getPostBySlug() {

        return this.posts.findBySlug(slug)
            .map(p -> Response.ok(p).build())
            .orElseThrow(() -> new PostNotFoundException(slug));
    }

    @PUT
    public Response updatePost(PostForm post) {
        return this.posts.findBySlug(slug)
            .map(
                p -> {
                    p.setTitle(post.getTitle());
                    p.setContent(post.getContent());
                    this.posts.save(p);
                    return Response.noContent().build();
                }
            )
            .orElseThrow(() -> new PostNotFoundException(slug));
    }

    @DELETE
    public Response deletePost() {
        return this.posts.findBySlug(slug)
            .map(
                p -> {
                    this.posts.delete(p);
                    return Response.noContent().build();
                }
            )
            .orElseThrow(() -> new PostNotFoundException(slug));
    }

    @Path("comments")
    public CommentsResource comments() {
        return resourceContext.getResource(CommentsResource.class);
    }

    @Path("favorites")
    public FavoritesResource favorites() {
        return resourceContext.getResource(FavoritesResource.class);
    }

}
