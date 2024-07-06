import axios from "axios";
import {useCadioDispatch, useCadioState} from "../context/cadioContext";
import {axiosCatch} from "../../utils/axiosUtils";


export default function useCadio() {
    const cadioDispatcher = useCadioDispatch();
    const {
        bootstrapLoading,
        systemAvailable
    } = useCadioState();

    function doBootstrap() {
        cadioDispatcher({
            type: "SET_BOOTSTRAP_LOADING",
            bootstrapLoading: true,
        })
        axios({
            method: "GET",
            url: `/api/bootstrap`,
            params: {}
        }).then((response) => {
            cadioDispatcher({
                type: "SET_SYSTEM_AVAILABLE",
                systemAvailable: response.data.result.systemAvailable,
                //systemAvailable: false,
            })

        }).catch((error) => {
            //TODO : error catch
            axiosCatch(error);
        }).finally(() => {
            cadioDispatcher({
                type: "SET_BOOTSTRAP_LOADING",
                bootstrapLoading: false,
            })
        });
    }

    function openToast(message) {
        console.log("message : ", message)
        if (!message) {
            return;
        }
        cadioDispatcher({
            type: "SET_TOAST",
            message: message,
            delay: 3000,
        })
    }

    return {
        doBootstrap,
        openToast
    }
}
