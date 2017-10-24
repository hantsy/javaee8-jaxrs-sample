package com.github.hantsy.ee8sample.repository;


import com.github.hantsy.ee8sample.domain.Comment;
import com.github.hantsy.ee8sample.domain.Comment_;
import com.github.hantsy.ee8sample.domain.Post;
import com.github.hantsy.ee8sample.support.AbstractRepository;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author hantsy
 */
@Stateless
public class CommentRepository extends AbstractRepository<Comment, Long> {

    @PersistenceContext
    private EntityManager em;

    public List<Comment> findByPost(Post post) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();

        CriteriaQuery<Comment> q = cb.createQuery(Comment.class);
        Root<Comment> c = q.from(Comment.class);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(
            cb.equal(c.get(Comment_.post), post)
        );

        q.where(predicates.toArray(new Predicate[predicates.size()]));

        TypedQuery<Comment> query = em.createQuery(q);

        return query.getResultList();
    }

    @Override
    protected EntityManager entityManager() {
        return this.em;
    }

}
