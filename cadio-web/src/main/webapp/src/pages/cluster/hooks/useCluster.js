import axios from "axios";
import {useClusterDispatch, useClusterState} from "../context/clusterContext";

export default function useCluster() {

    const clusterDispatcher = useClusterDispatch();
    const {} = useClusterState();

    function doGetKeyspaceList() {

        clusterDispatcher({
            type: "SET_KEYSPACE_LIST_LOADING",
            loading: true,
        })

        axios({
            method: "GET",
            url: `/api/cassandra/cluster/1/keyspace`,
            params: {}
        }).then((response) => {
            console.log("res ", response);
            clusterDispatcher({
                type: "SET_KEYSPACE_LIST",
                keyspaceList: response.data.result.keyspaceList,
            })
        }).catch((error) => {
            //TODO : error catch
        }).finally(() => {
            console.log("finally")
            clusterDispatcher({
                type: "SET_KEYSPACE_LIST_LOADING",
                loading: false,
            })
        });
    }

    return {
        doGetKeyspaceList,

    }
}
