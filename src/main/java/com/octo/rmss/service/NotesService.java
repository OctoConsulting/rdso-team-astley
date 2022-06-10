package com.octo.rmss.service;

import com.octo.rmss.domain.Notes;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Notes}.
 */
public interface NotesService {
    /**
     * Save a notes.
     *
     * @param notes the entity to save.
     * @return the persisted entity.
     */
    Mono<Notes> save(Notes notes);

    /**
     * Updates a notes.
     *
     * @param notes the entity to update.
     * @return the persisted entity.
     */
    Mono<Notes> update(Notes notes);

    /**
     * Partially updates a notes.
     *
     * @param notes the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Notes> partialUpdate(Notes notes);

    /**
     * Get all the notes.
     *
     * @return the list of entities.
     */
    Flux<Notes> findAll();

    /**
     * Returns the number of notes available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" notes.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Notes> findOne(Long id);

    /**
     * Delete the "id" notes.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
