package kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ColumnKindTest
 *
 * @author Seungho Kang (will.seungho@webtoonscorp.com)
 * @version 1.0.0
 * @since 2024. 07. 07.
 */
class ColumnKindTest {

    @Test
    void find_column_kind() {
        assertThat(ColumnKind.findByCode("partition_key")).isEqualTo(ColumnKind.PARTITION_KEY);
        assertThat(ColumnKind.findByCode("clustering")).isEqualTo(ColumnKind.CLUSTERING);
        assertThat(ColumnKind.findByCode("regular")).isEqualTo(ColumnKind.REGULAR);
    }

    @Test
    void unknown_column_kind() {
        assertThat(ColumnKind.findByCode("blabla")).isEqualTo(ColumnKind.UNKNOWN);
    }

}
