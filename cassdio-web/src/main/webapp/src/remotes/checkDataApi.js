import axios from "axios";

export default async function checkDataApi() {
    try {
        const response = await axios({
            method: "GET",
            url: `/api/check-data`,
        });

        console.log("response : ", response);

        return await response.data.result;
    } catch (error) {
        //TODO : error
    }
}
