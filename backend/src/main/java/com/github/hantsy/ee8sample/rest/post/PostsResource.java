/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest.post;

import com.github.hantsy.ee8sample.domain.Count;
import com.github.hantsy.ee8sample.domain.Post;
import com.github.hantsy.ee8sample.domain.repository.PostRepository;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
public class PostsResource {

    @Context
    UriInfo uriInfo;

    @Context
    ResourceContext resourceContext;

    @Inject
    PostRepository posts;

    @GET
    public Response getAllPosts(
        @QueryParam("q") String q,
        @QueryParam("limit") @DefaultValue("10") int limit,
        @QueryParam("offset") @DefaultValue("0") int offset
    ) {
        return Response.ok(this.posts.findByKeyword(q, limit, offset)).build();
    }

    @GET
    @Path("count")
    public Response getAllPosts(@QueryParam("q") String q) {
        return Response.ok(
            Count.builder().count(this.posts.countByKeyword(q))
        ).build();
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
    public PostResource postResource() {
        return resourceContext.getResource(PostResource.class);
    }

}
