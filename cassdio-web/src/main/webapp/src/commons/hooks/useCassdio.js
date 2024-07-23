import axios from "axios";
import {useCassdioDispatch} from "../context/cassdioContext";
import {toast} from "react-toastify";


export default function useCassdio() {
    const cassdioDispatcher = useCassdioDispatch();

    function doBootstrap() {
        cassdioDispatcher({
            type: "SET_BOOTSTRAP_LOADING",
            bootstrapLoading: true,
        })
        axios({
            method: "GET",
            url: `/api/bootstrap`,
            params: {}
        }).then((response) => {
            cassdioDispatcher({
                type: "SET_SYSTEM_AVAILABLE",
                systemAvailable: response.data.result.systemAvailable,
            })

        }).catch((error) => {
            errorCatch(error);
        }).finally(() => {
            cassdioDispatcher({
                type: "SET_BOOTSTRAP_LOADING",
                bootstrapLoading: false,
            })
        });
    }

    function errorCatch(error) {
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

    return {
        doBootstrap,
        errorCatch,
    }
}
