package com.octo.rmss.service;

import com.octo.rmss.domain.Song;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Song}.
 */
public interface SongService {
    /**
     * Save a song.
     *
     * @param song the entity to save.
     * @return the persisted entity.
     */
    Mono<Song> save(Song song);

    /**
     * Updates a song.
     *
     * @param song the entity to update.
     * @return the persisted entity.
     */
    Mono<Song> update(Song song);

    /**
     * Partially updates a song.
     *
     * @param song the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Song> partialUpdate(Song song);

    /**
     * Get all the songs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Song> findAll(Pageable pageable);

    /**
     * Returns the number of songs available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" song.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Song> findOne(Long id);

    /**
     * Delete the "id" song.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
