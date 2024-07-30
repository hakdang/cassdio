import AxiosUtils from "utils/axiosUtils";

export default async function clusterNodesApi({clusterId}) {
    try {
        const response = await AxiosUtils.axiosInstance(
            {
                method: "GET",
                url: `/api/cassandra/cluster/${clusterId}/node`,
            }
        );

        return await response.data;
    } catch (error) {
        return await error.response.data;
    }
}
