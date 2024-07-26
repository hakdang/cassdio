import axiosInstance from "../utils/axiosUtils";

const clusterKeyspaceNameApi = async (clusterId, cacheEvict) => {
    try {
        const response = await axiosInstance({
            method: "GET",
            url: `/api/cassandra/cluster/${clusterId}/keyspace-name`,
            params: {
                cacheEvict: cacheEvict
            }
        })

        return await response;
    } catch (error) {
        //TODO : error
    }
}


export default {
    clusterKeyspaceNameApi,

}
