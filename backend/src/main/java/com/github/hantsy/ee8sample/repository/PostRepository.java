package com.github.hantsy.ee8sample.repository;

import com.github.hantsy.ee8sample.domain.Post;
import com.github.hantsy.ee8sample.support.AbstractRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static java.util.stream.Collectors.toList;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class PostRepository extends AbstractRepository<Post, Long> {

    @PersistenceContext
    private EntityManager em;

    public List<Post> findByKeyword(String keyword) {
//        CriteriaBuilder cb = this.em.getCriteriaBuilder();
//
//        CriteriaQuery<Post> q = cb.createQuery(Post.class);
//        Root<Post> c = q.from(Post.class);
//
//        List<Predicate> predicates = new ArrayList<>();
//
//        if (keyword != null) {
//            predicates.add(
//                    cb.or(
//                            cb.like(c.get(Post_.title), '%' + keyword + '%'),
//                            cb.like(c.get(Post_.content), '%' + keyword + '%')
//                    )
//            );
//        }
//
//        q.where(predicates.toArray(new Predicate[predicates.size()]));
//
//        TypedQuery<Post> query = em.createQuery(q);
//
//        return query.getResultList();
        return this.stream()
                .filter(
                        p -> Optional.ofNullable(keyword)
                                .map(k -> p.getTitle().contains(k) || p.getContent().contains(k))
                                .orElse(Boolean.TRUE)
                )
                .sorted(Post.DEFAULT_COMPARATOR)
                .collect(toList());
    }

    public List<Post> findByCreatedBy(String username) {
        Objects.requireNonNull(username, "username can not be null");
//        CriteriaBuilder cb = this.em.getCriteriaBuilder();
//
//        CriteriaQuery<Post> q = cb.createQuery(Post.class);
//        Root<Post> c = q.from(Post.class);
//
//        List<Predicate> predicates = new ArrayList<>();
//
//        predicates.add(
//                cb.equal(c.get(Post_.createdBy), username)
//        );
//
//        q.where(predicates.toArray(new Predicate[predicates.size()]));
//
//        TypedQuery<Post> query = em.createQuery(q);
//        List<Post> posts = query.getResultList();

        return this.stream()
                .filter(p -> p.getCreatedBy().equals(username))
                .sorted(Post.DEFAULT_COMPARATOR)
                .collect(toList());
    }

    @Override
    protected EntityManager entityManager() {
        return this.em;
    }

}
