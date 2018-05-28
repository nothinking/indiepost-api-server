package com.indiepost.model.analytics;

import com.indiepost.enums.Types.LinkType;
import com.indiepost.model.Banner;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by jake on 8/6/17.
 */
@Entity
@Table(name = "Links")
public class Link {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String uid;

    @NotNull
    @Column(nullable = false)
    private String url;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "link")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private List<Click> clicks;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "campaignId")
    private Campaign campaign;

    @Column(name = "campaignId", nullable = false, updatable = false, insertable = false)
    private Long campaignId;

    private LinkType linkType = LinkType.Standard;

    @OneToOne(mappedBy = "link", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Banner banner;

    public LinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(LinkType linkType) {
        this.linkType = linkType;
    }

    public Banner getBanner() {
        return banner;
    }

    public void setBanner(Banner banner) {
        this.banner = banner;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Click> getClicks() {
        return clicks;
    }

    public void setClicks(List<Click> clicks) {
        this.clicks = clicks;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }
}
