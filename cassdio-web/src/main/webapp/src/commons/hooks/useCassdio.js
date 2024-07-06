import axios from "axios";
import {useCassdioDispatch} from "../context/cassdioContext";


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
                //systemAvailable: false,
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

    function openToast(message) {
        if (!message) {
            return;
        }
        cassdioDispatcher({
            type: "SET_TOAST",
            message: message,
            delay: 3000,
        })
    }

    function errorCatch(error) {
        if (axios.isAxiosError(error)) {
            const {message} = error;
            const {method, url} = error.config;
            const {status, statusText} = error.response;

            console.error(
                `[API] ${method?.toUpperCase()} ${url} | Error ${status} ${statusText} | ${message}`
            );

            switch (status) {
                case 400:
                    alert(`${status} 잘못된 요청입니다.`);
                    break;
                case 401: {
                    alert(`${status} 인증 실패입니다.`);
                    break;
                }
                case 403: {
                    alert(`${status} 권한이 없습니다.`);
                    break;
                }
                case 404: {
                    alert(`${status} 찾을 수 없는 페이지입니다.`);
                    break;
                }
                case 500: {
                    openToast(`${status} 서버 오류입니다.`);
                    break;
                }
                default: {
                    alert(`${status} 에러가 발생했습니다. ${error.message}`);
                }
            }
        } else if (error instanceof Error && error.name === "TimeoutError") {
            console.error(`[API] | TimeError ${error.toString()}`);
            alert(`${0} 요청 시간이 초과되었습니다.`);
        } else {
            console.error(`[API] | Error ${error.toString()}`);
            alert(`${0} 에러가 발생했습니다. ${error.toString()}`);
        }
    }

    return {
        doBootstrap,
        openToast,
        errorCatch,
    }
}