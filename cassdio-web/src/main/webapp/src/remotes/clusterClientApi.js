import AxiosUtils from "utils/axiosUtils";

export default async function clusterClientApi({clusterId}) {
    try {
        const response = await AxiosUtils.axiosInstance(
            {
                method: "GET",
                url: `/api/cassandra/cluster/${clusterId}/client`,
            }
        );

        return await response.data;
    } catch (error) {
        return await error.response.data;
    }
}
