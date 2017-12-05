/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest;

import com.github.hantsy.ee8sample.domain.Comment;
import com.github.hantsy.ee8sample.domain.Post;
import com.github.hantsy.ee8sample.domain.Slug;
import com.github.hantsy.ee8sample.domain.Username;
import com.github.hantsy.ee8sample.repository.CommentRepository;
import com.github.hantsy.ee8sample.repository.PostRepository;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 *
 * @author hantsy
 */
@Startup
@Singleton
public class PostDataInitializer {

//    @Inject
//    Logger LOG;

    @Inject
    PostRepository posts;

    @Inject
    CommentRepository comments;

    @PostConstruct
    public void init() {
        //LOG.info("initializing database...");

        Post post1 = Post.builder()
                .title("Build RESTful APIs with JAXRS 2.1")
                .content("Content of Getting started with REST")
                .build();
        post1.setCreatedBy(new Username("user"));
        posts.save(post1);

        Post post2 = Post.builder()
                .title("Getting started with Java EE 8")
                .content("Content of Getting started with Java EE 8")
                .build();
        post2.setCreatedBy(new Username("user"));
        posts.save(post2);

        Post post3 = Post.builder()
                .title("Getting started with Angular2")
                .content("Content of Getting started with Angular2")
                .build();
        post3.setCreatedBy(new Username("user"));
        post3 = posts.save(post3);

        Comment comment = Comment.builder()
                .content("Awesome!\n Good post.")
                .build();

        comment.setPost(new Slug(post3.getSlug()));
        comment.setCreatedBy(new Username("user"));
        comments.save(comment);

    }
}
