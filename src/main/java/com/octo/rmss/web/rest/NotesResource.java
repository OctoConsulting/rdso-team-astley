package com.octo.rmss.web.rest;

import com.octo.rmss.domain.Notes;
import com.octo.rmss.repository.NotesRepository;
import com.octo.rmss.service.NotesService;
import com.octo.rmss.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.octo.rmss.domain.Notes}.
 */
@RestController
@RequestMapping("/api")
public class NotesResource {

    private final Logger log = LoggerFactory.getLogger(NotesResource.class);

    private static final String ENTITY_NAME = "notes";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NotesService notesService;

    private final NotesRepository notesRepository;

    public NotesResource(NotesService notesService, NotesRepository notesRepository) {
        this.notesService = notesService;
        this.notesRepository = notesRepository;
    }

    /**
     * {@code POST  /notes} : Create a new notes.
     *
     * @param notes the notes to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notes, or with status {@code 400 (Bad Request)} if the notes has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/notes")
    public Mono<ResponseEntity<Notes>> createNotes(@RequestBody Notes notes) throws URISyntaxException {
        log.debug("REST request to save Notes : {}", notes);
        if (notes.getId() != null) {
            throw new BadRequestAlertException("A new notes cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return notesService
            .save(notes)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/notes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /notes/:id} : Updates an existing notes.
     *
     * @param id the id of the notes to save.
     * @param notes the notes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notes,
     * or with status {@code 400 (Bad Request)} if the notes is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/notes/{id}")
    public Mono<ResponseEntity<Notes>> updateNotes(@PathVariable(value = "id", required = false) final Long id, @RequestBody Notes notes)
        throws URISyntaxException {
        log.debug("REST request to update Notes : {}, {}", id, notes);
        if (notes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return notesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return notesService
                    .update(notes)
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
     * {@code PATCH  /notes/:id} : Partial updates given fields of an existing notes, field will ignore if it is null
     *
     * @param id the id of the notes to save.
     * @param notes the notes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notes,
     * or with status {@code 400 (Bad Request)} if the notes is not valid,
     * or with status {@code 404 (Not Found)} if the notes is not found,
     * or with status {@code 500 (Internal Server Error)} if the notes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/notes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Notes>> partialUpdateNotes(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Notes notes
    ) throws URISyntaxException {
        log.debug("REST request to partial update Notes partially : {}, {}", id, notes);
        if (notes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return notesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Notes> result = notesService.partialUpdate(notes);

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
     * {@code GET  /notes} : get all the notes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notes in body.
     */
    @GetMapping("/notes")
    public Mono<List<Notes>> getAllNotes() {
        log.debug("REST request to get all Notes");
        return notesService.findAll().collectList();
    }

    /**
     * {@code GET  /notes} : get all the notes as a stream.
     * @return the {@link Flux} of notes.
     */
    @GetMapping(value = "/notes", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Notes> getAllNotesAsStream() {
        log.debug("REST request to get all Notes as a stream");
        return notesService.findAll();
    }

    /**
     * {@code GET  /notes/:id} : get the "id" notes.
     *
     * @param id the id of the notes to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notes, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/notes/{id}")
    public Mono<ResponseEntity<Notes>> getNotes(@PathVariable Long id) {
        log.debug("REST request to get Notes : {}", id);
        Mono<Notes> notes = notesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(notes);
    }

    /**
     * {@code DELETE  /notes/:id} : delete the "id" notes.
     *
     * @param id the id of the notes to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/notes/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteNotes(@PathVariable Long id) {
        log.debug("REST request to delete Notes : {}", id);
        return notesService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
