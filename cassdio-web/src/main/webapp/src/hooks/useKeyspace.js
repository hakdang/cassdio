import axios from "axios";

import useCassdio from "hooks/useCassdio";
import {useState} from "react";

export default function useKeyspace() {
    const {errorCatch} = useCassdio();

    const [keyspaceNamesLoading, setKeyspaceNamesLoading] = useState(false);
    const [keyspaceGeneralNames, setKeyspaceGeneralNames] = useState([]);
    const [keyspaceSystemNames, setKeyspaceSystemNames] = useState([]);

    const doGetKeyspaceNames = (clusterId, cacheEvict) => {
        setKeyspaceNamesLoading(true);
        console.log("load : ", new Date());
        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${clusterId}/keyspace-name`,
            params: {
                cacheEvict: cacheEvict
            }
        }).then((response) => {
            console.log("start : ", new Date());
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
            console.log("set : ", new Date());
            setKeyspaceGeneralNames(userCreatedList);
            setKeyspaceSystemNames(systemCreatedList);
            console.log("set end : ", new Date());
        }).catch((error) => {
            errorCatch(error)
        }).finally(() => {
            setKeyspaceNamesLoading(false);
            console.log("end : ", new Date());
        });
    }


    return {
        doGetKeyspaceNames,
        keyspaceNamesLoading,
        keyspaceGeneralNames,
        keyspaceSystemNames,
    }
}
