import AxiosUtils from "utils/axiosUtils";

export default async function clusterKeyspaceNameListApi({clusterId, cacheEvict}) {
    try {
        const response = await AxiosUtils.axiosInstance({
            method: "GET",
            url: `/api/cassandra/cluster/${clusterId}/keyspace-name`,
            params: {
                cacheEvict: cacheEvict
            }
        })

        return await response.data;
    } catch (error) {
        return await error.response.data;
    }
}
