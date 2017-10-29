/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest;

import com.github.hantsy.ee8sample.domain.Comment;
import com.github.hantsy.ee8sample.domain.Slug;
import com.github.hantsy.ee8sample.repository.CommentRepository;
import static java.util.Collections.emptyList;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author hantsy
 */
//@Path("comments")
//@Stateless
public class CommentResource {

    private String slug;

    @Inject
    CommentRepository comments;

    @Context
    UriInfo uriInfo;

    public CommentResource() {
    }

    public CommentResource(String slug) {
        this.slug = slug;
    }

    @Path("")
    @GET
    public List<Comment> allCommentsOfPost() {
        return emptyList();
    }

    @Path("count")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response countOfComments() {
        return Response.ok(comments.countByPost(slug)).build();
    }

    @Path("")
    @POST
    public Response saveComment(CommentForm form) {
        Comment comment = Comment.builder().post(new Slug(slug)).content(form.getContent()).build();
        Comment saved = comments.save(comment);
        return Response
                .created(
                        uriInfo.getBaseUriBuilder()
                                .path("{commentId}")
                                .build(saved.getId())
                )
                .build();
    }

    @Path("{commentId}")
    @GET
    public Response getCommentById(@PathParam("commentId") Long commentId) {
        return comments.findOptionalById(commentId)
                .map(
                        c -> CommentDetails.builder()
                                .slug(c.getPost().getSlug())
                                .content(c.getContent())
                                //.createdBy(c.getCreatedBy())
                                .build()
                )
                .map(c -> Response.ok(c).build())
                .orElseThrow(() -> new CommentNotFoundException(commentId));

    }

    @Path("{commentId}")
    @PUT
    public Response updateComment(@PathParam("commentId") Long commentId, CommentForm form) {
        return comments.findOptionalById(commentId)
                .map(
                        c -> {
                            c.setContent(form.getContent());
                            return c;
                        }
                )
                .map(c -> comments.save(c))
                .map((Comment c) -> Response.noContent().build())
                .orElseThrow(() -> new CommentNotFoundException(commentId));

    }

    @Path("{commentId}")
    @DELETE
    public Response deleteComment(@PathParam("commentId") Long commentId) {
        return comments.findOptionalById(commentId)
                .map(c -> comments.delete(c))
                .map(c -> Response.noContent().build())
                .orElseThrow(() -> new CommentNotFoundException(commentId));

    }

}
