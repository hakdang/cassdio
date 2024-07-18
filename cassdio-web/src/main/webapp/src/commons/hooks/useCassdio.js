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
            const {status, statusText} = error.response;

            console.error(
                `[API] ${method?.toUpperCase()} ${url} | Error ${status} ${statusText} | ${message}`, error
            );

            switch (status) {
                case 400:
                    toast.warn(`${status} 잘못된 요청입니다.`);
                    break;
                case 401: {
                    toast.warn(`${status} 인증 실패입니다.`);
                    break;
                }
                case 403: {
                    toast.warn(`${status} 권한이 없습니다.`);
                    break;
                }
                case 404: {
                    toast.warn(`${status} 찾을 수 없는 페이지입니다.`);
                    break;
                }
                case 500: {
                    toast.error(`${status} 서버 오류입니다. ${error.message}`);
                    break;
                }
                default: {
                    toast.error(`${status} 에러가 발생했습니다. ${error.message}`);
                }
            }
        } else if (error instanceof Error && error.name === "TimeoutError") {
            console.error(`[API] | TimeError ${error.toString()}`);
            toast.error(`${0} 요청 시간이 초과되었습니다.`);
        } else {
            console.error(`[API] | Error ${error.toString()}`);
            toast.error(`${0} 에러가 발생했습니다. ${error.toString()}`);
        }
    }

    return {
        doBootstrap,
        errorCatch,
    }
}
