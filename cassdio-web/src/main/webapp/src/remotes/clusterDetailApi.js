import AxiosUtils from "utils/axiosUtils";

export default async function clusterDetailApi({clusterId}) {
    try {
        const response = await AxiosUtils.axiosInstance({
            method: "GET",
            url: `/api/cassandra/cluster/${clusterId}`,
            params: {}
        });

        return await response.data;
    } catch (error) {
        return await error.response.data;
    }
}
