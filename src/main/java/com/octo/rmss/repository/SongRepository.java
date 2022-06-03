package com.octo.rmss.repository;

import com.octo.rmss.domain.Song;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Song entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SongRepository extends ReactiveCrudRepository<Song, Long>, SongRepositoryInternal {
    @Override
    <S extends Song> Mono<S> save(S entity);

    @Override
    Flux<Song> findAll();

    @Override
    Mono<Song> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SongRepositoryInternal {
    <S extends Song> Mono<S> save(S entity);

    Flux<Song> findAllBy(Pageable pageable);

    Flux<Song> findAll();

    Mono<Song> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Song> findAllBy(Pageable pageable, Criteria criteria);

}
