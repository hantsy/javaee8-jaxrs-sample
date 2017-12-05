/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest;

import static com.github.hantsy.ee8sample.Constants.ROLE_ADMIN;
import static com.github.hantsy.ee8sample.Constants.ROLE_USER;
import com.github.hantsy.ee8sample.domain.Post;
import com.github.hantsy.ee8sample.repository.CommentRepository;
import com.github.hantsy.ee8sample.repository.FavoriteRepository;
import com.github.hantsy.ee8sample.repository.PostRepository;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author hantsy
 */
@Path("posts")
@Stateless
public class PostResource {

    @Context
    UriInfo uriInfo;

    @Context
    ResourceContext resourceContext;

    @Inject
    PostRepository posts;

    @Inject
    CommentRepository comments;

    @Inject
    FavoriteRepository favorites;

    @GET
    public Response getAllPosts(
            @QueryParam("q") String q,
            @QueryParam("limit") @DefaultValue("10") int limit,
            @QueryParam("offset") @DefaultValue("0") int offset
    ) {

        return Response.ok(this.posts.findByKeyword(q, limit, offset)).build();
    }

    @Path("{slug}")
    @GET
    public Response getPostBySlug(@PathParam("slug") String slug) {

        return this.posts.findBySlug(slug)
                .map(p -> Response.ok(p).build())
                .orElseThrow(() -> new PostNotFoundException(slug));
    }

    @POST
    public Response savePost(PostForm post) {
        Post entity = Post.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .build();
        Post saved = this.posts.save(entity);
        return Response.created(uriInfo.getBaseUriBuilder().path("posts/{slug}").build(saved.getSlug())).build();
    }

    @Path("{slug}")
    @PUT
    public Response updatePost(@PathParam("slug") String slug, PostForm post) {
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

    @Path("{slug}")
    @DELETE
    public Response deletePost(@PathParam("slug") String slug) {
        return this.posts.findBySlug(slug)
                .map(
                        p -> {
                            this.posts.delete(p);
                            return Response.noContent().build();
                        }
                )
                .orElseThrow(() -> new PostNotFoundException(slug));
    }

    @Path("{slug}/comments")
    public CommentResource comments(@PathParam("slug") String slug) {
        //return resourceContext.initResource(new CommentResource());
        return resourceContext.getResource(CommentResource.class);
    }

    @Path("{slug}/favorites")
    public FavoriteResource favorites(@PathParam("slug") String slug) {
        //return resourceContext.initResource(new FavoriteResource());
        return resourceContext.getResource(FavoriteResource.class);
    }

}
