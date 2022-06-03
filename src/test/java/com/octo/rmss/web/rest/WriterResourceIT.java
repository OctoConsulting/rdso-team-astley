package com.octo.rmss.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.octo.rmss.IntegrationTest;
import com.octo.rmss.domain.Writer;
import com.octo.rmss.repository.EntityManager;
import com.octo.rmss.repository.WriterRepository;
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
 * Integration tests for the {@link WriterResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class WriterResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_SONG_ID = 1L;
    private static final Long UPDATED_SONG_ID = 2L;

    private static final String ENTITY_API_URL = "/api/writers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WriterRepository writerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Writer writer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Writer createEntity(EntityManager em) {
        Writer writer = new Writer().name(DEFAULT_NAME).songId(DEFAULT_SONG_ID);
        return writer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Writer createUpdatedEntity(EntityManager em) {
        Writer writer = new Writer().name(UPDATED_NAME).songId(UPDATED_SONG_ID);
        return writer;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Writer.class).block();
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
        writer = createEntity(em);
    }

    @Test
    void createWriter() throws Exception {
        int databaseSizeBeforeCreate = writerRepository.findAll().collectList().block().size();
        // Create the Writer
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(writer))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Writer in the database
        List<Writer> writerList = writerRepository.findAll().collectList().block();
        assertThat(writerList).hasSize(databaseSizeBeforeCreate + 1);
        Writer testWriter = writerList.get(writerList.size() - 1);
        assertThat(testWriter.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testWriter.getSongId()).isEqualTo(DEFAULT_SONG_ID);
    }

    @Test
    void createWriterWithExistingId() throws Exception {
        // Create the Writer with an existing ID
        writer.setId(1L);

        int databaseSizeBeforeCreate = writerRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(writer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Writer in the database
        List<Writer> writerList = writerRepository.findAll().collectList().block();
        assertThat(writerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllWritersAsStream() {
        // Initialize the database
        writerRepository.save(writer).block();

        List<Writer> writerList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Writer.class)
            .getResponseBody()
            .filter(writer::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(writerList).isNotNull();
        assertThat(writerList).hasSize(1);
        Writer testWriter = writerList.get(0);
        assertThat(testWriter.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testWriter.getSongId()).isEqualTo(DEFAULT_SONG_ID);
    }

    @Test
    void getAllWriters() {
        // Initialize the database
        writerRepository.save(writer).block();

        // Get all the writerList
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
            .value(hasItem(writer.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].songId")
            .value(hasItem(DEFAULT_SONG_ID.intValue()));
    }

    @Test
    void getWriter() {
        // Initialize the database
        writerRepository.save(writer).block();

        // Get the writer
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, writer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(writer.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.songId")
            .value(is(DEFAULT_SONG_ID.intValue()));
    }

    @Test
    void getNonExistingWriter() {
        // Get the writer
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewWriter() throws Exception {
        // Initialize the database
        writerRepository.save(writer).block();

        int databaseSizeBeforeUpdate = writerRepository.findAll().collectList().block().size();

        // Update the writer
        Writer updatedWriter = writerRepository.findById(writer.getId()).block();
        updatedWriter.name(UPDATED_NAME).songId(UPDATED_SONG_ID);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedWriter.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedWriter))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Writer in the database
        List<Writer> writerList = writerRepository.findAll().collectList().block();
        assertThat(writerList).hasSize(databaseSizeBeforeUpdate);
        Writer testWriter = writerList.get(writerList.size() - 1);
        assertThat(testWriter.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWriter.getSongId()).isEqualTo(UPDATED_SONG_ID);
    }

    @Test
    void putNonExistingWriter() throws Exception {
        int databaseSizeBeforeUpdate = writerRepository.findAll().collectList().block().size();
        writer.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, writer.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(writer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Writer in the database
        List<Writer> writerList = writerRepository.findAll().collectList().block();
        assertThat(writerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchWriter() throws Exception {
        int databaseSizeBeforeUpdate = writerRepository.findAll().collectList().block().size();
        writer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(writer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Writer in the database
        List<Writer> writerList = writerRepository.findAll().collectList().block();
        assertThat(writerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamWriter() throws Exception {
        int databaseSizeBeforeUpdate = writerRepository.findAll().collectList().block().size();
        writer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(writer))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Writer in the database
        List<Writer> writerList = writerRepository.findAll().collectList().block();
        assertThat(writerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateWriterWithPatch() throws Exception {
        // Initialize the database
        writerRepository.save(writer).block();

        int databaseSizeBeforeUpdate = writerRepository.findAll().collectList().block().size();

        // Update the writer using partial update
        Writer partialUpdatedWriter = new Writer();
        partialUpdatedWriter.setId(writer.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedWriter.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedWriter))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Writer in the database
        List<Writer> writerList = writerRepository.findAll().collectList().block();
        assertThat(writerList).hasSize(databaseSizeBeforeUpdate);
        Writer testWriter = writerList.get(writerList.size() - 1);
        assertThat(testWriter.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testWriter.getSongId()).isEqualTo(DEFAULT_SONG_ID);
    }

    @Test
    void fullUpdateWriterWithPatch() throws Exception {
        // Initialize the database
        writerRepository.save(writer).block();

        int databaseSizeBeforeUpdate = writerRepository.findAll().collectList().block().size();

        // Update the writer using partial update
        Writer partialUpdatedWriter = new Writer();
        partialUpdatedWriter.setId(writer.getId());

        partialUpdatedWriter.name(UPDATED_NAME).songId(UPDATED_SONG_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedWriter.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedWriter))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Writer in the database
        List<Writer> writerList = writerRepository.findAll().collectList().block();
        assertThat(writerList).hasSize(databaseSizeBeforeUpdate);
        Writer testWriter = writerList.get(writerList.size() - 1);
        assertThat(testWriter.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWriter.getSongId()).isEqualTo(UPDATED_SONG_ID);
    }

    @Test
    void patchNonExistingWriter() throws Exception {
        int databaseSizeBeforeUpdate = writerRepository.findAll().collectList().block().size();
        writer.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, writer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(writer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Writer in the database
        List<Writer> writerList = writerRepository.findAll().collectList().block();
        assertThat(writerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchWriter() throws Exception {
        int databaseSizeBeforeUpdate = writerRepository.findAll().collectList().block().size();
        writer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(writer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Writer in the database
        List<Writer> writerList = writerRepository.findAll().collectList().block();
        assertThat(writerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamWriter() throws Exception {
        int databaseSizeBeforeUpdate = writerRepository.findAll().collectList().block().size();
        writer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(writer))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Writer in the database
        List<Writer> writerList = writerRepository.findAll().collectList().block();
        assertThat(writerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteWriter() {
        // Initialize the database
        writerRepository.save(writer).block();

        int databaseSizeBeforeDelete = writerRepository.findAll().collectList().block().size();

        // Delete the writer
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, writer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Writer> writerList = writerRepository.findAll().collectList().block();
        assertThat(writerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
