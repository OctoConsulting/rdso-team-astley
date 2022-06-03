package com.octo.rmss.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.octo.rmss.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WriterTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Writer.class);
        Writer writer1 = new Writer();
        writer1.setId(1L);
        Writer writer2 = new Writer();
        writer2.setId(writer1.getId());
        assertThat(writer1).isEqualTo(writer2);
        writer2.setId(2L);
        assertThat(writer1).isNotEqualTo(writer2);
        writer1.setId(null);
        assertThat(writer1).isNotEqualTo(writer2);
    }
}
