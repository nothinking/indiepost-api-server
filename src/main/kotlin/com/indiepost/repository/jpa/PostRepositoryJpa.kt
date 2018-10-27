package com.indiepost.repository.jpa

import com.indiepost.dto.Timeline
import com.indiepost.dto.TimelineRequest
import com.indiepost.dto.post.PostQuery
import com.indiepost.dto.post.PostSummaryDto
import com.indiepost.enums.Types.PostStatus
import com.indiepost.model.*
import com.indiepost.model.QPost.post
import com.indiepost.repository.PostRepository
import com.indiepost.repository.utils.addConjunction
import com.indiepost.utils.DateUtil.instantToLocalDateTime
import com.indiepost.utils.DateUtil.localDateTimeToInstant
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.Tuple
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.apache.commons.collections4.CollectionUtils.isEmpty
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * Created by jake on 7/30/16.
 */
@Repository
class PostRepositoryJpa : PostRepository {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    private val queryFactory: JPAQueryFactory
        get() = JPAQueryFactory(entityManager)

    override fun findById(id: Long): Post? {
        return queryFactory
                .selectFrom(post)
                .join(post.category, QCategory.category)
                .leftJoin(post.titleImage, QImageSet.imageSet)
                .fetchJoin()
                .where(post.id.eq(id))
                .fetchOne()
    }

    override fun count(): Long {
        return queryFactory
                .selectFrom(post)
                .fetchCount()
    }

    override fun count(postQuery: PostQuery): Long {
        val builder = addConjunction(postQuery, BooleanBuilder())
        return queryFactory
                .selectFrom(post)
                .where(builder)
                .fetchCount()
    }

    override fun findByIds(ids: List<Long>): List<PostSummaryDto> {
        if (isEmpty(ids)) {
            return ArrayList()
        }
        val query = queryFactory.from(post)
        addProjections(query)
                .innerJoin(post.category, QCategory.category)
                .leftJoin(post.titleImage, QImageSet.imageSet)
                .where(post.id.`in`(ids))
                .distinct()
        val rows = query.fetch() as List<Tuple>
        val result = ArrayList<Tuple>()
        for (id in ids) {
            for (row in rows) {
                val postId = row.get(post.id)
                if (id == postId) {
                    result.add(row)
                    break
                }
            }
        }
        return toDtoList(result)
    }

    override fun findByCategorySlug(slug: String, pageable: Pageable): Page<PostSummaryDto> {
        val query = PostQuery.Builder(PostStatus.PUBLISH)
                .category(slug)
                .build()
        return this.query(query, pageable)
    }

