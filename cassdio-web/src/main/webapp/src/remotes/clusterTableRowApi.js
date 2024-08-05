import AxiosUtils from "utils/axiosUtils";

export default async function clusterTableRowApi(
    {clusterId, keyspaceName, tableName, cursor}
) {
    try {
        const response = await AxiosUtils.axiosInstance({
            method: "GET",
            url: `/api/cassandra/cluster/${clusterId}/keyspace/${keyspaceName}/table/${tableName}/row`,
            params: {
                pageSize: 50,
                timeoutSeconds: 3,
                cursor: cursor,
            },
        })

        return await response.data;
    } catch (error) {
        return await error.response.data;
    }
}

