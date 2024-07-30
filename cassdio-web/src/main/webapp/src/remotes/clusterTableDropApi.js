import AxiosUtils from "utils/axiosUtils";

export default async function clusterTableDropApi(
    {clusterId, keyspaceName, tableName}
) {
    try {
        const response = await AxiosUtils.axiosInstance({
            method: "DELETE",
            url: `/api/cassandra/cluster/${clusterId}/keyspace/${keyspaceName}/table/${tableName}/drop`,
            params: {},
        })

        return await response.data;
    } catch (error) {
        return await error.response.data;
    }
}

