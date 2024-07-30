import AxiosUtils from "utils/axiosUtils";

export default async function clusterSaveApi({clusterId = null, clusterInfo}) {
    try {
        let method = "POST"
        let url = "/api/cassandra/cluster";
        if (clusterId) {
            method = "PUT";
            url = `/api/cassandra/cluster/${clusterId}`;
        }

        const response = await AxiosUtils.axiosInstance({
            method: method,
            url: url,
            data: {
                contactPoints: clusterInfo.contactPoints,
                port: clusterInfo.port,
                localDatacenter: clusterInfo.localDatacenter,
                username: clusterInfo.username,
                password: clusterInfo.password,
                memo: clusterInfo.memo,
            },
        });

        return await response.data;
    } catch (error) {
        return await error.response.data;
    }
}
