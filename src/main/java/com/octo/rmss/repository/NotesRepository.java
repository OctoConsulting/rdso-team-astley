package com.octo.rmss.repository;

import com.octo.rmss.domain.Notes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Notes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotesRepository extends ReactiveCrudRepository<Notes, Long>, NotesRepositoryInternal {
    @Query("SELECT * FROM notes entity WHERE entity.song_id = :id")
    Flux<Notes> findBySong(Long id);

    @Query("SELECT * FROM notes entity WHERE entity.song_id IS NULL")
    Flux<Notes> findAllWhereSongIsNull();

    @Override
    <S extends Notes> Mono<S> save(S entity);

    @Override
    Flux<Notes> findAll();

    @Override
    Mono<Notes> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface NotesRepositoryInternal {
    <S extends Notes> Mono<S> save(S entity);

    Flux<Notes> findAllBy(Pageable pageable);

    Flux<Notes> findAll();

    Mono<Notes> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Notes> findAllBy(Pageable pageable, Criteria criteria);

}
