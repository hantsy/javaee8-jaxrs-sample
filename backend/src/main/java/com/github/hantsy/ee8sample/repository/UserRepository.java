package com.github.hantsy.ee8sample.repository;

import com.github.hantsy.ee8sample.domain.User;
import com.github.hantsy.ee8sample.support.AbstractRepository;
import java.util.Objects;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class UserRepository extends AbstractRepository<User, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    protected EntityManager entityManager() {
        return this.em;
    }

    public Optional<User> findByUsername(String caller) {
        Objects.requireNonNull(caller, "username can not be null");
        return this.stream().filter(u -> u.getUsername().equals(caller)).findFirst();
    }

}
