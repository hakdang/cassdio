import axios from "axios";
import {useCadioDispatch, useCadioState} from "../context/cadioContext";


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
            params: {sleep: 500}
        }).then((response) => {
            console.log("res ", response);
            cadioDispatcher({
                type: "SET_SYSTEM_AVAILABLE",
                systemAvailable: response.data.result.systemAvailable,
                //systemAvailable: false,
            })


        }).catch((error) => {
            //TODO : error catch
        }).finally(() => {
            console.log("finally")
            cadioDispatcher({
                type: "SET_BOOTSTRAP_LOADING",
                bootstrapLoading: false,
            })
        });
    }

    return {
        doBootstrap,
    }
}
