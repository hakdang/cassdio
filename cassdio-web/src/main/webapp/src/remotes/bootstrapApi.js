import axiosInstance from "utils/axiosUtils";

export default async function bootstrapApi(
    {}
) {
    try {
        const response = await axiosInstance({
            method: "GET",
            url: `/api/bootstrap`,
        });

        console.log("bootstrap response : ", response);

        return await response.data.result;
    } catch (error) {
        //TODO : error
    }
}
