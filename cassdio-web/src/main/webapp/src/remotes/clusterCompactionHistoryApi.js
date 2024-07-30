import AxiosUtils from "utils/axiosUtils";

export default async function clusterCompactionHistoryApi({clusterId, keyspaceName,}) {
    try {
        const response = await AxiosUtils.axiosInstance({
            method: "GET",
            url: `/api/cassandra/cluster/${clusterId}/compaction-history`,
            params: {
                keyspace: keyspaceName,
            }
        })

        return await response.data;
    } catch (error) {
        return await error.response.data;
    }
}
