import {useState} from "react";
import clusterKeyspaceNameListApi from "remotes/clusterKeyspaceNameListApi";

export default function useKeyspace() {
    const [keyspaceNamesLoading, setKeyspaceNamesLoading] = useState(false);
    const [keyspaceGeneralNames, setKeyspaceGeneralNames] = useState([]);
    const [keyspaceSystemNames, setKeyspaceSystemNames] = useState([]);

    const doGetKeyspaceNames = (clusterId, cacheEvict) => {
        setKeyspaceNamesLoading(true);
        clusterKeyspaceNameListApi({
            clusterId,
            cacheEvict,
        }).then((data) => {
            if (!data.ok) {
                return;
            }

            const userCreatedList = [];
            const systemCreatedList = [];

            const tempKeyspaceList = data.result.keyspaceNameList;

            for (const ele of tempKeyspaceList) {
                if (ele.systemKeyspace) {
                    systemCreatedList.push(ele)
                } else {
                    userCreatedList.push(ele)
                }
            }
            setKeyspaceGeneralNames(userCreatedList);
            setKeyspaceSystemNames(systemCreatedList);
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
