package com.octo.rmss.repository;

import com.octo.rmss.domain.Writer;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Writer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WriterRepository extends ReactiveCrudRepository<Writer, UUID>, WriterRepositoryInternal {
    @Override
    <S extends Writer> Mono<S> save(S entity);

    @Override
    Flux<Writer> findAll();

    @Override
    Mono<Writer> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface WriterRepositoryInternal {
    <S extends Writer> Mono<S> save(S entity);

    Flux<Writer> findAllBy(Pageable pageable);

    Flux<Writer> findAll();

    Mono<Writer> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Writer> findAllBy(Pageable pageable, Criteria criteria);

}
