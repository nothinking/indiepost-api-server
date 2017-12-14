package com.indiepost.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by jake on 10/29/17.
 */
@Entity
@Table(name = "Metadata")
public class Metadata implements Serializable {

    private static final long serialVersionUID = 3184119725585457013L;

    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime postStatsLastUpdated;

    private LocalDateTime searchIndexLastUpdated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getPostStatsLastUpdated() {
        return postStatsLastUpdated;
    }

    public void setPostStatsLastUpdated(LocalDateTime postStatsLastUpdated) {
        this.postStatsLastUpdated = postStatsLastUpdated;
    }

    public LocalDateTime getSearchIndexLastUpdated() {
        return searchIndexLastUpdated;
    }

    public void setSearchIndexLastUpdated(LocalDateTime searchIndexLastUpdated) {
        this.searchIndexLastUpdated = searchIndexLastUpdated;
    }
}