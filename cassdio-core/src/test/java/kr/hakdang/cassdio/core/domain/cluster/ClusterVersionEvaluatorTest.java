package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.Version;
import kr.hakdang.cassdio.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ClusterVersionEvaluatorTest
 *
 * @author seungh0
 * @since 2024-07-31
 */
class ClusterVersionEvaluatorTest extends BaseTest {

    @Mock
    private ClusterVersionGetCommander clusterVersionGetCommander;

    private ClusterVersionEvaluator clusterVersionEvaluator;

    @BeforeEach
    void setup() {
        clusterVersionEvaluator = new ClusterVersionEvaluator(clusterVersionGetCommander);
    }

    @Test
    void isLessThan() {
        // given
        BDDMockito.when(clusterVersionGetCommander.getCassandraVersion(CLUSTER_ID)).thenReturn(Version.parse("4.0.1"));

        // when
        assertThat(clusterVersionEvaluator.isLessThan(CLUSTER_ID, Version.parse("4.0.0"))).isFalse();
        assertThat(clusterVersionEvaluator.isLessThan(CLUSTER_ID, Version.parse("4.0.1"))).isFalse();
        assertThat(clusterVersionEvaluator.isLessThan(CLUSTER_ID, Version.parse("4.0.2"))).isTrue();
    }

    @Test
    void isLessThanOrEqual() {
        // given
        BDDMockito.when(clusterVersionGetCommander.getCassandraVersion(CLUSTER_ID)).thenReturn(Version.parse("4.0.1"));

        // when
        assertThat(clusterVersionEvaluator.isLessThanOrEqual(CLUSTER_ID, Version.parse("4.0.0"))).isFalse();
        assertThat(clusterVersionEvaluator.isLessThanOrEqual(CLUSTER_ID, Version.parse("4.0.1"))).isTrue();
        assertThat(clusterVersionEvaluator.isLessThanOrEqual(CLUSTER_ID, Version.parse("4.0.2"))).isTrue();
    }

    @Test
    void isGreaterThan() {
        // given
        BDDMockito.when(clusterVersionGetCommander.getCassandraVersion(CLUSTER_ID)).thenReturn(Version.parse("4.0.1"));

        // when
        assertThat(clusterVersionEvaluator.isGreaterThan(CLUSTER_ID, Version.parse("4.0.0"))).isTrue();
        assertThat(clusterVersionEvaluator.isGreaterThan(CLUSTER_ID, Version.parse("4.0.1"))).isFalse();
        assertThat(clusterVersionEvaluator.isGreaterThan(CLUSTER_ID, Version.parse("4.0.2"))).isFalse();
    }

    @Test
    void isGreaterThanOrEqual() {
        // given
        BDDMockito.when(clusterVersionGetCommander.getCassandraVersion(CLUSTER_ID)).thenReturn(Version.parse("4.0.1"));

        // when
        assertThat(clusterVersionEvaluator.isGreaterThanOrEqual(CLUSTER_ID, Version.parse("4.0.0"))).isTrue();
        assertThat(clusterVersionEvaluator.isGreaterThanOrEqual(CLUSTER_ID, Version.parse("4.0.1"))).isTrue();
        assertThat(clusterVersionEvaluator.isGreaterThanOrEqual(CLUSTER_ID, Version.parse("4.0.2"))).isFalse();
    }

    @Test
    void isEqual() {
        // given
        BDDMockito.when(clusterVersionGetCommander.getCassandraVersion(CLUSTER_ID)).thenReturn(Version.parse("4.0.1"));

        // when
        assertThat(clusterVersionEvaluator.isEqual(CLUSTER_ID, Version.parse("4.0.0"))).isFalse();
        assertThat(clusterVersionEvaluator.isEqual(CLUSTER_ID, Version.parse("4.0.1"))).isTrue();
        assertThat(clusterVersionEvaluator.isEqual(CLUSTER_ID, Version.parse("4.0.2"))).isFalse();
    }

}
