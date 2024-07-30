import {toast} from "react-toastify";
import {useState} from "react";

import clusterListApi from "remotes/clusterListApi";
import clusterDeleteApi from "remotes/clusterDeleteApi";
import clusterDetailApi from "remotes/clusterDetailApi";
import clusterSaveApi from "remotes/clusterSaveApi";

export default function useCluster() {
    const [clustersLoading, setClustersLoading] = useState(false);
    const [clusters, setClusters] = useState([]);
    const [clusterDetailLoading, setClusterDetailLoading] = useState(false);

    const doGetClusterList = () => {
        setClustersLoading(true)

        clusterListApi(
        ).then((data) => {
            if (!data.ok) {
                return;
            }

            setClusters(data.result.clusters)
        }).finally(() => {
            setClustersLoading(false)
        });
    }

    const removeClusterId = (clusterId) => {
        if (!window.confirm("Do you want to remove this?")) {
            return;
        }

        clusterDeleteApi({
            clusterId: clusterId
        }).then((data) => {
            if (!data.ok) {
                return;
            }

            toast.info("Complete");
            doGetClusterList();
        }).finally(() => {

        });
    }

    const [clusterInfo, setClusterInfo] = useState(
        {
            clusterId: null,
            contactPoints: "",
            port: 9042,
            localDatacenter: "",
            clusterAuthCredentials: false,
            username: "",
            password: "",
            memo: "",
        }
    );

    const [saveLoading, setSaveLoading] = useState(false);
    const doSaveCluster = (clusterId, handleClose) => {
        if (!clusterInfo.contactPoints) {
            toast.warn("Please enter ContactPoints");
            return;
        }

        if (!clusterInfo.port || clusterInfo.port === 0) {
            toast.warn("Please enter Port");
            return;
        }

        if (!clusterInfo.localDatacenter) {
            toast.warn("Please enter local Datacenter");
            return;
        }

        if (clusterInfo.clusterAuthCredentials) {
            if (!clusterInfo.username) {
                toast.warn("Please enter username.");
                return;
            }

            if (!clusterInfo.password) {
                toast.warn("Please enter your password.");
                return;
            }
        }

        setSaveLoading(true);

        clusterSaveApi({
            clusterId,
            clusterInfo
        }).then((data) => {
            if (!data.ok) {
                return;
            }

            doGetClusterList();
            toast.info("Complete");
            handleClose();
        }).finally(() => {
            setSaveLoading(false);
        })
    }

    const doGetCluster = (clusterId) => {
        setClusterDetailLoading(true)

        clusterDetailApi({
            clusterId: clusterId
        }).then((data) => {
            if (!data.ok) {
                return;
            }

            const result = data.result.cluster;
            const clusterAuthCredentials = !!(result.username && result.password);
            setClusterInfo({
                ...result,
                clusterAuthCredentials: clusterAuthCredentials
            })
        }).finally(() => {
            setClusterDetailLoading(false)
        });
    }

    return {
        doGetClusterList,
        removeClusterId,
        doSaveCluster,
        doGetCluster,
        clusters,
        clusterDetailLoading,
        clusterInfo,
        setClusterInfo,
        saveLoading,
        clustersLoading,
    }
}
