package kr.hakdang.cassdio.core.domain.cluster.compaction;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassandraSystemKeyspace;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.CassandraSystemTable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * CompactionHistoryListCommander
 *
 * @author seungh0
 * @since 2024-07-25
 */
@Service
public class CompactionHistoryListCommander extends BaseClusterCommander {

    public CompactionHistoryListResult getCompactionHistories(CqlSession session, String keyspace) {
        SimpleStatement statement = QueryBuilder
            .selectFrom(CassandraSystemKeyspace.SYSTEM.getKeyspaceName(), CassandraSystemTable.SYSTEM_COMPACTION_HISTORY.getTableName())
            .all()
            .build();

        ResultSet rs = session.execute(statement);

        List<CompactionHistory> historyList = StreamSupport.stream(rs.spliterator(), false)
            .map(CompactionHistory::from)
            .filter(history -> StringUtils.isBlank(keyspace) || history.getKeyspaceName().equals(keyspace))
            .sorted(Comparator.comparing(CompactionHistory::getCompactedAt).reversed())
            .collect(Collectors.toList());

        return CompactionHistoryListResult.builder()
            .histories(historyList)
            .build();
    }

}
