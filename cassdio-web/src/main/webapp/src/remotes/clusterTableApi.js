import axiosInstance from "../utils/axiosUtils";

const clusterTableDetailApi = async (clusterId, keyspaceName, tableName) => {
    const response = await axiosInstance({
        method: "GET",
        url: `/api/cassandra/cluster/${clusterId}/keyspace/${keyspaceName}/table2/${tableName}`,
        params: {
            withTableDescribe: true,
        }
    })

    return await response;
}


export default {
    clusterTableDetailApi,

}
