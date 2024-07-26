import axios from "axios";

export default async function clusterKeyspaceNameApi(clusterId, cacheEvict) {
    try {
        const response = await axios({
            method: "GET",
            url: `/api/cassandra/cluster/${clusterId}/keyspace-name`,
            params: {
                cacheEvict: cacheEvict
            }
        })

        console.log("response : ", response);

        return await response.data.result;
    } catch (error) {
        //TODO : error
    }
}
