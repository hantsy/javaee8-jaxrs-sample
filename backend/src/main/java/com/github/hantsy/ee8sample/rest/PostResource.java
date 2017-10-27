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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import static java.util.stream.Collectors.toList;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
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

    @Path("")
    @GET
    public CompletionStage getAllPosts(@QueryParam("q") String q) {

        List<CompletableFuture<PostSummary>> stages = this.posts.findByKeyword(q)
                .stream()
                .map(PostSummary::new)
                .map(p -> p.addLink(Link.fromUriBuilder(uriInfo.getBaseUriBuilder()).rel("self").uri("posts/{slug}").build(p.getSlug())))
                .map(p -> p.addLink(Link.fromUriBuilder(uriInfo.getBaseUriBuilder()).rel("comments").uri("posts/{slug}/comments").build(p.getSlug())))
                .map(
                        p -> CompletableFuture
                                .supplyAsync(() -> this.comments.countByPost(p.getSlug()))
                                .thenApply(
                                        (cnt) -> {
                                            p.setCommentsCount(cnt);
                                            return p;
                                        }
                                )
                )
                .collect(toList());

        return CompletableFuture
                .allOf(stages.toArray(new CompletableFuture[stages.size()]))
                .thenApply(
                        v -> stages.stream()
                                .map(CompletionStage::toCompletableFuture)
                                .map(CompletableFuture::join)
                                .collect(toList())
                )
                .thenApply(PostSummaryList::new)
                .thenApply(p -> p.addLink(Link.fromUriBuilder(uriInfo.getBaseUriBuilder()).rel("self").uri("posts").build()));

    }

    @Path("{slug}")
    @GET
    public CompletionStage getPostBySlug(@PathParam("slug") String slug) {

        return CompletableFuture
                .supplyAsync(
                        () -> this.posts.findBySlug(slug)
                                .map(PostDetails::new)
                                .map(p -> p.addLink(Link.fromUriBuilder(uriInfo.getBaseUriBuilder()).rel("self").uri("posts/{slug}").build(p.getSlug())))
                                .map(p -> p.addLink(Link.fromUriBuilder(uriInfo.getBaseUriBuilder()).rel("comments").uri("posts/{slug}/comments").build(p.getSlug())))
                                .orElseThrow(() -> new PostNotFoundException(slug))
                )
                .thenCombineAsync(
                        CompletableFuture.supplyAsync(() -> this.comments.countByPost(slug)),
                        (post, cnt) -> {
                            post.setCommentsCount(cnt);
                            return post;
                        }
                )
                .thenCombineAsync(
                        CompletableFuture.supplyAsync(() -> this.favorites.postIsFavorited(slug, "user")),
                        (post, favorited) -> {
                            post.setFavorited(favorited);
                            return post;
                        }
                );

    }

    @Path("")
    @POST
    public Response savePost( PostForm post) {
        Post entity = Post.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .build();
        Post saved = this.posts.save(entity);
        return Response.created(uriInfo.getBaseUriBuilder().path("posts/{slug}").build(saved.getSlug())).build();
    }

    @Path("{slug}")
    @PUT
    public Response updatePost(@PathParam("slug") String slug,  PostForm post) {
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
        return resourceContext.initResource(new CommentResource(slug));
    }

    @Path("{slug}/favorites")
    public FavoriteResource favorites(@PathParam("slug") String slug) {
        return resourceContext.initResource(new FavoriteResource(slug));
    }

}
