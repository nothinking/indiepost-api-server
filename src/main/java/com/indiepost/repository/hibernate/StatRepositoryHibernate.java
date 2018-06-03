package com.indiepost.repository.hibernate;

import com.indiepost.dto.stat.PostStatDto;
import com.indiepost.dto.stat.ShareStat;
import com.indiepost.dto.stat.TimeDomainDoubleStat;
import com.indiepost.dto.stat.TimeDomainStat;
import com.indiepost.enums.Types.ClientType;
import com.indiepost.enums.Types.TimeDomainDuration;
import com.indiepost.model.analytics.Stat;
import com.indiepost.model.analytics.Visitor;
import com.indiepost.repository.StatRepository;
import com.indiepost.repository.utils.ResultMapper;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static com.indiepost.utils.DateUtil.localDateTimeToDate;
import static com.indiepost.utils.DateUtil.normalizeTimeDomainStats;

/**
 * Created by jake on 8/9/17.
 */
@Repository
public class StatRepositoryHibernate implements StatRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Long save(Stat stat) {
        if (stat.getVisitorId() != null) {
            Visitor visitorReference = entityManager.getReference(Visitor.class, stat.getVisitorId());
            stat.setVisitor(visitorReference);
        }
        entityManager.persist(stat);
        return stat.getId();
    }

    @Override
    public void delete(Stat stat) {
        entityManager.remove(stat);
    }

    @Override
    public Stat findOne(Long id) {
        return entityManager.find(Stat.class, id);
    }

    @Override
    public Long getTotalPageviews(LocalDateTime since, LocalDateTime until) {
        Criteria criteria = createCriteria();
        criteria.createAlias("visitor", "v");
        criteria.add(Restrictions.ne("s.class", "Click"));
        criteria.add(Restrictions.between("s.timestamp", since, until));
        criteria.add(Restrictions.ne("v.appName", ClientType.INDIEPOST_AD_ENGINE.toString()));
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }

    @Override
    public Long getTotalPageviews(LocalDateTime since, LocalDateTime until, String client) {
        Criteria criteria = createCriteria();
        criteria.createAlias("visitor", "v");
        criteria.add(Restrictions.ne("s.class", "Click"));
        criteria.add(Restrictions.between("s.timestamp", since, until));
        criteria.add(Restrictions.eq("v.appName", client));
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }

    @Override
    public Long getTotalPostviews(LocalDateTime since, LocalDateTime until) {
        Criteria criteria = createCriteria();
        criteria.createAlias("visitor", "v");
        criteria.add(Restrictions.isNotNull("s.postId"));
        criteria.add(Restrictions.ne("s.class", "Click"));
        criteria.add(Restrictions.between("s.timestamp", since, until));
        criteria.add(Restrictions.ne("v.appName", ClientType.INDIEPOST_AD_ENGINE.toString()));
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }

    @Override
    public Long getTotalPostviews(LocalDateTime since, LocalDateTime until, String client) {
        Criteria criteria = createCriteria();
        criteria.createAlias("visitor", "v");
        criteria.add(Restrictions.isNotNull("s.postId"));
        criteria.add(Restrictions.ne("s.class", "Click"));
        criteria.add(Restrictions.between("s.timestamp", since, until));
        criteria.add(Restrictions.eq("v.appName", client));
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }

    @Override
    public Long getTotalUniquePageviews(LocalDateTime since, LocalDateTime until) {
        Query query = getNamedQuery("@GET_TOTAL_UNIQUE_PAGEVIEWS");
        query.setParameter("since", localDateTimeToDate(since));
        query.setParameter("until", localDateTimeToDate(until));
        return ((BigInteger) query.getSingleResult()).longValue();
    }

    @Override
    public Long getTotalUniquePageviews(LocalDateTime since, LocalDateTime until, String client) {
        Query query = getNamedQuery("@GET_TOTAL_UNIQUE_PAGEVIEWS_BY_CLIENT");
        query.setParameter("client", client);
        query.setParameter("since", localDateTimeToDate(since));
        query.setParameter("until", localDateTimeToDate(until));
        return ((BigInteger) query.getSingleResult()).longValue();
    }

    @Override
    public Long getTotalUniquePostviews(LocalDateTime since, LocalDateTime until) {
        Query query = getNamedQuery("@GET_TOTAL_UNIQUE_PAGEVIEWS_ON_POSTS");
        query.setParameter("since", localDateTimeToDate(since));
        query.setParameter("until", localDateTimeToDate(until));
        return ((BigInteger) query.getSingleResult()).longValue();
    }

    @Override
    public Long getTotalUniquePostviews(LocalDateTime since, LocalDateTime until, String client) {
        Query query = getNamedQuery("@GET_TOTAL_UNIQUE_PAGEVIEWS_ON_POSTS_BY_CLIENT");
        query.setParameter("client", client);
        query.setParameter("since", localDateTimeToDate(since));
        query.setParameter("until", localDateTimeToDate(until));
        return ((BigInteger) query.getSingleResult()).longValue();
    }

    @Override
    public List<TimeDomainStat> getPageviewTrend(LocalDateTime since, LocalDateTime until, TimeDomainDuration duration) {
        switch (duration) {
            case HOURLY:
                return getPageviewTrendHourly(since, until);
            case DAILY:
                return getPageviewTrendDaily(since, until);
            case MONTHLY:
                return getPageviewTrendMonthly(since, until);
            case YEARLY:
                return getPageviewTrendYearly(since, until);
            default:
                return getPageviewTrendHourly(since, until);
        }
    }

    private List<TimeDomainStat> getPageviewTrendHourly(LocalDateTime since, LocalDateTime until) {
        Query query = getNamedQuery("@GET_PAGEVIEW_TREND_HOURLY");
        List<TimeDomainStat> trend = ResultMapper.toTimeDomainStatList(query, TimeDomainDuration.HOURLY, since, until);
        return normalizeTimeDomainStats(trend, since.toLocalDate(), until.toLocalDate());
    }

    private List<TimeDomainStat> getPageviewTrendDaily(LocalDateTime since, LocalDateTime until) {
        Query query = getNamedQuery("@GET_PAGEVIEW_TREND_DAILY");
        return ResultMapper.toTimeDomainStatList(query, TimeDomainDuration.DAILY, since, until);
    }

    private List<TimeDomainStat> getPageviewTrendMonthly(LocalDateTime since, LocalDateTime until) {
        Query query = getNamedQuery("@GET_PAGEVIEW_TREND_MONTHLY");
        return ResultMapper.toTimeDomainStatList(query, TimeDomainDuration.MONTHLY, since, until);
    }

    private List<TimeDomainStat> getPageviewTrendYearly(LocalDateTime since, LocalDateTime until) {
        Query query = getNamedQuery("@GET_PAGEVIEW_TREND_YEARLY");
        return ResultMapper.toTimeDomainStatList(query, TimeDomainDuration.YEARLY, since, until);
    }

    @Override
    public List<TimeDomainDoubleStat> getRecentAndOldPageviewTrend(LocalDateTime since, LocalDateTime until, TimeDomainDuration duration) {
        switch (duration) {
            case HOURLY:
                return getOldAndNewPageviewTrendHourly(since, until);
            case DAILY:
                return getOldAndNewPageviewTrendDaily(since, until);
            case MONTHLY:
                return getOldAndNewPageviewTrendMonthly(since, until);
            case YEARLY:
                return getOldAndNewPageviewTrendYearly(since, until);
            default:
                return getOldAndNewPageviewTrendHourly(since, until);
        }
    }

    private List<TimeDomainDoubleStat> getOldAndNewPageviewTrendHourly(LocalDateTime since, LocalDateTime until) {
        Query query = getNamedQuery("@GET_OLD_AND_NEW_PAGEVIEW_TREND_HOURLY");
        return ResultMapper.toTimeDomainDoubleStatList(query, TimeDomainDuration.HOURLY, since, until);
    }

    private List<TimeDomainDoubleStat> getOldAndNewPageviewTrendDaily(LocalDateTime since, LocalDateTime until) {
        Query query = getNamedQuery("@GET_OLD_AND_NEW_PAGEVIEW_TREND_DAILY");
        return ResultMapper.toTimeDomainDoubleStatList(query, TimeDomainDuration.DAILY, since, until);
    }

    private List<TimeDomainDoubleStat> getOldAndNewPageviewTrendMonthly(LocalDateTime since, LocalDateTime until) {
        Query query = getNamedQuery("@GET_OLD_AND_NEW_PAGEVIEW_TREND_MONTHLY");
        return ResultMapper.toTimeDomainDoubleStatList(query, TimeDomainDuration.MONTHLY, since, until);
    }

    private List<TimeDomainDoubleStat> getOldAndNewPageviewTrendYearly(LocalDateTime since, LocalDateTime until) {
        Query query = getNamedQuery("@GET_OLD_AND_NEW_PAGEVIEW_TREND_YEARLY");
        return ResultMapper.toTimeDomainDoubleStatList(query, TimeDomainDuration.YEARLY, since, until);
    }

    @Override
    public List<PostStatDto> getPostStatsOrderByPageviews(LocalDateTime since, LocalDateTime until, Integer limit) {
        Query query = getNamedQuery("@GET_POST_STATS_ORDER_BY_PAGEVIEWS");
        return ResultMapper.toPostStatDtoList(query, since, until, limit);
    }

    @Override
    public List<PostStatDto> getAllPostStats() {
        Query query = getNamedQuery("@GET_ALL_POST_STATS");
        return ResultMapper.toPostStatDtoList(query);
    }

    @Override
    public List<PostStatDto> getCachedPostStats() {
        Query query = getNamedQuery("@GET_ALL_POST_STATS_FROM_CACHE");
        return ResultMapper.toPostStatDtoList(query);
    }

    @Override
    public List<ShareStat> getPageviewsByCategory(LocalDateTime since, LocalDateTime until, Integer limit) {
        Query query = getNamedQuery("@GET_PAGEVIEWS_ORDER_BY_CATEGORY");
        return ResultMapper.toShareStateList(query, since, until, limit);
    }

    @Override
    public List<ShareStat> getPageviewsByAuthor(LocalDateTime since, LocalDateTime until, Integer limit) {
        Query query = getNamedQuery("@GET_PAGEVIEWS_ORDER_BY_AUTHOR");
        return ResultMapper.toShareStateList(query, since, until, limit);
    }

    @Override
    public List<ShareStat> getTopPages(LocalDateTime since, LocalDateTime until, Integer limit) {
        Query query = getNamedQuery("@GET_TOP_PAGES");
        return ResultMapper.toShareStateList(query, since, until, limit);
    }

    @Override
    public List<ShareStat> getTopPages(LocalDateTime since, LocalDateTime until, Integer limit, String client) {
        Query query = getNamedQuery("@GET_TOP_PAGES_BY_CLINT_TYPE");
        return ResultMapper.toShareStateList(query, since, until, limit, client);
    }

    @Override
    public List<ShareStat> getTopPosts(LocalDateTime since, LocalDateTime until, Integer limit) {
        Query query = getNamedQuery("@GET_TOP_POSTS");
        return ResultMapper.toShareStateList(query, since, until, limit);
    }

    @Override
    public List<ShareStat> getTopPosts(LocalDateTime since, LocalDateTime until, Integer limit, String client) {
        Query query = getNamedQuery("@GET_TOP_POSTS_BY_CLINT_TYPE");
        return ResultMapper.toShareStateList(query, since, until, limit, client);
    }

    @Override
    public List<ShareStat> getTopLandingPages(LocalDateTime since, LocalDateTime until, Integer limit) {
        Query query = getNamedQuery("@GET_TOP_LANDING_PAGE");
        return ResultMapper.toShareStateList(query, since, until, limit);
    }

    @Override
    public List<ShareStat> getTopLandingPages(LocalDateTime since, LocalDateTime until, Integer limit, String client) {
        Query query = getNamedQuery("@GET_TOP_LANDING_PAGE_BY_CLINT_TYPE");
        return ResultMapper.toShareStateList(query, since, until, limit, client);
    }

    @Override
    public List<ShareStat> getTopTags(LocalDateTime since, LocalDateTime until, Integer limit) {
        Query query = getNamedQuery("@GET_TOP_TAGS");
        return ResultMapper.toShareStateList(query, since, until, limit, null);
    }

    @Override
    public List<ShareStat> getTopTags(LocalDateTime since, LocalDateTime until, Integer limit, String client) {
        Query query = getNamedQuery("@GET_TOP_TAGS_BY_CLIENT");
        return ResultMapper.toShareStateList(query, since, until, limit, client);
    }

    @Override
    public List<ShareStat> getTopRecentPosts(LocalDateTime since, LocalDateTime until, Integer limit) {
        Query query = getNamedQuery("@GET_TOP_RECENT_POSTS");
        return ResultMapper.toShareStateList(query, since, until, limit, null);
    }

    @Override
    public List<ShareStat> getTopOldPosts(LocalDateTime since, LocalDateTime until, Integer limit) {
        Query query = getNamedQuery("@GET_TOP_OLD_POSTS");
        return ResultMapper.toShareStateList(query, since, until, limit, null);
    }

    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    private Query getNamedQuery(String queryName) {
        return entityManager.createNamedQuery(queryName);
    }

    private Criteria createCriteria() {
        return getSession().createCriteria(Stat.class, "s");
    }
}