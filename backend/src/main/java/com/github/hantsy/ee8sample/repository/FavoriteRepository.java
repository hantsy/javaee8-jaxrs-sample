package com.github.hantsy.ee8sample.repository;

import com.github.hantsy.ee8sample.domain.Favorite;
import com.github.hantsy.ee8sample.domain.Slug;
import com.github.hantsy.ee8sample.domain.support.AbstractRepository;
import java.util.List;
import static java.util.stream.Collectors.toList;

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

    public List<Favorite> favoritesByPost(String slug) {
        return this.stream()
                .filter(f -> f.getPost().equals(new Slug(slug)))
                .collect(toList());
    }

    public Boolean postIsFavorited(String slug, String username) {
        return this.stream()
                .filter(f -> f.getUser().getUsername().equals(username) && f.getPost().equals(new Slug(slug)))
                .count() > 0;
    }

}
