package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.DefaultBatchType;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.insert.InsertInto;
import com.datastax.oss.driver.api.querybuilder.insert.JsonInsert;
import com.datastax.oss.protocol.internal.util.Bytes;
import com.google.common.collect.Lists;
import kr.hakdang.cassdio.common.utils.Jsons;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResults;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassdioColumnDefinition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * ClusterTableRowCommander
 *
 * @author akageun
 * @since 2024-07-25
 */
@Slf4j
@Service
public class ClusterTableRowCommander extends BaseClusterCommander {

    public CqlSessionSelectResults rowSelect(String clusterId, TableDTO.ClusterTableRowArgs args) {
        CqlSession session = cqlSessionFactory.get(clusterId);

        SimpleStatement statement = QueryBuilder.selectFrom(args.getKeyspace(), args.getTable())
            .all()
            .build()
            .setPageSize(args.getPageSize())
            .setTimeout(Duration.ofSeconds(3))  // 3s timeout
            .setPagingState(StringUtils.isNotBlank(args.getCursor()) ? Bytes.fromHexString(args.getCursor()) : null);

        ResultSet resultSet = session.execute(statement);

        return CqlSessionSelectResults.of(
            convertRows(session, resultSet),
            CassdioColumnDefinition.makes(resultSet.getColumnDefinitions()),
            resultSet.getExecutionInfo().getPagingState()
        );
    }

    public void rowInserts(TableDTO.ClusterTableRowImportArgs args, List<Map<String, Object>> values) {
        if (CollectionUtils.isEmpty(values)) {
            return;
        }

        CqlSession session = cqlSessionFactory.get(args.getClusterId());

        for (List<Map<String, Object>> list : Lists.partition(values, args.getPerCommitSize())) {
            BatchStatement batchStatement = BatchStatement.newInstance(args.getBatchType());

            for (Map<String, Object> map : list) {
                batchStatement = batchStatement.add(
                    QueryBuilder.insertInto(args.getKeyspace(), args.getTable())
                        .json(Jsons.toJson(map))
                        .build()
                );
            }

            session.execute(batchStatement);
        }
    }
}
