import AxiosUtils from "utils/axiosUtils";

export default async function bootstrapApi() {
    try {
        const response = await AxiosUtils.axiosInstance({
            method: "GET",
            url: `/api/bootstrap`,
        });

        return await response.data;
    } catch (error) {
        return await error.response.data;
    }
}
