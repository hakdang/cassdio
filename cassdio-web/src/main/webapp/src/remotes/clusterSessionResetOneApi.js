import AxiosUtils from "utils/axiosUtils";

export default async function clusterSessionResetOneApi({clusterId}) {
    try {
        const response = await AxiosUtils.axiosInstance(
            {
                method: "POST",
                url: `/api/cassandra/cluster/${clusterId}/session/clear`,
            }
        );

        return await response.data;
    } catch (error) {
        return await error.response.data;
    }
}
