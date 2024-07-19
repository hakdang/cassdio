import axios from "axios";
import {useParams} from "react-router-dom";

import {useClusterDispatch} from "../context/clusterContext";

import useCassdio from "commons/hooks/useCassdio";

export default function useCluster() {
    const routeParams = useParams();
    const {errorCatch} = useCassdio();
    const clusterDispatcher = useClusterDispatch();

    function doGetKeyspaceNames() {
        clusterDispatcher({
            type: "SET_KEYSPACE_NAMES_LOADING",
            loading: true,
        })

        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${routeParams.clusterId}/keyspace-name`,
            params: {}
        }).then((response) => {
            const userCreatedList = [];
            const systemCreatedList = [];

            const tempKeyspaceList = response.data.result.keyspaceNameList;

            for (const ele of tempKeyspaceList) {
                if (ele.systemKeyspace) {
                    systemCreatedList.push(ele)
                } else {
                    userCreatedList.push(ele)
                }
            }

            clusterDispatcher({
                type: "SET_KEYSPACE_GENERAL_NAMES",
                keyspaceNames: userCreatedList,
            })
            clusterDispatcher({
                type: "SET_KEYSPACE_SYSTEM_NAMES",
                keyspaceNames: systemCreatedList,
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


    return {
        doGetKeyspaceNames,
    }
}
