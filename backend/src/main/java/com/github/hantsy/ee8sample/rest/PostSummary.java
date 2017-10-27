/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest;

import com.github.hantsy.ee8sample.domain.Post;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Link;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author hantsy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSummary {

    private Long id;

    private String slug;

    private String title;

    private long commentsCount;

    private List<Link> links = new ArrayList<>();

    public PostSummary(Post post) {
        this.id = post.getId();
        this.slug = post.getSlug();
        this.title = post.getTitle();
    }

    public PostSummary addLink(Link link) {
        this.links.add(link);
        return this;
    }
}
