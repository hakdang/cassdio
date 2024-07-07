package kr.hakdang.cassdio.core.domain.cluster;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ClusterQueryCommanderArgsTest
 *
 * @author Seungho Kang (will.seungho@webtoonscorp.com)
 * @version 1.0.0
 * @since 2024. 07. 07.
 */
class ClusterQueryCommanderArgsTest {

    @Test
    void default_pageSize_is_50() {
        // given
        ClusterQueryCommanderArgs args = ClusterQueryCommanderArgs.builder().build();

        // when
        int sut = args.getPageSize();

        // then
        assertThat(sut).isEqualTo(50);
    }

    @Test
    void when_pageSize_is_over_500_throw_exception() {
        // when & then
        assertThatThrownBy(() ->
            ClusterQueryCommanderArgs.builder()
                .pageSize(501)
                .build()
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    void default_timeout_is_3s() {
        // given
        ClusterQueryCommanderArgs args = ClusterQueryCommanderArgs.builder().build();

        // when
        int sut = args.getTimeoutSeconds();

        // then
        assertThat(sut).isEqualTo(3);
    }

    @Test
    void when_timeout_is_over_1m_throw_exception() {
        // when & then
        assertThatThrownBy(() ->
            ClusterQueryCommanderArgs.builder()
                .timeoutSeconds(61)
                .build()
        ).isInstanceOf(RuntimeException.class);
    }

}
