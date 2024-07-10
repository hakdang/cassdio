import axios from "axios";
import {useClusterDispatch, useClusterState} from "../context/clusterContext";
import {useParams} from "react-router-dom";
import useCassdio from "../../../commons/hooks/useCassdio";

export default function useCluster() {
    const routeParams = useParams();
    const {errorCatch} = useCassdio();
    const clusterDispatcher = useClusterDispatch();
    const {} = useClusterState();

    // function doGetKeyspaceNames() {
    //     //console.log("keyspace : ", routeParams.clusterId);
    //     clusterDispatcher({
    //         type: "SET_KEYSPACE_NAMES_LOADING",
    //         loading: true,
    //     })
    //
    //     axios({
    //         method: "GET",
    //         url: `/api/cassandra/cluster/${routeParams.clusterId}/keyspace-name`,
    //         params: {}
    //     }).then((response) => {
    //         console.log("doGetKeyspaceNames", response);
    //         clusterDispatcher({
    //             type: "SET_KEYSPACE_GENERAL_NAMES",
    //             keyspaceNames: response.data.result.keyspaceNameMap.GENERAL,
    //         })
    //         clusterDispatcher({
    //             type: "SET_KEYSPACE_SYSTEM_NAMES",
    //             keyspaceNames: response.data.result.keyspaceNameMap.SYSTEM,
    //         })
    //     }).catch((error) => {
    //         errorCatch(error)
    //     }).finally(() => {
    //         clusterDispatcher({
    //             type: "SET_KEYSPACE_NAMES_LOADING",
    //             loading: false,
    //         })
    //     });
    // }

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

            const userCreatedList = [];
            const systemCreatedList = [];

            const tempKeyspaceList = response.data.result.keyspaceList;

            for (const ele of tempKeyspaceList) {
                if (ele.systemKeyspace) {
                    systemCreatedList.push(ele)
                } else {
                    userCreatedList.push(ele)
                }
            }

            clusterDispatcher({
                type: "SET_KEYSPACE_LIST",
                keyspaceList: userCreatedList,
            })

            clusterDispatcher({
                type: "SET_SYSTEM_KEYSPACE_LIST",
                keyspaceList: systemCreatedList,
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
        //doGetKeyspaceNames,
    }
}
