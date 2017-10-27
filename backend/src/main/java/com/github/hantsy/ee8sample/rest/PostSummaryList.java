/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest;

import java.util.ArrayList;
import java.util.List;
import javax.json.bind.annotation.JsonbProperty;
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
public class PostSummaryList {

    private List<PostSummary> content = new ArrayList<>();

    @JsonbProperty("_links")
    private List<Link> links = new ArrayList<>();

    public PostSummaryList(List<PostSummary> content) {
        this.content = content;
    }

    public PostSummaryList addLink(Link link) {
        this.links.add(link);
        return this;
    }
}
