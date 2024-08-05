import AxiosUtils from "utils/axiosUtils";

export default async function clusterTableDetailApi(
    {clusterId, keyspaceName, tableName}
) {
    try {
        const response = await AxiosUtils.axiosInstance({
            method: "GET",
            url: `/api/cassandra/cluster/${clusterId}/keyspace/${keyspaceName}/table/${tableName}`,
            params: {
                withTableDescribe: true,
            }
        })

        return await response.data;
    } catch (error) {
        return await error.response.data;
    }
}

