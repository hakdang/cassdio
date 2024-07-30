import AxiosUtils from "utils/axiosUtils";

export default async function clusterDeleteApi({clusterId}) {
    try {
        const response = await AxiosUtils.axiosInstance({
            method: "DELETE",
            url: `/api/cassandra/cluster/${clusterId}`,
            params: {}
        });

        return await response.data;
    } catch (error) {
        return await error.response.data;
    }
}
