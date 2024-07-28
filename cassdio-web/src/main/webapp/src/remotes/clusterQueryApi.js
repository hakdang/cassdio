import axiosInstance from "../utils/axiosUtils";

export default async function clusterQueryApi(
    {clusterId, keyspaceName = null, query, cursor, queryOptions,}
) {
    try {
        let url = `/api/cassandra/cluster/${clusterId}/query`;
        if (keyspaceName) {
            url = `/api/cassandra/cluster/${clusterId}/keyspace/${keyspaceName}/query`;
        }

        const response = await axiosInstance({
            method: "POST",
            url: url,
            data: {
                query: query,
                pageSize: queryOptions.limit,
                trace: queryOptions.trace,
                timeoutSeconds: queryOptions.timeoutSeconds,
                consistencyLevelProtocolCode: queryOptions.consistencyLevelProtocolCode,
                cursor: cursor,
            },
        })

        return await response;
    } catch (error) {
        //TODO : error
    }
}
