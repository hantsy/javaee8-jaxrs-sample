/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest;

import com.github.hantsy.ee8sample.domain.Post;
import com.github.hantsy.ee8sample.domain.Post.Status;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.json.bind.annotation.JsonbProperty;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriInfo;
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
public class PostDetails implements Serializable {

    private Long id;

    private String slug;

    private String title;

    private String content;

    private Status status;

    private boolean favorited = false;

    private long commentsCount = 0;

    private LocalDateTime createdDate;

    private String createdBy;

    private LocalDateTime lastModifiedDate;

    private String lastModifiedBy;

    @JsonbProperty("_links")
    private List<Link> links = new ArrayList<>();

    public PostDetails(Post data, UriInfo uriInfo) {
        setTitle(data.getTitle());
        setContent(data.getContent());
        setSlug(data.getSlug());
        setStatus(data.getStatus());
        setCreatedDate(data.getCreatedDate());
        setCreatedBy(data.getCreatedBy());
        setLastModifiedDate(data.getLastModifiedDate());
        setLastModifiedBy(data.getLastModifiedBy());
        
        Link self = Link.fromUriBuilder(uriInfo.getBaseUriBuilder()).rel("self").uri("posts/{slug}").build(data.getSlug());
        Link comments = Link.fromUriBuilder(uriInfo.getBaseUriBuilder()).rel("comments").uri("posts/{slug}/comments").build(data.getSlug());
        this.links.add(self);
        this.links.add(comments);
    }
    
    public PostDetails addLink(Link link){
        this.links.add(link);
        return this;
    }

}
