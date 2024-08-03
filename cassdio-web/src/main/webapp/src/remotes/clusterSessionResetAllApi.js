import AxiosUtils from "utils/axiosUtils";

export default async function clusterSessionResetAllApi() {
    try {
        const response = await AxiosUtils.axiosInstance(
            {
                method: "POST",
                url: `/api/cassandra/cluster/session/clear`,
            }
        );

        return await response.data;
    } catch (error) {
        return await error.response.data;
    }
}
