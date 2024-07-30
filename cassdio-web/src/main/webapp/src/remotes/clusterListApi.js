import AxiosUtils from "utils/axiosUtils";

export default async function clusterListApi() {
    try {
        const response = await AxiosUtils.axiosInstance({
            method: "GET",
            url: `/api/cassandra/cluster`,
            params: {}
        });

        return await response.data;
    } catch (error) {
        return await error.response.data;
    }
}
