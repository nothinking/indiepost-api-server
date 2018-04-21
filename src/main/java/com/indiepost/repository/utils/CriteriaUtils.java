package com.indiepost.repository.utils;

import com.indiepost.dto.PostQuery;
import com.indiepost.dto.stat.PostStatDto;
import com.indiepost.dto.stat.ShareStat;
import com.indiepost.dto.stat.TimeDomainDoubleStat;
import com.indiepost.dto.stat.TimeDomainStat;
import com.indiepost.enums.Types;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static com.indiepost.utils.DateUtil.localDateTimeToDate;

/**
 * Created by jake on 17. 1. 14.
 */
public interface CriteriaUtils {
    static Criteria setPageToCriteria(Criteria criteria, Pageable pageable) {
        Sort sort = pageable.getSort();
        if (sort != null) {
            for (Sort.Order order : sort) {
                if (order.getDirection().equals(Sort.Direction.ASC)) {
                    criteria.addOrder(Order.asc(order.getProperty()));
                } else {
                    criteria.addOrder(Order.desc(order.getProperty()));
                }
            }
        }
        // TODO remove verbose casting
        criteria.setFirstResult(new Long(pageable.getOffset()).intValue());
        criteria.setMaxResults(pageable.getPageSize());
        return criteria;
    }

    static void buildConjunction(PostQuery query, Conjunction conjunction) {
        if (query.getAuthorId() != null) {
            conjunction.add(Restrictions.eq("authorId", query.getAuthorId()));
        }
        if (query.getEditorId() != null) {
            conjunction.add(Restrictions.eq("editorId", query.getEditorId()));
        }
        if (query.getCategoryId() != null) {
            conjunction.add(Restrictions.eq("categoryId", query.getCategoryId()));
        }
        if (StringUtils.isNotEmpty(query.getCategorySlug())) {
            conjunction.add(Restrictions.ilike("category.slug", query.getCategorySlug()));
        }

        if (query.getStatus() != null) {
            conjunction.add(Restrictions.eq("status", query.getStatus()));
        }
        if (query.getDateFrom() != null) {
            conjunction.add(Restrictions.ge("publishedAt", query.getDateFrom()));
        }
        if (query.getDateTo() != null) {
            conjunction.add(Restrictions.le("publishedAt", query.getDateTo()));
        }
        if (query.isSplash()) {
            conjunction.add(Restrictions.eq("splash", query.isSplash()));
        }
        if (query.isFeatured()) {
            conjunction.add(Restrictions.eq("featured", query.isFeatured()));
        }
        if (query.isPicked()) {
            conjunction.add(Restrictions.eq("picked", query.isPicked()));
        }
    }

    static List<TimeDomainStat> getTrend(Query query, Types.TimeDomainDuration duration, LocalDateTime since, LocalDateTime until) {
        query.setParameter("since", localDateTimeToDate(since));
        query.setParameter("until", localDateTimeToDate(until));
        query.setResultTransformer(new ResultTransformer() {
            @Override
            public Object transformTuple(Object[] tuple, String[] aliases) {
                if (Types.TimeDomainDuration.HOURLY.equals(duration)) {
                    return new TimeDomainStat(
                            ((Timestamp) tuple[0]).toLocalDateTime(),
                            ((BigInteger) tuple[1]).longValue()
                    );
                }
                return new TimeDomainStat(
                        ((Date) tuple[0]).toLocalDate().atStartOfDay(),
                        ((BigInteger) tuple[1]).longValue()
                );
            }

            @Override
            public List transformList(List collection) {
                return collection;
            }
        });
        return query.list();
    }

    static List<TimeDomainDoubleStat> getDoubleTrend(Query query, Types.TimeDomainDuration duration, LocalDateTime since, LocalDateTime until) {
        query.setParameter("since", localDateTimeToDate(since));
        query.setParameter("until", localDateTimeToDate(until));
        query.setResultTransformer(new ResultTransformer() {
            @Override
            public Object transformTuple(Object[] tuple, String[] aliases) {
                if (Types.TimeDomainDuration.HOURLY.equals(duration)) {
                    return new TimeDomainDoubleStat(
                            ((Timestamp) tuple[0]).toLocalDateTime(),
                            ((BigInteger) tuple[1]).longValue(),
                            ((BigInteger) tuple[2]).longValue()
                    );
                }
                return new TimeDomainDoubleStat(
                        ((Date) tuple[0]).toLocalDate().atStartOfDay(),
                        ((BigInteger) tuple[1]).longValue(),
                        ((BigInteger) tuple[2]).longValue()
                );
            }

            @Override
            public List transformList(List collection) {
                return collection;
            }
        });
        return query.list();
    }

    static List<ShareStat> getShare(Query query, LocalDateTime since, LocalDateTime until, Long limit) {
        return getShare(query, since, until, limit, null);
    }

    static List<ShareStat> getShare(Query query, LocalDateTime since, LocalDateTime until, Long limit, String client) {
        query.setParameter("since", localDateTimeToDate(since));
        query.setParameter("until", localDateTimeToDate(until));
        query.setLong("limit", limit);
        if (client != null) {
            query.setParameter("client", client);
        }
        query.setResultTransformer(new ShareStatResultTransformer());
        return query.list();
    }

    static List<PostStatDto> getPostShare(Query query, LocalDateTime since, LocalDateTime until, Long limit) {
        query.setParameter("since", localDateTimeToDate(since));
        query.setParameter("until", localDateTimeToDate(until));
        query.setLong("limit", limit);
        query.setResultTransformer(new PostStatsResultTransformer());
        return query.list();
    }
}
