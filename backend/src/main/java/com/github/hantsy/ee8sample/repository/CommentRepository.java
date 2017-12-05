package com.github.hantsy.ee8sample.repository;

import com.github.hantsy.ee8sample.domain.Comment;
import com.github.hantsy.ee8sample.domain.support.AbstractRepository;
import java.util.List;
import java.util.Objects;
import static java.util.stream.Collectors.toList;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author hantsy
 */
@Stateless
public class CommentRepository extends AbstractRepository<Comment, Long> {

    @PersistenceContext
    private EntityManager em;

    public List<Comment> findByPost(String post, long limit, long offset) {
//        CriteriaBuilder cb = this.em.getCriteriaBuilder();
//
//        CriteriaQuery<Comment> q = cb.createQuery(Comment.class);
//        Root<Comment> c = q.from(Comment.class);
//
//        List<Predicate> predicates = new ArrayList<>();
//
//        predicates.add(
//            cb.equal(c.get(Comment_.post), post)
//        );
//
//        q.where(predicates.toArray(new Predicate[predicates.size()]));
//
//        TypedQuery<Comment> query = em.createQuery(q);
//
//        return query.getResultList();
        Objects.requireNonNull(post, "post slug can not be null");
        return this.stream()
                .filter(c -> c.getPost().getSlug().equals(post))
                .limit(limit)
                .skip(offset)
                .collect(toList());
    }

    public long countByPost(String slug) {
        Objects.requireNonNull(slug, "post slug can not be null");
        return this.stream()
                .filter(c -> c.getPost().equals(slug))
                .count();
    }

    @Override
    protected EntityManager entityManager() {
        return this.em;
    }

}