    override fun findByTagName(tagName: String, pageable: Pageable): Page<PostSummaryDto> {
        val tag = tagName.toLowerCase()
        val query = queryFactory.from(post)
        addProjections(query)
                .innerJoin(post.category, QCategory.category)
                .innerJoin(post.postTags, QPostTag.postTag)
                .innerJoin(QPostTag.postTag.tag, QTag.tag)
                .leftJoin(post.titleImage, QImageSet.imageSet)
                .where(QTag.tag.name.eq(tag).and(post.status.eq(PostStatus.PUBLISH)))
                .orderBy(post.publishedAt.desc(), QPostTag.postTag.priority.asc())
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong()).distinct()
        val result = query.fetch()
        if (result.size == 0) {
            return PageImpl(emptyList(), pageable, 0)
        }
        val total = queryFactory.selectFrom(post)
                .innerJoin(post.postTags, QPostTag.postTag)
                .innerJoin(QPostTag.postTag.tag, QTag.tag)
                .where(QTag.tag.name.eq(tag).and(post.status.eq(PostStatus.PUBLISH)))
                .fetchCount()
        val dtoList = toDtoList(result as List<Tuple>)
        return PageImpl(dtoList, pageable, total)
    }

    override fun findByContributorFullName(fullName: String, pageable: Pageable): Page<PostSummaryDto> {
        val ct = QCategory.category
        val pc = QPostContributor.postContributor
        val c = QContributor.contributor
        val i = QImageSet.imageSet

        val query = queryFactory.from(post)
        addProjections(query)
                .innerJoin(post.category, ct)
                .innerJoin(post.postContributors, pc)
                .innerJoin(pc.contributor, c)
                .leftJoin(post.titleImage, i)
                .where(c.fullName.eq(fullName).and(post.status.eq(PostStatus.PUBLISH)))
                .orderBy(post.publishedAt.desc(), QPostContributor.postContributor.priority.asc())
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong()).distinct()
        val result = query.fetch()
        if (result.size == 0) {
            return PageImpl(emptyList(), pageable, 0)
        }
        val total = queryFactory.selectFrom(post)
                .innerJoin(post.postContributors, pc)
                .innerJoin(pc.contributor, c)
                .where(c.fullName.eq(fullName).and(post.status.eq(PostStatus.PUBLISH)))
                .fetchCount()
        val dtoList = toDtoList(result as List<Tuple>)
        return PageImpl(dtoList, pageable, total)
    }

    override fun findByStatus(status: PostStatus, pageable: Pageable): Page<PostSummaryDto> {
        val query = PostQuery.Builder(status).build()
        return this.query(query, pageable)
    }

    override fun findRelatedPostsById(id: Long): Page<PostSummaryDto> {
        val ct = QCategory.category
        val im = QImageSet.imageSet
        val pt = QPostTag.postTag
        val t = QTag.tag
        val pp = QPostPost.postPost

        val query = queryFactory
                .selectFrom(pp)
        addProjections(query)
                .innerJoin(pp.relatedPost, post)
                .innerJoin(pp.relatedPost.category, ct)
                .leftJoin(pp.relatedPost.postTags, pt)
                .leftJoin(pt.tag, t)
                .leftJoin(pp.relatedPost.titleImage, im)
                .where(pp.postId.eq(id)
                        .and(pp.relatedPost.status.eq(PostStatus.PUBLISH)))
                .orderBy(pp.priority.asc())
                .groupBy(pp.relatedPostId)
        val result = query.fetch()
        val dtoList = toDtoList(result as List<Tuple>)
        return PageImpl(dtoList, PageRequest.of(0, 4), dtoList.size.toLong())
    }

    override fun findScheduledPosts(): List<PostSummaryDto> {
        val query = queryFactory.from(post)
        addProjections(query)
                .innerJoin(post.category, QCategory.category)
                .leftJoin(post.titleImage, QImageSet.imageSet)
                .where(post.status.eq(PostStatus.FUTURE))
                .orderBy(post.publishedAt.asc())
                .distinct()
        val result = query.fetch()
        return toDtoList(result as List<Tuple>)
    }

    override fun query(postQuery: PostQuery, pageable: Pageable): Page<PostSummaryDto> {
        val query = queryFactory.from(post)
        val builder = addConjunction(postQuery, BooleanBuilder())
        addProjections(query)
                .innerJoin(post.category, QCategory.category)
                .leftJoin(post.titleImage, QImageSet.imageSet)
                .where(builder)
                .orderBy(post.publishedAt.desc())
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong()).distinct()
        val result = query.fetch()
        val total = count(postQuery)
        val dtoList = toDtoList(result as List<Tuple>)
        return PageImpl(dtoList, pageable, total)

    }

    override fun getStatusById(postId: Long): PostStatus? {
        return queryFactory
                .select(post.status)
                .from(post)
                .where(post.id.eq(postId))
                .fetchOne()
    }

    override fun findReadingHistoryByUserId(userId: Long, request: TimelineRequest): Timeline<PostSummaryDto> {
        val ct = QCategory.category
        val im = QImageSet.imageSet
        val r = QPostReading.postReading

        val whereClause: BooleanExpression
        val orderClause: OrderSpecifier<*>
        val isAfter = request.isAfter
        val instant = Instant.ofEpochSecond(request.timepoint)
        val timepoint = instantToLocalDateTime(instant)

        whereClause = post.status.eq(PostStatus.PUBLISH)
                .and(r.isVisible.isTrue)
                .and(r.userId.eq(userId))
                .and(if (isAfter)
                    r.lastRead.after(timepoint)
                else
                    r.lastRead.before(timepoint)
                )
        orderClause = r.lastRead.desc()

        val query = queryFactory.from(post)
        addProjections(query)
                .innerJoin(post.category, ct)
                .innerJoin(post.postReadings, r)
                .innerJoin(r.post, post)
                .leftJoin(post.titleImage, im)
                .where(whereClause)
                .orderBy(orderClause)
                .limit(request.size.toLong()).distinct()
        val result = query.fetch()
        if (result.size == 0) {
            return Timeline(emptyList(), request, 0)
        }
        val dtoList = toDtoList(result as List<Tuple>)
        val total = queryFactory.selectFrom(post)
                .innerJoin(post.postReadings, r)
                .innerJoin(r.post, post)
                .where(whereClause)
                .fetchCount()
        return Timeline(dtoList, request, total.toInt())
    }

    override fun findBookmarksByUserId(userId: Long, request: TimelineRequest): Timeline<PostSummaryDto> {
        val ct = QCategory.category
        val im = QImageSet.imageSet
        val b = QBookmark.bookmark

        val whereClause: BooleanExpression
        val orderClause: OrderSpecifier<*>
        val isAfter = request.isAfter
        val instant = Instant.ofEpochSecond(request.timepoint)
        val timepoint = instantToLocalDateTime(instant)

        whereClause = post.status.eq(PostStatus.PUBLISH)
                .and(b.userId.eq(userId))
                .and(if (isAfter)
                    b.created.after(timepoint)
                else
                    b.created.before(timepoint)
                )
        orderClause = b.created.desc()

        val query = queryFactory.from(post)
        addProjections(query)
                .innerJoin(post.category, ct)
                .innerJoin(post.postBookmarks, b)
                .innerJoin(b.post, post)
                .leftJoin(post.titleImage, im)
                .where(whereClause)
                .orderBy(orderClause)
                .limit(request.size.toLong()).distinct()
        val result = query.fetch()
        if (result.size == 0) {
            return Timeline(emptyList(), request, 0)
        }
        val dtoList = toDtoList(result as List<Tuple>)
        val total = queryFactory.selectFrom(post)
                .innerJoin(post.postBookmarks, b)
                .innerJoin(b.post, post)
                .where(whereClause)
                .fetchCount()
        return Timeline(dtoList, request, total.toInt())
    }

    override fun fallbackSearch(text: String, pageable: Pageable): Page<PostSummaryDto> {
        val like = "%$text%"
        val query = queryFactory
                .selectDistinct(post.id)
                .from(post)
                .leftJoin(post.postContributors, QPostContributor.postContributor)
                .leftJoin(QPostContributor.postContributor.contributor, QContributor.contributor)
                .leftJoin(post.postTags, QPostTag.postTag)
                .leftJoin(QPostTag.postTag.tag, QTag.tag)
                .orderBy(post.id.desc())
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())

        val builder = BooleanBuilder()
        builder.and(post.status.eq(PostStatus.PUBLISH))
        builder.and(post.title.like(like)
                .or(post.excerpt.like(like))
                .or(post.displayName.like(like))
                .or(QTag.tag.name.like(like))
                .or(QContributor.contributor.fullName.like(like))
        )

        query.where(builder)

        val count = query.fetchCount()
        val ids = query.fetch()

        if (ids.size == 0) {
            return PageImpl(emptyList(), pageable, 0)
        }

        val dtoList = findByIds(ids)
        return PageImpl(dtoList, pageable, count.toInt().toLong())
    }

    private fun addProjections(query: JPAQuery<*>): JPAQuery<*> {
        return query.select(
                post.id, post.categoryId, post.category.name, post.category.slug, post.isSplash, post.isPicked, post.isFeatured,
                post.displayName, post.title, post.publishedAt, post.modifiedAt, post.excerpt, post.titleImage, post.isShowLastUpdated)
    }


    private fun toDtoList(result: List<Tuple>): List<PostSummaryDto> {
        val dtoList = ArrayList<PostSummaryDto>()
        for (row in result) {
            val dto = PostSummaryDto()
            dto.id = row.get(post.id)
            dto.title = row.get(post.title)
            dto.displayName = row.get(post.displayName)
            dto.publishedAt = localDateTimeToInstant(row.get(post.publishedAt)!!)
            dto.modifiedAt = localDateTimeToInstant(row.get(post.modifiedAt)!!)
            dto.excerpt = row.get(post.excerpt)
            row.get(post.isSplash)?.let {
                dto.isSplash = it
            }
            row.get(post.isFeatured)?.let {
                dto.isFeatured = it
            }
            row.get(post.isPicked)?.let {
                dto.isPicked = it
            }
            row.get(post.isShowLastUpdated)?.let {
                dto.isShowLastUpdated = it
            }
            dto.categoryName = row.get(post.category.slug)
            val titleImage = row.get(post.titleImage)
            if (titleImage != null) {
                dto.titleImage = titleImage
            }
            dtoList.add(dto)
        }
        return dtoList
    }
}