import AxiosUtils from "utils/axiosUtils";

export default async function clusterTableTruncateApi(
    {clusterId, keyspaceName, tableName}
) {
    try {
        const response = await AxiosUtils.axiosInstance({
            method: "DELETE",
            url: `/api/cassandra/cluster/${clusterId}/keyspace/${keyspaceName}/table/${tableName}/truncate`,
            params: {},
        })

        return await response.data;
    } catch (error) {
        return await error.response.data;
    }
}

