package com.github.hantsy.ee8sample.domain.repository;

import com.github.hantsy.ee8sample.domain.User;
import com.github.hantsy.ee8sample.domain.support.AbstractRepository;
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

    public Optional<User> findByEmail(String email) {
        Objects.requireNonNull(email, "email can not be null");
        return this.stream().filter(u -> u.getEmail().equals(email)).findFirst();
    }

}
