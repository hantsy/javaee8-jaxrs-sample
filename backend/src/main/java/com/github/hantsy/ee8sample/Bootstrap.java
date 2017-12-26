/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample;

import static com.github.hantsy.ee8sample.Constants.ROLE_ADMIN;
import static com.github.hantsy.ee8sample.Constants.ROLE_USER;
import com.github.hantsy.ee8sample.domain.Comment;
import com.github.hantsy.ee8sample.domain.Favorite;
import com.github.hantsy.ee8sample.domain.Post;
import com.github.hantsy.ee8sample.domain.Slug;
import com.github.hantsy.ee8sample.domain.User;
import com.github.hantsy.ee8sample.domain.Username;
import com.github.hantsy.ee8sample.domain.repository.CommentRepository;
import com.github.hantsy.ee8sample.domain.repository.FavoriteRepository;
import com.github.hantsy.ee8sample.domain.repository.PostRepository;
import com.github.hantsy.ee8sample.domain.repository.UserRepository;
import com.github.hantsy.ee8sample.security.hash.Crypto;
import static com.github.hantsy.ee8sample.security.hash.Crypto.Type.BCRYPT;
import com.github.hantsy.ee8sample.security.hash.PasswordEncoder;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
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
    FavoriteRepository favorites;

    @Inject
    @Crypto(BCRYPT)
    private PasswordEncoder passwordHash;

    @PostConstruct
    public void init() {
        LOG.info("initializing database...");
        User user = User.builder()
            .username("user")
            .password(passwordHash.encode("password"))
            .firstName("user firstName")
            .lastName("user lastName")
            .email("user@example.com")
            .authorities(Collections.unmodifiableSet(new HashSet<>(asList(ROLE_USER))))
            .build();

        User admin = User.builder()
            .username("admin")
            .password(passwordHash.encode("password"))
            .firstName("admin firstName")
            .lastName("admin lastName")
            .email("admin@example.com")
            .authorities(Collections.unmodifiableSet(new HashSet<>(asList(ROLE_USER, ROLE_ADMIN))))
            .build();

        users.save(user);
        users.save(admin);

        Post post1 = Post.builder()
            .title("Build RESTful APIs with JAXRS 2.1")
            .content("Content of Getting started with REST")
            .status(Post.Status.PUBLISHED)
            .build();
        post1.setCreatedBy(new Username("user"));
        posts.save(post1);

        Post post2 = Post.builder()
            .title("Getting started with Java EE 8")
            .content("Content of Getting started with Java EE 8")
            .status(Post.Status.PUBLISHED)
            .build();
        post2.setCreatedBy(new Username("user"));
        posts.save(post2);

        Post post3 = Post.builder()
            .title("Getting started with Angular")
            .content("Content of Getting started with Angular")
            .status(Post.Status.PUBLISHED)
            .build();

        post3.setCreatedBy(new Username("user"));
        post3 = posts.save(post3);

        Comment comment = Comment.builder()
            .content("Awesome!\n Good post.")
            .build();

        comment.setPost(new Slug(post3.getSlug()));
        comment.setCreatedBy(new Username("user"));
        comments.save(comment);

        Favorite favorite = Favorite.builder()
            .post(new Slug(post3.getSlug()))
            .user(new Username("user"))
            .build();

        favorites.save(favorite);

        LOG.info("data initilization done.");
        LOG.log(Level.INFO, "all uesrs :: {0}", users.findAll());
        LOG.log(Level.INFO, "all posts :: {0}", posts.findAll().stream().map(Post.TO_STRING).collect(toList()));
        LOG.log(Level.INFO, "all comments :: {0}", comments.findAll());
    }
}
