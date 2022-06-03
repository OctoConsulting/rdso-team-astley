package com.octo.rmss.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.octo.rmss.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class NotesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notes.class);
        Notes notes1 = new Notes();
        notes1.setId(UUID.randomUUID());
        Notes notes2 = new Notes();
        notes2.setId(notes1.getId());
        assertThat(notes1).isEqualTo(notes2);
        notes2.setId(UUID.randomUUID());
        assertThat(notes1).isNotEqualTo(notes2);
        notes1.setId(null);
        assertThat(notes1).isNotEqualTo(notes2);
    }
}
