import AxiosUtils from "utils/axiosUtils";

export default async function clusterTableListApi({clusterId, keyspaceName, cursor = null,}) {
    try {
        const response = await AxiosUtils.axiosInstance({
            method: "GET",
            url: `/api/cassandra/cluster/${clusterId}/keyspace/${keyspaceName}`,
            params: {}
        })

        return await response.data;
    } catch (error) {
        return await error.response.data;
    }
}
