import axiosInstance from "utils/axiosUtils";

export default async function clusterTableDetailApi(
    {clusterId, keyspaceName, tableName}
) {
    const response = await axiosInstance({
        method: "GET",
        url: `/api/cassandra/cluster/${clusterId}/keyspace/${keyspaceName}/table/${tableName}`,
        params: {
            withTableDescribe: true,
        }
    })

    return response;
}

