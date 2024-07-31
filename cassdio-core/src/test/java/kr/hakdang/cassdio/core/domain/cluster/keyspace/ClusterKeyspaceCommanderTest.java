package kr.hakdang.cassdio.core.domain.cluster.keyspace;

import com.datastax.oss.driver.api.core.Version;
import kr.hakdang.cassdio.IntegrationTest;
import kr.hakdang.cassdio.core.domain.cluster.ClusterUtils;
import kr.hakdang.cassdio.core.domain.cluster.ClusterVersionCommander;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
class ClusterKeyspaceCommanderTest extends IntegrationTest {

    @Autowired
    private ClusterKeyspaceCommander clusterKeyspaceCommander;

    @Autowired
    private ClusterVersionCommander clusterVersionCommander;

    @Test
    void allSystemKeyspaceList() {
        //system keyspace 체크
        List<String> keyspaceNameList = clusterKeyspaceCommander.allKeyspaceNameList(CLUSTER_ID).stream()
            .filter(KeyspaceDTO.KeyspaceNameResult::isSystemKeyspace)
            .map(KeyspaceDTO.KeyspaceNameResult::getKeyspaceName).toList();

        int expectedSize = CassandraSystemKeyspace.onlySystemKeyspaceList().size();
        for (CassandraSystemKeyspace systemKeyspace : CassandraSystemKeyspace.onlySystemKeyspaceList()) {
            Assertions.assertTrue(keyspaceNameList.contains(systemKeyspace.getKeyspaceName()), "Default System Table Empty!!!");
        }

        if (clusterVersionCommander.getCassandraVersion(CLUSTER_ID).compareTo(Version.V4_0_0) >= 0) {
            expectedSize += CassandraSystemKeyspace.virtualSystemKeyspaceList().size();
            for (CassandraSystemKeyspace virtualKeyspace : CassandraSystemKeyspace.virtualSystemKeyspaceList()) {
                Assertions.assertTrue(keyspaceNameList.contains(virtualKeyspace.getKeyspaceName()), "Default Virtual System Table Empty!!!");
            }
        }

        Assertions.assertEquals(expectedSize, keyspaceNameList.size());
    }

    /**
     * 사용자가 생성한 Keyspace 목록
     */
    @Test
    void generalKeyspaceList() {
        //system keyspace 체크
        KeyspaceDTO.ClusterKeyspaceListResult keyspaceNameList = clusterKeyspaceCommander.generalKeyspaceList(CLUSTER_ID);

        for (KeyspaceResult keyspaceResult : keyspaceNameList.getKeyspaceList()) {
            Assertions.assertFalse(
                ClusterUtils.isSystemKeyspace(makeSession().getContext(), keyspaceResult.getKeyspaceName()),
                "include system table"
            );
        }
    }
}
