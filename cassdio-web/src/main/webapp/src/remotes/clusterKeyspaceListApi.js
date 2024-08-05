import AxiosUtils from "utils/axiosUtils";

export default async function clusterKeyspaceListApi({clusterId, }) {
    try {
        const response = await AxiosUtils.axiosInstance({
            method: "GET",
            url: `/api/cassandra/cluster/${clusterId}/keyspace`,
            params: {}
        })

        return await response.data;
    } catch (error) {
        return await error.response.data;
    }
}
