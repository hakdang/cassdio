import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";

import Spinner from "components/common/spinner";
import useCluster from "hooks/useCluster";

const ClusterList = () => {

    const {doGetClusterList, removeClusterId, clusters, clustersLoading} = useCluster();

    const [showClusterModal, setShowClusterModal] = useState(false);
    const [detailClusterId, setDetailClusterId] = useState(null);

    const closeClusterModal = () => {
        setShowClusterModal(false)
        setDetailClusterId(null);
    }

    useEffect(() => {
        //show component

        doGetClusterList();

        return () => {
            //hide component
        };
    }, []);

    return (
        <>
            <div
                className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h2 className="h2">Clusters</h2>
                <div className="btn-toolbar mb-2 mb-md-0">
                    <div className="btn-group me-2">
                        <Link to={`/admin/cluster`} className="btn btn-sm btn-outline-secondary">
                            New
                        </Link>
                    </div>
                </div>
            </div>

            <div className="row g-4">
                <Spinner loading={clustersLoading}>
                    {
                        (clusters && clusters.length > 0) ? clusters.map((info, infoIndex) => {
                                return (
                                    <div className={"col-md-4 col-sm-12"} key={`clusters${infoIndex}`}>
                                        <div className="card">
                                            <div className="card-body">
                                            <h4 className="card-title">
                                                <Link
                                                    className={"text-decoration-none link-body-emphasis"}
                                                    to={`/cluster/${info.clusterId}`}>
                                                    {info.clusterName}
                                                </Link>
                                            </h4>
                                            <h6 className="card-subtitle mb-2 text-body-secondary">Cluster Id
                                                : {info.clusterId}</h6>
                                            <p className="card-text text-truncate">{info.memo}</p>

                                            <Link to={`/cluster/${info.clusterId}`}
                                                  className="btn btn-primary btn-sm">
                                                Go To
                                            </Link>
                                        </div>
                                    </div>
                                </div>
                            )
                            }) :
                            <div className={"col"}>
                                <p>
                                    No Data

                                    <Link to={`/admin/cluster`} className={`ms-2 btn btn-sm btn-outline-primary`}>
                                        Go to registration
                                    </Link>
                                </p>
                            </div>
                    }
                </Spinner>
            </div>
        </>
    )
}

export default ClusterList;
