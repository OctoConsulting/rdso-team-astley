package com.octo.rmss.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Notes.
 */
@Table("notes")
public class Notes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("user_id")
    private UUID userId;

    @Column("note")
    private String note;

    @Transient
    private Song song;

    @Column("song_id")
    private Long songId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Notes id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUserId() {
        return this.userId;
    }

    public Notes userId(UUID userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getNote() {
        return this.note;
    }

    public Notes note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Song getSong() {
        return this.song;
    }

    public void setSong(Song song) {
        this.song = song;
        this.songId = song != null ? song.getId() : null;
    }

    public Notes song(Song song) {
        this.setSong(song);
        return this;
    }

    public Long getSongId() {
        return this.songId;
    }

    public void setSongId(Long song) {
        this.songId = song;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notes)) {
            return false;
        }
        return id != null && id.equals(((Notes) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Notes{" +
            "id=" + getId() +
            ", userId='" + getUserId() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
