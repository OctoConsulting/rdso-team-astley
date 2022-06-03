package com.octo.rmss.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.octo.rmss.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SongTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Song.class);
        Song song1 = new Song();
        song1.setId(1L);
        Song song2 = new Song();
        song2.setId(song1.getId());
        assertThat(song1).isEqualTo(song2);
        song2.setId(2L);
        assertThat(song1).isNotEqualTo(song2);
        song1.setId(null);
        assertThat(song1).isNotEqualTo(song2);
    }
}
