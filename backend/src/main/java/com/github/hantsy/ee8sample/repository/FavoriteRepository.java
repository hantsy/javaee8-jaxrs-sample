package com.github.hantsy.ee8sample.repository;

import com.github.hantsy.ee8sample.domain.Favorite;
import com.github.hantsy.ee8sample.domain.Slug;
import com.github.hantsy.ee8sample.domain.User;
import com.github.hantsy.ee8sample.domain.support.AbstractRepository;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class FavoriteRepository extends AbstractRepository<Favorite, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return this.em;
    }

    public Boolean postIsFavorited(String slug, String username) {
        return this.stream().filter(f -> f.getUsername().equals(username) && f.getSlug().equals(new Slug(slug))).count()>0 ;
    }

}
