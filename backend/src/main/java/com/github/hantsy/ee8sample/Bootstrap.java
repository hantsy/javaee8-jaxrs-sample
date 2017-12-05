/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample;

import static com.github.hantsy.ee8sample.Constants.ROLE_ADMIN;
import static com.github.hantsy.ee8sample.Constants.ROLE_USER;
import com.github.hantsy.ee8sample.domain.Comment;
import com.github.hantsy.ee8sample.domain.Post;
import com.github.hantsy.ee8sample.domain.Slug;
import com.github.hantsy.ee8sample.domain.User;
import com.github.hantsy.ee8sample.domain.Username;
import com.github.hantsy.ee8sample.repository.CommentRepository;
import com.github.hantsy.ee8sample.repository.PostRepository;
import com.github.hantsy.ee8sample.repository.UserRepository;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;

/**
 *
 * @author hantsy
 */
@Startup
@Singleton
public class Bootstrap {

    @Inject
    Logger LOG;

    @Inject
    PostRepository posts;

    @Inject
    CommentRepository comments;

    @Inject
    UserRepository users;

    @Inject
    private Pbkdf2PasswordHash passwordHash;

    @PostConstruct
    public void init() {
        LOG.info("initializing database...");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("Pbkdf2PasswordHash.Iterations", "3072");
        parameters.put("Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA512");
        parameters.put("Pbkdf2PasswordHash.SaltSizeBytes", "64");
        passwordHash.initialize(parameters);

        User user = User.builder()
                .username("user")
                .password(passwordHash.generate("password".toCharArray()))
                .authorities(Collections.unmodifiableSet(new HashSet<>(asList(ROLE_USER))))
                .build();

        User admin = User.builder()
                .username("admin")
                .password(passwordHash.generate("password".toCharArray()))
                .authorities(Collections.unmodifiableSet(new HashSet<>(asList(ROLE_USER, ROLE_ADMIN))))
                .build();
        
        users.save(user);
        users.save(admin);

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
