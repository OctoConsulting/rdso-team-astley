package com.octo.rmss.service.impl;

import com.octo.rmss.domain.Notes;
import com.octo.rmss.repository.NotesRepository;
import com.octo.rmss.service.NotesService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Notes}.
 */
@Service
@Transactional
public class NotesServiceImpl implements NotesService {

    private final Logger log = LoggerFactory.getLogger(NotesServiceImpl.class);

    private final NotesRepository notesRepository;

    public NotesServiceImpl(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }

    @Override
    public Mono<Notes> save(Notes notes) {
        log.debug("Request to save Notes : {}", notes);
        return notesRepository.save(notes);
    }

    @Override
    public Mono<Notes> update(Notes notes) {
        log.debug("Request to save Notes : {}", notes);
        return notesRepository.save(notes);
    }

    @Override
    public Mono<Notes> partialUpdate(Notes notes) {
        log.debug("Request to partially update Notes : {}", notes);

        return notesRepository
            .findById(notes.getId())
            .map(existingNotes -> {
                if (notes.getUserId() != null) {
                    existingNotes.setUserId(notes.getUserId());
                }
                if (notes.getNote() != null) {
                    existingNotes.setNote(notes.getNote());
                }

                return existingNotes;
            })
            .flatMap(notesRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Notes> findAll() {
        log.debug("Request to get all Notes");
        return notesRepository.findAll();
    }

    public Mono<Long> countAll() {
        return notesRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Notes> findOne(Long id) {
        log.debug("Request to get Notes : {}", id);
        return notesRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Notes : {}", id);
        return notesRepository.deleteById(id);
    }
}
