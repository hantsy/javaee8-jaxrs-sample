/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest;

import com.github.hantsy.ee8sample.domain.Slug;

/**
 *
 * @author hantsy
 */
//@Path("comments")
//@Stateless
public class CommentResource {

    private Slug slug;

    public CommentResource() {
    }

    public CommentResource(String slug) {
        this.slug = new Slug(slug);
    }

}
