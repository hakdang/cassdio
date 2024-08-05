import axios from "axios";
import {toast} from "react-toastify";

const axiosInstance = axios.create({
    timeout: 5000,
});

axiosInstance.interceptors.request.use(
    (config) => {
        //요청 보내기 전에 수행 로직
        return config;
    },
    (err) => {
        //요청 에러 시 수행 로직
        return Promise.reject(err);
    }
);

//응답 인터셉터
axiosInstance.interceptors.response.use(
    (response) => {
        //응답에 대한 로직
        return response;
    },
    (err) => {
        errorCatch(err)
        return Promise.reject(err);
    }
);

const errorCatch = (error) => {
    if (axios.isAxiosError(error)) {
        const {message} = error;
        const {method, url} = error.config;
        const {status, statusText, data} = error.response;

        console.error(
            `[API] ${method?.toUpperCase()} ${url} | Error ${status} ${statusText} | ${message}`, error
        );

        switch (status) {
            case 400:
                toast.warn(`Invalid request : ${status}`);
                break;
            case 401: {
                toast.warn(`Unauthorized error : ${status}`);
                break;
            }
            case 403: {
                toast.warn(`Forbidden error : ${status}`);
                break;
            }
            case 404: {
                toast.warn(`Not Found : ${status}`);
                break;
            }
            case 500: {
                toast.error(`Internal Server Error : ${status}\n ${data.message}`);
                break;
            }
            default: {
                toast.error(`Error : ${status}, ${data.message}`);
            }
        }
    } else if (error instanceof Error && error.name === "TimeoutError") {
        console.error(`[API] | TimeError ${error.toString()}`);
        toast.error(`Timeout ${0}`);
    } else {
        console.error(`[API] | Error ${error.toString()}`);
        toast.error(`Error ${0}, ${error.toString()}`);
    }
}

export default {
    axiosInstance,
};
