package com.github.hantsy.ee8sample.domain.repository;

import com.github.hantsy.ee8sample.domain.Favorite;
import com.github.hantsy.ee8sample.domain.Slug;
import com.github.hantsy.ee8sample.domain.support.AbstractRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    public List<Favorite> findByPost(String slug) {
        Objects.requireNonNull(slug, "post slug can not be null");
        return this.stream()
                .filter(f -> f.getPost().equals(new Slug(slug)))
                .collect(toList());
    }

    public Boolean postIsFavorited(String slug, String username) {
        Objects.requireNonNull(slug, "post slug can not be null");
        Objects.requireNonNull(username, "username can not be null");
        return this.stream()
                .filter(f -> f.getUser().getUsername().equals(username) && f.getPost().getSlug().equals(slug))
                .count() > 0;
    }

    public Optional<Favorite> findBySlugAndUsername(String slug, String username) {
        Objects.requireNonNull(slug, "post slug can not be null");
        Objects.requireNonNull(username, "username can not be null");
        return this.stream()
                .filter(f -> f.getUser().getUsername().equals(username) && f.getPost().getSlug().equals(slug))
                .findFirst();
    }

    public long countByPost(String slug) {
        Objects.requireNonNull(slug, "post slug can not be null");
        return this.stream()
                .filter(f -> f.getPost().getSlug().equals(slug))
                .count();
    }

}
