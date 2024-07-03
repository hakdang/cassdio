import axios from "axios";
import {useClusterDispatch, useClusterState} from "../context/clusterContext";
import {useParams} from "react-router-dom";
import {axiosCatch} from "../../../utils/axiosUtils";

export default function useCluster() {
    const routeParams = useParams();

    const clusterDispatcher = useClusterDispatch();
    const {} = useClusterState();

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
            console.log(response);
            clusterDispatcher({
                type: "SET_KEYSPACE_LIST",
                keyspaceList: response.data.result.keyspaceList,
            })
        }).catch((error) => {
            axiosCatch(error)
        }).finally(() => {
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
