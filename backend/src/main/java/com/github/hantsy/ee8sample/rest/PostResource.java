/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest;

import com.github.hantsy.ee8sample.domain.Post;
import com.github.hantsy.ee8sample.repository.CommentRepository;
import com.github.hantsy.ee8sample.repository.FavoriteRepository;
import com.github.hantsy.ee8sample.repository.PostRepository;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.validation.Valid;
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

    @Inject
    SecurityContext securityContext;

    @Path("")
    @GET
    public Response getAllPosts(@QueryParam("q") String q) {
        return Response.ok(posts.findByKeyword(q))
                .link(uriInfo.getBaseUriBuilder().path("/posts/comment").build(), "comment")
                .build();
    }

    @Path("{slug}")
    @GET
    public CompletionStage getPostBySlug(@PathParam("slug") String slug) {

        return CompletableFuture
                .supplyAsync(
                        () -> this.posts.findBySlug(slug)
                                .map(
                                        p -> new PostDetails(p, uriInfo)
                                )
                                .orElseThrow(() -> new PostNotFoundExeception(slug))
                )
                .thenCombineAsync(
                        CompletableFuture.supplyAsync(() -> this.comments.countByPost(slug)),
                        (post, cnt) -> {
                            post.setCommentsCount(cnt);
                            return post;
                        }
                )
                .thenCombineAsync(
                        CompletableFuture.supplyAsync(() -> this.favorites.postIsFavorited(slug, securityContext.getCallerPrincipal().getName())),
                        (post, favorited) -> {
                            post.setFavorited(favorited);
                            return post;
                        }
                );

    }

    @Path("")
    @POST
    public Response savePost(@Valid PostForm post) {
        Post entity = Post.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .build();
        Post saved = this.posts.save(entity);
        return Response.created(uriInfo.getBaseUriBuilder().path("posts/{slug}").build(saved.getSlug())).build();
    }

    @Path("{slug}")
    @PUT
    public Response updatePost(@PathParam("slug") String slug, @Valid PostForm post) {
        return this.posts.findBySlug(slug)
                .map(
                        p -> {
                            p.setTitle(post.getTitle());
                            p.setContent(post.getContent());
                            this.posts.delete(p);
                            return Response.noContent().build();
                        }
                )
                .orElseThrow(() -> new PostNotFoundExeception(slug));
    }

    @Path("{slug}")
    @PUT
    public Response deletePost(@PathParam("slug") String slug) {
        return this.posts.findBySlug(slug)
                .map(
                        p -> {
                            this.posts.delete(p);
                            return Response.noContent().build();
                        }
                )
                .orElseThrow(() -> new PostNotFoundExeception(slug));
    }

    @Path("{slug}/comments")
    public CommentResource comments(@PathParam("slug") String slug) {
        return resourceContext.initResource(new CommentResource(slug));
    }

}
