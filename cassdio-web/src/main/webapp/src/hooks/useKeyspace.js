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
        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${clusterId}/keyspace-name`,
            params: {
                cacheEvict: cacheEvict
            }
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
            setKeyspaceGeneralNames(userCreatedList);
            setKeyspaceSystemNames(systemCreatedList);
        }).catch((error) => {
            errorCatch(error)
        }).finally(() => {
            setKeyspaceNamesLoading(false);
        });
    }


    return {
        doGetKeyspaceNames,
        keyspaceNamesLoading,
        keyspaceGeneralNames,
        keyspaceSystemNames,
    }
}
