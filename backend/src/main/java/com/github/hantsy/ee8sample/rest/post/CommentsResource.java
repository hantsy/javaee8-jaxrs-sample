/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest.post;

import com.github.hantsy.ee8sample.domain.Comment;
import com.github.hantsy.ee8sample.domain.Slug;
import com.github.hantsy.ee8sample.domain.repository.CommentRepository;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author hantsy
 */
@Stateless
public class CommentsResource {

    @PathParam("slug")
    private String slug;

    @Inject
    CommentRepository comments;

    @Context
    UriInfo uriInfo;

    public CommentsResource() {
    }

    @GET
    public Response allCommentsOfPost(
        @QueryParam("limit") @DefaultValue("50") int limit,
        @QueryParam("offset") @DefaultValue("0") int offset
    ) {
        List<CommentDetails> all = comments.findByPost(slug, limit, offset)
            .map(c -> toDetails(c))
            .collect(toList());

        return Response.ok(all).build();
    }

    @Path("count")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response countOfComments() {
        return Response.ok(comments.countByPost(slug)).build();
    }

    @POST
    public Response saveComment(CommentForm form) {
        Comment comment = Comment.builder().post(new Slug(slug)).content(form.getContent()).build();
        Comment saved = comments.save(comment);
        return Response
            .created(
                uriInfo.getBaseUriBuilder()
                    .path("/posts/{slug}/comments/{commentId}")
                    .build(slug, saved.getId())
            )
            .build();
    }

    @Path("{commentId}")
    @GET
    public Response getCommentById(@PathParam("commentId") Long commentId) {
        return comments.findOptionalById(commentId)
            .map(
                c -> toDetails(c)
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

    CommentDetails toDetails(Comment c) {
        return CommentDetails.builder()
            .content(c.getContent())
            .createdBy(c.getCreatedBy())
            .createdDate(c.getCreatedDate())
            .build();
    }

}
