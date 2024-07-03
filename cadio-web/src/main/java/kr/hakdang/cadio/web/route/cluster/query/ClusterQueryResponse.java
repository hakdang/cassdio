package kr.hakdang.cadio.web.route.cluster.query;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * ClusterQueryResponse
 * TODO : 변경필요
 *
 * @author akageun
 * @since 2024-07-03
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterQueryResponse {
    private boolean wasApplied;
    private List<String> columnNames;
    private List<Map<String, Object>> rows;
    private String nextToken;


}
