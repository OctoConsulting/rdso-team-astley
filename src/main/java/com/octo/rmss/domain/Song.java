package com.octo.rmss.domain;

import java.io.Serializable;
import java.time.Duration;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Song.
 */
@Table("song")
public class Song implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("title")
    private String title;

    @NotNull(message = "must not be null")
    @Column("writer")
    private String writer;

    @NotNull(message = "must not be null")
    @Column("performer")
    private String performer;

    @NotNull(message = "must not be null")
    @Column("length")
    private Duration length;

    @NotNull(message = "must not be null")
    @Column("soundtrack")
    private String soundtrack;

    @NotNull(message = "must not be null")
    @Column("track_number")
    private Integer trackNumber;

    @Column("url")
    private String url;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Song id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Song title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWriter() {
        return this.writer;
    }

    public Song writer(String writer) {
        this.setWriter(writer);
        return this;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getPerformer() {
        return this.performer;
    }

    public Song performer(String performer) {
        this.setPerformer(performer);
        return this;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public Duration getLength() {
        return this.length;
    }

    public Song length(Duration length) {
        this.setLength(length);
        return this;
    }

    public void setLength(Duration length) {
        this.length = length;
    }

    public String getSoundtrack() {
        return this.soundtrack;
    }

    public Song soundtrack(String soundtrack) {
        this.setSoundtrack(soundtrack);
        return this;
    }

    public void setSoundtrack(String soundtrack) {
        this.soundtrack = soundtrack;
    }

    public Integer getTrackNumber() {
        return this.trackNumber;
    }

    public Song trackNumber(Integer trackNumber) {
        this.setTrackNumber(trackNumber);
        return this;
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getUrl() {
        return this.url;
    }

    public Song url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Song)) {
            return false;
        }
        return id != null && id.equals(((Song) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Song{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", writer='" + getWriter() + "'" +
            ", performer='" + getPerformer() + "'" +
            ", length='" + getLength() + "'" +
            ", soundtrack='" + getSoundtrack() + "'" +
            ", trackNumber=" + getTrackNumber() +
            ", url='" + getUrl() + "'" +
            "}";
    }
}
