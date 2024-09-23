import AxiosUtils from "utils/axiosUtils";

export default async function clusterTableRowImportApi(
    {clusterId, keyspaceName, tableName, formData}
) {
    try {
        const response = await AxiosUtils.axiosInstance({
            method: 'post',
            url: `/api/cassandra/cluster/${clusterId}/keyspace/${keyspaceName}/table/${tableName}/row/import`,
            data: formData,
        })

        return await response.data;
    } catch (error) {
        return await error.response.data;
    }
}

