package com.indiepost.repository.hibernate;

import com.indiepost.model.Tag;
import com.indiepost.repository.CriteriaMaker;
import com.indiepost.repository.TagRepository;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by jake on 9/17/16.
 */
@Repository
public class TagRepositoryHibernate implements TagRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CriteriaMaker criteriaMaker;

    @Override
    public void save(Tag tag) {
        getSession().save(tag);
    }

    @Override
    public Tag findByTagName(String name) {
        return (Tag) getCriteria()
                .add(Restrictions.eq("name", name))
                .uniqueResult();
    }

    @Override
    public Tag findById(int id) {
        return (Tag) getCriteria()
                .add(Restrictions.eq("id", id))
                .uniqueResult();
    }

    @Override
    public List<Tag> findAll() {
        return getCriteria()
                .addOrder(Order.asc("name"))
                .list();
    }

    @Override
    public List<Tag> findAll(Pageable pageable) {
        return criteriaMaker.getPagedCriteria(getCriteria(), pageable)
                .list();
    }

    @Override
    public void update(Tag tag) {
        getSession().update(tag);
    }

    @Override
    public void delete(Tag tag) {
        getSession().delete(tag);
    }

    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    private Criteria getCriteria() {
        return getSession().createCriteria(Tag.class);
    }
}
