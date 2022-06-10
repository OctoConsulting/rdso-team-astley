package com.octo.rmss.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.octo.rmss.IntegrationTest;
import com.octo.rmss.domain.Song;
import com.octo.rmss.repository.EntityManager;
import com.octo.rmss.repository.SongRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link SongResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SongResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_PERFORMER = "AAAAAAAAAA";
    private static final String UPDATED_PERFORMER = "BBBBBBBBBB";

    private static final Duration DEFAULT_LENGTH = Duration.ofHours(6);
    private static final Duration UPDATED_LENGTH = Duration.ofHours(12);

    private static final String DEFAULT_SOUNDTRACK = "AAAAAAAAAA";
    private static final String UPDATED_SOUNDTRACK = "BBBBBBBBBB";

    private static final Integer DEFAULT_TRACK_NUMBER = 1;
    private static final Integer UPDATED_TRACK_NUMBER = 2;

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_WRITER = "AAAAAAAAAA";
    private static final String UPDATED_WRITER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/songs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Song song;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Song createEntity(EntityManager em) {
        Song song = new Song()
            .title(DEFAULT_TITLE)
            .performer(DEFAULT_PERFORMER)
            .length(DEFAULT_LENGTH)
            .soundtrack(DEFAULT_SOUNDTRACK)
            .trackNumber(DEFAULT_TRACK_NUMBER)
            .url(DEFAULT_URL)
            .writer(DEFAULT_WRITER);
        return song;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Song createUpdatedEntity(EntityManager em) {
        Song song = new Song()
            .title(UPDATED_TITLE)
            .performer(UPDATED_PERFORMER)
            .length(UPDATED_LENGTH)
            .soundtrack(UPDATED_SOUNDTRACK)
            .trackNumber(UPDATED_TRACK_NUMBER)
            .url(UPDATED_URL)
            .writer(UPDATED_WRITER);
        return song;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Song.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        song = createEntity(em);
    }

    @Test
    void createSong() throws Exception {
        int databaseSizeBeforeCreate = songRepository.findAll().collectList().block().size();
        // Create the Song
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(song))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeCreate + 1);
        Song testSong = songList.get(songList.size() - 1);
        assertThat(testSong.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSong.getPerformer()).isEqualTo(DEFAULT_PERFORMER);
        assertThat(testSong.getLength()).isEqualTo(DEFAULT_LENGTH);
        assertThat(testSong.getSoundtrack()).isEqualTo(DEFAULT_SOUNDTRACK);
        assertThat(testSong.getTrackNumber()).isEqualTo(DEFAULT_TRACK_NUMBER);
        assertThat(testSong.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testSong.getWriter()).isEqualTo(DEFAULT_WRITER);
    }

    @Test
    void createSongWithExistingId() throws Exception {
        // Create the Song with an existing ID
        song.setId(1L);

        int databaseSizeBeforeCreate = songRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(song))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllSongs() {
        // Initialize the database
        songRepository.save(song).block();

        // Get all the songList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(song.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].performer")
            .value(hasItem(DEFAULT_PERFORMER))
            .jsonPath("$.[*].length")
            .value(hasItem(DEFAULT_LENGTH.toString()))
            .jsonPath("$.[*].soundtrack")
            .value(hasItem(DEFAULT_SOUNDTRACK))
            .jsonPath("$.[*].trackNumber")
            .value(hasItem(DEFAULT_TRACK_NUMBER))
            .jsonPath("$.[*].url")
            .value(hasItem(DEFAULT_URL))
            .jsonPath("$.[*].writer")
            .value(hasItem(DEFAULT_WRITER));
    }

    @Test
    void getSong() {
        // Initialize the database
        songRepository.save(song).block();

        // Get the song
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, song.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(song.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.performer")
            .value(is(DEFAULT_PERFORMER))
            .jsonPath("$.length")
            .value(is(DEFAULT_LENGTH.toString()))
            .jsonPath("$.soundtrack")
            .value(is(DEFAULT_SOUNDTRACK))
            .jsonPath("$.trackNumber")
            .value(is(DEFAULT_TRACK_NUMBER))
            .jsonPath("$.url")
            .value(is(DEFAULT_URL))
            .jsonPath("$.writer")
            .value(is(DEFAULT_WRITER));
    }

    @Test
    void getNonExistingSong() {
        // Get the song
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewSong() throws Exception {
        // Initialize the database
        songRepository.save(song).block();

        int databaseSizeBeforeUpdate = songRepository.findAll().collectList().block().size();

        // Update the song
        Song updatedSong = songRepository.findById(song.getId()).block();
        updatedSong
            .title(UPDATED_TITLE)
            .performer(UPDATED_PERFORMER)
            .length(UPDATED_LENGTH)
            .soundtrack(UPDATED_SOUNDTRACK)
            .trackNumber(UPDATED_TRACK_NUMBER)
            .url(UPDATED_URL)
            .writer(UPDATED_WRITER);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedSong.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedSong))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
        Song testSong = songList.get(songList.size() - 1);
        assertThat(testSong.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSong.getPerformer()).isEqualTo(UPDATED_PERFORMER);
        assertThat(testSong.getLength()).isEqualTo(UPDATED_LENGTH);
        assertThat(testSong.getSoundtrack()).isEqualTo(UPDATED_SOUNDTRACK);
        assertThat(testSong.getTrackNumber()).isEqualTo(UPDATED_TRACK_NUMBER);
        assertThat(testSong.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testSong.getWriter()).isEqualTo(UPDATED_WRITER);
    }

    @Test
    void putNonExistingSong() throws Exception {
        int databaseSizeBeforeUpdate = songRepository.findAll().collectList().block().size();
        song.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, song.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(song))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSong() throws Exception {
        int databaseSizeBeforeUpdate = songRepository.findAll().collectList().block().size();
        song.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(song))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSong() throws Exception {
        int databaseSizeBeforeUpdate = songRepository.findAll().collectList().block().size();
        song.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(song))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSongWithPatch() throws Exception {
        // Initialize the database
        songRepository.save(song).block();

        int databaseSizeBeforeUpdate = songRepository.findAll().collectList().block().size();

        // Update the song using partial update
        Song partialUpdatedSong = new Song();
        partialUpdatedSong.setId(song.getId());

        partialUpdatedSong
            .title(UPDATED_TITLE)
            .performer(UPDATED_PERFORMER)
            .length(UPDATED_LENGTH)
            .soundtrack(UPDATED_SOUNDTRACK)
            .url(UPDATED_URL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSong.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSong))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
        Song testSong = songList.get(songList.size() - 1);
        assertThat(testSong.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSong.getPerformer()).isEqualTo(UPDATED_PERFORMER);
        assertThat(testSong.getLength()).isEqualTo(UPDATED_LENGTH);
        assertThat(testSong.getSoundtrack()).isEqualTo(UPDATED_SOUNDTRACK);
        assertThat(testSong.getTrackNumber()).isEqualTo(DEFAULT_TRACK_NUMBER);
        assertThat(testSong.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testSong.getWriter()).isEqualTo(DEFAULT_WRITER);
    }

    @Test
    void fullUpdateSongWithPatch() throws Exception {
        // Initialize the database
        songRepository.save(song).block();

        int databaseSizeBeforeUpdate = songRepository.findAll().collectList().block().size();

        // Update the song using partial update
        Song partialUpdatedSong = new Song();
        partialUpdatedSong.setId(song.getId());

        partialUpdatedSong
            .title(UPDATED_TITLE)
            .performer(UPDATED_PERFORMER)
            .length(UPDATED_LENGTH)
            .soundtrack(UPDATED_SOUNDTRACK)
            .trackNumber(UPDATED_TRACK_NUMBER)
            .url(UPDATED_URL)
            .writer(UPDATED_WRITER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSong.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSong))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
        Song testSong = songList.get(songList.size() - 1);
        assertThat(testSong.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSong.getPerformer()).isEqualTo(UPDATED_PERFORMER);
        assertThat(testSong.getLength()).isEqualTo(UPDATED_LENGTH);
        assertThat(testSong.getSoundtrack()).isEqualTo(UPDATED_SOUNDTRACK);
        assertThat(testSong.getTrackNumber()).isEqualTo(UPDATED_TRACK_NUMBER);
        assertThat(testSong.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testSong.getWriter()).isEqualTo(UPDATED_WRITER);
    }

    @Test
    void patchNonExistingSong() throws Exception {
        int databaseSizeBeforeUpdate = songRepository.findAll().collectList().block().size();
        song.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, song.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(song))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSong() throws Exception {
        int databaseSizeBeforeUpdate = songRepository.findAll().collectList().block().size();
        song.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(song))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSong() throws Exception {
        int databaseSizeBeforeUpdate = songRepository.findAll().collectList().block().size();
        song.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(song))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSong() {
        // Initialize the database
        songRepository.save(song).block();

        int databaseSizeBeforeDelete = songRepository.findAll().collectList().block().size();

        // Delete the song
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, song.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
