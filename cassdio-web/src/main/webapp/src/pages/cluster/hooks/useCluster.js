import axios from "axios";

import useCassdio from "commons/hooks/useCassdio";
import {toast} from "react-toastify";
import {useState} from "react";

export default function useCluster() {
    const {errorCatch} = useCassdio();

    const [clustersLoading, setClustersLoading] = useState(false);
    const [clusters, setClusters] = useState([]);
    const [clusterDetailLoading, setClusterDetailLoading] = useState(false);

    const doGetClusterList = () => {
        setClustersLoading(true)

        axios({
            method: "GET",
            url: `/api/cassandra/cluster`,
            params: {}
        }).then((response) => {
            setClusters(response.data.result.clusters)
        }).catch((error) => {
            errorCatch(error)
        }).finally(() => {
            setClustersLoading(false)
        });
    }

    const removeClusterId = (clusterId) => {
        if (!window.confirm("Do you want to remove this?")) {
            return;
        }

        axios({
            method: "DELETE",
            url: `/api/cassandra/cluster/${clusterId}`,
            params: {}
        }).then((response) => {
            toast.info("Complete");
            doGetClusterList();
        }).catch((error) => {
            errorCatch(error)
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

        let method = "POST"
        let url = "/api/cassandra/cluster";
        if (clusterId) {
            method = "PUT";
            url = `/api/cassandra/cluster/${clusterId}`;
        }

        axios({
            method: method,
            url: url,
            data: {
                contactPoints: clusterInfo.contactPoints,
                port: clusterInfo.port,
                localDatacenter: clusterInfo.localDatacenter,
                username: clusterInfo.username,
                password: clusterInfo.password,
            },
        }).then((response) => {
            toast.info("Complete");
            doGetClusterList();
            handleClose();
        }).catch((error) => {
            errorCatch(error);
        }).finally(() => {
            setSaveLoading(false);
        })
    }

    const doGetCluster = (clusterId) => {
        setClusterDetailLoading(true)

        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${clusterId}`,
            params: {}
        }).then((response) => {
            setClusterInfo(response.data.result.cluster)
        }).catch((error) => {
            errorCatch(error)
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
