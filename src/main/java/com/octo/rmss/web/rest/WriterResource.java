package com.octo.rmss.web.rest;

import com.octo.rmss.domain.Writer;
import com.octo.rmss.repository.WriterRepository;
import com.octo.rmss.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.octo.rmss.domain.Writer}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class WriterResource {

    private final Logger log = LoggerFactory.getLogger(WriterResource.class);

    private static final String ENTITY_NAME = "writer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WriterRepository writerRepository;

    public WriterResource(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }

    /**
     * {@code POST  /writers} : Create a new writer.
     *
     * @param writer the writer to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new writer, or with status {@code 400 (Bad Request)} if the writer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/writers")
    public Mono<ResponseEntity<Writer>> createWriter(@RequestBody Writer writer) throws URISyntaxException {
        log.debug("REST request to save Writer : {}", writer);
        if (writer.getId() != null) {
            throw new BadRequestAlertException("A new writer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        writer.setId(UUID.randomUUID());
        return writerRepository
            .save(writer)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/writers/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /writers/:id} : Updates an existing writer.
     *
     * @param id the id of the writer to save.
     * @param writer the writer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated writer,
     * or with status {@code 400 (Bad Request)} if the writer is not valid,
     * or with status {@code 500 (Internal Server Error)} if the writer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/writers/{id}")
    public Mono<ResponseEntity<Writer>> updateWriter(
        @PathVariable(value = "id", required = false) final UUID id,
        @RequestBody Writer writer
    ) throws URISyntaxException {
        log.debug("REST request to update Writer : {}, {}", id, writer);
        if (writer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, writer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return writerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return writerRepository
                    .save(writer.setIsPersisted())
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /writers/:id} : Partial updates given fields of an existing writer, field will ignore if it is null
     *
     * @param id the id of the writer to save.
     * @param writer the writer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated writer,
     * or with status {@code 400 (Bad Request)} if the writer is not valid,
     * or with status {@code 404 (Not Found)} if the writer is not found,
     * or with status {@code 500 (Internal Server Error)} if the writer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/writers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Writer>> partialUpdateWriter(
        @PathVariable(value = "id", required = false) final UUID id,
        @RequestBody Writer writer
    ) throws URISyntaxException {
        log.debug("REST request to partial update Writer partially : {}, {}", id, writer);
        if (writer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, writer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return writerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Writer> result = writerRepository
                    .findById(writer.getId())
                    .map(existingWriter -> {
                        if (writer.getName() != null) {
                            existingWriter.setName(writer.getName());
                        }
                        if (writer.getSongId() != null) {
                            existingWriter.setSongId(writer.getSongId());
                        }

                        return existingWriter;
                    })
                    .flatMap(writerRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /writers} : get all the writers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of writers in body.
     */
    @GetMapping("/writers")
    public Mono<List<Writer>> getAllWriters() {
        log.debug("REST request to get all Writers");
        return writerRepository.findAll().collectList();
    }

    /**
     * {@code GET  /writers} : get all the writers as a stream.
     * @return the {@link Flux} of writers.
     */
    @GetMapping(value = "/writers", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Writer> getAllWritersAsStream() {
        log.debug("REST request to get all Writers as a stream");
        return writerRepository.findAll();
    }

    /**
     * {@code GET  /writers/:id} : get the "id" writer.
     *
     * @param id the id of the writer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the writer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/writers/{id}")
    public Mono<ResponseEntity<Writer>> getWriter(@PathVariable UUID id) {
        log.debug("REST request to get Writer : {}", id);
        Mono<Writer> writer = writerRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(writer);
    }

    /**
     * {@code DELETE  /writers/:id} : delete the "id" writer.
     *
     * @param id the id of the writer to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/writers/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteWriter(@PathVariable UUID id) {
        log.debug("REST request to delete Writer : {}", id);
        return writerRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
