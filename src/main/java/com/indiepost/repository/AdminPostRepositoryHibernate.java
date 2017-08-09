package com.indiepost.repository;

import com.github.fluent.hibernate.request.aliases.Aliases;
import com.indiepost.dto.PostQuery;
import com.indiepost.enums.Types.PostStatus;
import com.indiepost.enums.Types.UserRole;
import com.indiepost.model.Post;
import com.indiepost.model.Role;
import com.indiepost.model.User;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

import static com.indiepost.repository.utils.CriteriaUtils.buildConjunction;
import static com.indiepost.repository.utils.CriteriaUtils.setPageToCriteria;

/**
 * Created by jake on 17. 1. 11.
 */
@Repository
@SuppressWarnings("unchecked")
public class AdminPostRepositoryHibernate implements AdminPostRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Long save(Post post) {
        return (Long) getSession().save(post);
    }

    @Override
    public Post findById(Long id) {
        // TODO reduce query
        return entityManager.find(Post.class, id);
    }

    @Override
    public void update(Post post) {
        getSession().update(post);
    }

    @Override
    public void delete(Post post) {
        getSession().delete(post);
    }

    @Override
    public List<Post> find(User user, Pageable pageable) {
        return find(user, null, pageable);
    }


    @Override
    public List<Post> find(User user, PostQuery query, Pageable pageable) {
        Criteria criteria = getPagedCriteria(pageable);
        getAliases().addToCriteria(criteria);
        // TODO Projection List<AdminPostSummaryDto>
        // TODO fetch n + 1 problem
//        criteria.setProjection(getProjectionList());
        criteria.setFetchMode("tags", FetchMode.JOIN);
        UserRole role = getRole(user);
        Conjunction conjunction = Restrictions.conjunction();

        if (query != null) {
            buildConjunction(query, conjunction);
        }

        switch (role) {
            case Administrator:
                break;
            case EditorInChief:
            case Editor:
                conjunction.add(getPrivacyCriterion(user.getId()));
                break;
            default:
                conjunction.add(getPrivacyCriterion(user.getId()));
                conjunction.add(Restrictions.eq("author.id", user.getId()));
                break;
        }

        if (conjunction.conditions().iterator().hasNext()) {
            criteria.add(conjunction);
        }
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

        return criteria.list();
    }

    @Override
    public Long count() {
        return (Long) getCriteria().setProjection(Projections.rowCount())
                .uniqueResult();
    }

    @Override
    public Long count(PostQuery postQuery) {
        Conjunction conjunction = Restrictions.conjunction();
        buildConjunction(postQuery, conjunction);
        return (Long) getCriteria().add(conjunction).setProjection(Projections.rowCount())
                .uniqueResult();
    }

    @Override
    public List<Post> findScheduledPosts() {
        return getCriteria()
                .add(Restrictions.eq("status", PostStatus.FUTURE))
                .add(Restrictions.le("publishedAt", LocalDateTime.now()))
                .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public void disableSplashPosts() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Post> update = criteriaBuilder.createCriteriaUpdate(Post.class);
        Root e = update.from(Post.class);
        update.set("splash", false);
        update.where(criteriaBuilder.and(
                criteriaBuilder.equal(e.get("status"), PostStatus.PUBLISH)),
                criteriaBuilder.equal(e.get("splash"), true));
        entityManager.createQuery(update).executeUpdate();
    }

    @Override
    public void disableFeaturedPosts() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Post> update = criteriaBuilder.createCriteriaUpdate(Post.class);
        Root e = update.from(Post.class);
        update.set("featured", false);
        update.where(criteriaBuilder.and(
                criteriaBuilder.equal(e.get("status"), PostStatus.PUBLISH)),
                criteriaBuilder.equal(e.get("featured"), true));
        entityManager.createQuery(update).executeUpdate();
    }

    @Override
    public List<String> findAllDisplayNames() {
        Criteria criteria = getCriteria()
                .add(Restrictions.ne("displayName", ""))
                .setProjection(
                        Projections.distinct(
                                Projections.projectionList()
                                        .add(Projections.property("displayName"))

                        )
                );
        return criteria.list();
    }


    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    private Criteria getCriteria() {
        return getSession().createCriteria(Post.class);
    }

    private Criteria getPagedCriteria(Pageable pageable) {
        return setPageToCriteria(getCriteria(), pageable);
    }

    private Aliases getAliases() {
        return Aliases.create()
                .add("author", "author", JoinType.INNER)
                .add("editor", "editor", JoinType.INNER)
                .add("category", "category", JoinType.INNER);
    }

    private ProjectionList getProjectionList() {
        return Projections.projectionList()
                .add(Property.forName("id"), "id")
                .add(Property.forName("title"), "title")
                .add(Property.forName("status"), "status")
                .add(Property.forName("displayName"), "displayName")
                .add(Property.forName("category.name"), "category.name")
                .add(Property.forName("author.displayName"), "author.displayName")
                .add(Property.forName("editor.displayName"), "editor.displayName")
                .add(Property.forName("createdAt"), "createdAt")
                .add(Property.forName("publishedAt"), "publishedAt")
                .add(Property.forName("modifiedAt"), "modifiedAt")
                .add(Property.forName("likesCount"), "likesCount")
                .add(Property.forName("tags"), "tags");
    }

    private Criterion getPrivacyCriterion(Long userId) {
        return Restrictions.not(
                Restrictions.and(
                        Restrictions.ne("editor.id", userId),
                        Restrictions.or(
                                Restrictions.eq("status", PostStatus.TRASH),
                                Restrictions.eq("status", PostStatus.DRAFT),
                                Restrictions.eq("status", PostStatus.AUTOSAVE)
                        )
                )
        );
    }

    private UserRole getRole(User user) {
        List<Role> roleList = user.getRoles();
        int userLevel = 1;
        for (Role role : roleList) {
            if (role.getLevel() > userLevel) {
                userLevel = role.getLevel();
            }
        }

        switch (userLevel) {
            case 9:
                return UserRole.Administrator;
            case 7:
                return UserRole.EditorInChief;
            case 5:
                return UserRole.Editor;
            case 3:
                return UserRole.Author;
            case 1:
                return UserRole.User;
            default:
                return UserRole.User;
        }
    }
}
