import AxiosUtils from "utils/axiosUtils";

export default async function clusterKeyspaceDetailApi({clusterId, keyspaceName, withTableList = true}) {
    try {
        const response = await AxiosUtils.axiosInstance({
            method: "GET",
            url: `/api/cassandra/cluster/${clusterId}/keyspace/${keyspaceName}`,
            params: {
                withTableList: withTableList,
            }
        })

        return await response.data;
    } catch (error) {
        return await error.response.data;
    }
}
