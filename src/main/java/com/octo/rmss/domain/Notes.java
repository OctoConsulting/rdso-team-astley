package com.octo.rmss.domain;

import java.io.Serializable;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Notes.
 */
@Table("notes")
public class Notes implements Serializable, Persistable<UUID> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private UUID id;

    @Column("user_id")
    private UUID userId;

    @Column("song_id")
    private UUID songId;

    @Column("note")
    private String note;

    @Transient
    private boolean isPersisted;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public Notes id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
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

    public UUID getSongId() {
        return this.songId;
    }

    public Notes songId(UUID songId) {
        this.setSongId(songId);
        return this;
    }

    public void setSongId(UUID songId) {
        this.songId = songId;
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

    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public Notes setIsPersisted() {
        this.isPersisted = true;
        return this;
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
            ", songId='" + getSongId() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
