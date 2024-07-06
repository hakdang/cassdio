import axios from "axios";
import {useClusterDispatch, useClusterState} from "../context/clusterContext";
import {useParams} from "react-router-dom";
import {useAxios} from "../../../utils/axiosUtils";

export default function useCluster() {
    const routeParams = useParams();
    const {errorCatch} = useAxios();
    const clusterDispatcher = useClusterDispatch();
    const {} = useClusterState();

    function doGetKeyspaceNames() {
        //console.log("keyspace : ", routeParams.clusterId);
        clusterDispatcher({
            type: "SET_KEYSPACE_NAMES_LOADING",
            loading: true,
        })

        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${routeParams.clusterId}/keyspace-name`,
            params: {}
        }).then((response) => {
            console.log("doGetKeyspaceNames", response);
            clusterDispatcher({
                type: "SET_KEYSPACE_GENERAL_NAMES",
                keyspaceNames: response.data.result.keyspaceNameMap.GENERAL,
            })
            clusterDispatcher({
                type: "SET_KEYSPACE_SYSTEM_NAMES",
                keyspaceNames: response.data.result.keyspaceNameMap.SYSTEM,
            })
        }).catch((error) => {
            errorCatch(error)
        }).finally(() => {
            clusterDispatcher({
                type: "SET_KEYSPACE_NAMES_LOADING",
                loading: false,
            })
        });
    }

    function doGetKeyspaceList() {
        //console.log("keyspace : ", routeParams.clusterId);
        clusterDispatcher({
            type: "SET_KEYSPACE_LIST_LOADING",
            loading: true,
        })

        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${routeParams.clusterId}/keyspace`,
            params: {}
        }).then((response) => {
            console.log("doGetKeyspaceList", response);
            clusterDispatcher({
                type: "SET_KEYSPACE_LIST",
                keyspaceList: response.data.result.keyspaceList,
            })
        }).catch((error) => {
            errorCatch(error)
        }).finally(() => {
            clusterDispatcher({
                type: "SET_KEYSPACE_LIST_LOADING",
                loading: false,
            })
        });
    }

    return {
        doGetKeyspaceList,
        doGetKeyspaceNames,
    }
}
