package com.octo.rmss.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.octo.rmss.IntegrationTest;
import com.octo.rmss.domain.Notes;
import com.octo.rmss.repository.EntityManager;
import com.octo.rmss.repository.NotesRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.UUID;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link NotesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class NotesResourceIT {

    private static final UUID DEFAULT_USER_ID = UUID.randomUUID();
    private static final UUID UPDATED_USER_ID = UUID.randomUUID();

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/notes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Notes notes;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notes createEntity(EntityManager em) {
        Notes notes = new Notes().userId(DEFAULT_USER_ID).note(DEFAULT_NOTE);
        return notes;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notes createUpdatedEntity(EntityManager em) {
        Notes notes = new Notes().userId(UPDATED_USER_ID).note(UPDATED_NOTE);
        return notes;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Notes.class).block();
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
        notes = createEntity(em);
    }

    @Test
    void createNotes() throws Exception {
        int databaseSizeBeforeCreate = notesRepository.findAll().collectList().block().size();
        // Create the Notes
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notes))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Notes in the database
        List<Notes> notesList = notesRepository.findAll().collectList().block();
        assertThat(notesList).hasSize(databaseSizeBeforeCreate + 1);
        Notes testNotes = notesList.get(notesList.size() - 1);
        assertThat(testNotes.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testNotes.getNote()).isEqualTo(DEFAULT_NOTE);
    }

    @Test
    void createNotesWithExistingId() throws Exception {
        // Create the Notes with an existing ID
        notes.setId(1L);

        int databaseSizeBeforeCreate = notesRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notes))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notes in the database
        List<Notes> notesList = notesRepository.findAll().collectList().block();
        assertThat(notesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllNotesAsStream() {
        // Initialize the database
        notesRepository.save(notes).block();

        List<Notes> notesList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Notes.class)
            .getResponseBody()
            .filter(notes::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(notesList).isNotNull();
        assertThat(notesList).hasSize(1);
        Notes testNotes = notesList.get(0);
        assertThat(testNotes.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testNotes.getNote()).isEqualTo(DEFAULT_NOTE);
    }

    @Test
    void getAllNotes() {
        // Initialize the database
        notesRepository.save(notes).block();

        // Get all the notesList
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
            .value(hasItem(notes.getId().intValue()))
            .jsonPath("$.[*].userId")
            .value(hasItem(DEFAULT_USER_ID.toString()))
            .jsonPath("$.[*].note")
            .value(hasItem(DEFAULT_NOTE.toString()));
    }

    @Test
    void getNotes() {
        // Initialize the database
        notesRepository.save(notes).block();

        // Get the notes
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, notes.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(notes.getId().intValue()))
            .jsonPath("$.userId")
            .value(is(DEFAULT_USER_ID.toString()))
            .jsonPath("$.note")
            .value(is(DEFAULT_NOTE.toString()));
    }

    @Test
    void getNonExistingNotes() {
        // Get the notes
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewNotes() throws Exception {
        // Initialize the database
        notesRepository.save(notes).block();

        int databaseSizeBeforeUpdate = notesRepository.findAll().collectList().block().size();

        // Update the notes
        Notes updatedNotes = notesRepository.findById(notes.getId()).block();
        updatedNotes.userId(UPDATED_USER_ID).note(UPDATED_NOTE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedNotes.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedNotes))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Notes in the database
        List<Notes> notesList = notesRepository.findAll().collectList().block();
        assertThat(notesList).hasSize(databaseSizeBeforeUpdate);
        Notes testNotes = notesList.get(notesList.size() - 1);
        assertThat(testNotes.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testNotes.getNote()).isEqualTo(UPDATED_NOTE);
    }

    @Test
    void putNonExistingNotes() throws Exception {
        int databaseSizeBeforeUpdate = notesRepository.findAll().collectList().block().size();
        notes.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, notes.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notes))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notes in the database
        List<Notes> notesList = notesRepository.findAll().collectList().block();
        assertThat(notesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchNotes() throws Exception {
        int databaseSizeBeforeUpdate = notesRepository.findAll().collectList().block().size();
        notes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notes))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notes in the database
        List<Notes> notesList = notesRepository.findAll().collectList().block();
        assertThat(notesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamNotes() throws Exception {
        int databaseSizeBeforeUpdate = notesRepository.findAll().collectList().block().size();
        notes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notes))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Notes in the database
        List<Notes> notesList = notesRepository.findAll().collectList().block();
        assertThat(notesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateNotesWithPatch() throws Exception {
        // Initialize the database
        notesRepository.save(notes).block();

        int databaseSizeBeforeUpdate = notesRepository.findAll().collectList().block().size();

        // Update the notes using partial update
        Notes partialUpdatedNotes = new Notes();
        partialUpdatedNotes.setId(notes.getId());

        partialUpdatedNotes.userId(UPDATED_USER_ID).note(UPDATED_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNotes.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedNotes))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Notes in the database
        List<Notes> notesList = notesRepository.findAll().collectList().block();
        assertThat(notesList).hasSize(databaseSizeBeforeUpdate);
        Notes testNotes = notesList.get(notesList.size() - 1);
        assertThat(testNotes.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testNotes.getNote()).isEqualTo(UPDATED_NOTE);
    }

    @Test
    void fullUpdateNotesWithPatch() throws Exception {
        // Initialize the database
        notesRepository.save(notes).block();

        int databaseSizeBeforeUpdate = notesRepository.findAll().collectList().block().size();

        // Update the notes using partial update
        Notes partialUpdatedNotes = new Notes();
        partialUpdatedNotes.setId(notes.getId());

        partialUpdatedNotes.userId(UPDATED_USER_ID).note(UPDATED_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNotes.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedNotes))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Notes in the database
        List<Notes> notesList = notesRepository.findAll().collectList().block();
        assertThat(notesList).hasSize(databaseSizeBeforeUpdate);
        Notes testNotes = notesList.get(notesList.size() - 1);
        assertThat(testNotes.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testNotes.getNote()).isEqualTo(UPDATED_NOTE);
    }

    @Test
    void patchNonExistingNotes() throws Exception {
        int databaseSizeBeforeUpdate = notesRepository.findAll().collectList().block().size();
        notes.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, notes.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(notes))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notes in the database
        List<Notes> notesList = notesRepository.findAll().collectList().block();
        assertThat(notesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchNotes() throws Exception {
        int databaseSizeBeforeUpdate = notesRepository.findAll().collectList().block().size();
        notes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(notes))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notes in the database
        List<Notes> notesList = notesRepository.findAll().collectList().block();
        assertThat(notesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamNotes() throws Exception {
        int databaseSizeBeforeUpdate = notesRepository.findAll().collectList().block().size();
        notes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(notes))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Notes in the database
        List<Notes> notesList = notesRepository.findAll().collectList().block();
        assertThat(notesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteNotes() {
        // Initialize the database
        notesRepository.save(notes).block();

        int databaseSizeBeforeDelete = notesRepository.findAll().collectList().block().size();

        // Delete the notes
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, notes.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Notes> notesList = notesRepository.findAll().collectList().block();
        assertThat(notesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
