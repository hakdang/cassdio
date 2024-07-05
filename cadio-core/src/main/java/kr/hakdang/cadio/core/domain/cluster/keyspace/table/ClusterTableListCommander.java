package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ColumnDefinition;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.TableMetadata;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.protocol.internal.util.Bytes;
import io.micrometer.common.util.StringUtils;
import kr.hakdang.cadio.common.Jsons;
import kr.hakdang.cadio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.ClusterTableArgs.ClusterTableListArgs;
import kr.hakdang.cadio.core.domain.cluster.keyspace.CassandraSystemKeyspace;
import kr.hakdang.cadio.core.domain.cluster.keyspace.table.column.CassandraSystemTablesColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;
import static java.util.Collections.emptyList;

/**
 * ClusterTableListCommander
 *
 * @author seungh0
 * @since 2024-07-01
 */
@Slf4j
@Service
public class ClusterTableListCommander extends BaseClusterCommander {

    public ClusterTableListResult listTables(CqlSession session, ClusterTableListArgs args) {
        SimpleStatement statement = QueryBuilder
            .selectFrom(CassandraSystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_TABLES.getTableName())
            .all()
            .whereColumn(CassandraSystemTablesColumn.TABLES_KEYSPACE_NAME.getColumnName()).isEqualTo(bindMarker())
            .limit(args.getLimit())
            .build(args.getKeyspace())
            .setPagingState(StringUtils.isBlank(args.getNextPageState()) ? null : Bytes.fromHexString(args.getNextPageState()));

//        for (Map.Entry<CqlIdentifier, TableMetadata> entry : session.getMetadata().getKeyspace(args.getKeyspace())
//            .orElseThrow().getTables().entrySet()) {
//            log.info("key : {}, value : {}", entry.getKey(), entry.getValue());
//        }

//        ResultSet rs2 = session.execute("DESC " + args.getKeyspace());
//        for (Row row : rs2.all()) {
//            log.info("222row : {}", row.getFormattedContents());
//        }

        ResultSet rs = session.execute(statement);

        List<ClusterTable> tables = rs.all().stream()
            .map(ClusterTable::from)
            .collect(Collectors.toList());

        return ClusterTableListResult.of(tables, rs.getExecutionInfo().getPagingState());
    }

}
