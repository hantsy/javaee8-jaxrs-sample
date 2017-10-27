/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest;

import com.github.hantsy.ee8sample.domain.Favorite;
import com.github.hantsy.ee8sample.domain.Slug;
import static java.util.Collections.emptyList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 * @author hantsy
 */
//@Path("favorites")
//@Stateless
public class FavoriteResource {

    private Slug slug;

    public FavoriteResource() {
    }

    public FavoriteResource(String slug) {
        this.slug = new Slug(slug);
    }
    
    
    @Path("")
    @GET
    public List<Favorite> allFavoritesOfPost(){
       return emptyList();
    }

}
