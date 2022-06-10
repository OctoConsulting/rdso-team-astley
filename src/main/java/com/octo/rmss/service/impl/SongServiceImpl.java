package com.octo.rmss.service.impl;

import com.octo.rmss.domain.Song;
import com.octo.rmss.repository.SongRepository;
import com.octo.rmss.service.SongService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Song}.
 */
@Service
@Transactional
public class SongServiceImpl implements SongService {

    private final Logger log = LoggerFactory.getLogger(SongServiceImpl.class);

    private final SongRepository songRepository;

    public SongServiceImpl(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public Mono<Song> save(Song song) {
        log.debug("Request to save Song : {}", song);
        return songRepository.save(song);
    }

    @Override
    public Mono<Song> update(Song song) {
        log.debug("Request to save Song : {}", song);
        return songRepository.save(song);
    }

    @Override
    public Mono<Song> partialUpdate(Song song) {
        log.debug("Request to partially update Song : {}", song);

        return songRepository
            .findById(song.getId())
            .map(existingSong -> {
                if (song.getTitle() != null) {
                    existingSong.setTitle(song.getTitle());
                }
                if (song.getPerformer() != null) {
                    existingSong.setPerformer(song.getPerformer());
                }
                if (song.getLength() != null) {
                    existingSong.setLength(song.getLength());
                }
                if (song.getSoundtrack() != null) {
                    existingSong.setSoundtrack(song.getSoundtrack());
                }
                if (song.getTrackNumber() != null) {
                    existingSong.setTrackNumber(song.getTrackNumber());
                }
                if (song.getUrl() != null) {
                    existingSong.setUrl(song.getUrl());
                }
                if (song.getWriter() != null) {
                    existingSong.setWriter(song.getWriter());
                }

                return existingSong;
            })
            .flatMap(songRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Song> findAll(Pageable pageable) {
        log.debug("Request to get all Songs");
        return songRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return songRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Song> findOne(Long id) {
        log.debug("Request to get Song : {}", id);
        return songRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Song : {}", id);
        return songRepository.deleteById(id);
    }
}
