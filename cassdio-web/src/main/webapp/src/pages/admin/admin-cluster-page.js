import React, {useEffect, useState} from "react";
import useCluster from "hooks/useCluster";
import Spinner from "components/common/spinner";
import {Link} from "react-router-dom";
import ClusterManageModal from "components/cluster/modal/cluster-manage-modal";
import {OverlayTrigger, Tooltip} from "react-bootstrap";

const AdminClusterPage = () => {

    const {
        doGetClusterList,
        removeClusterId,
        doSessionClearAll,
        doSessionClearOne,
        clusters,
        clustersLoading,
    } = useCluster();

    const [showClusterModal, setShowClusterModal] = useState(false);
    const [detailClusterId, setDetailClusterId] = useState(null);

    const closeClusterModal = () => {
        setShowClusterModal(false)
        setDetailClusterId(null);
    }

    const sessionAllReset = () => {
        if (!window.confirm("All cluster session reset?")) {
            return;
        }

        doSessionClearAll();
    }
    const sessionOneReset = (clusterId) => {
        if (!window.confirm(`${clusterId} cluster session reset?`)) {
            return;
        }

        doSessionClearOne({clusterId})
    }

    useEffect(() => {
        //show component

        doGetClusterList();

        return () => {
            //hide component
        };
    }, [doGetClusterList]);

    return (
        <>
            <div
                className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h2 className="h2">Cluster Admin</h2>
                <div className="btn-toolbar mb-2 mb-md-0">
                    <div className="btn-group me-2">
                        <button type="button" className="btn btn-sm btn-outline-secondary"
                                onClick={e => setShowClusterModal(true)}>
                            <i className="bi bi-database-fill"></i> New Cluster
                        </button>
                    </div>
                </div>
            </div>

            <div className={"row mb-3"}>
                <div className={"col"}>
                    <button type="button" className="btn btn-sm btn-outline-danger"
                            onClick={e => sessionAllReset()}>
                        Session Reset
                    </button>
                </div>
            </div>

            <Spinner loading={clustersLoading}>
                <div className="table-responsive small">
                    <table className="table table-sm table-hover">
                        <thead>
                        <tr className={"table-dark"}>
                            <th className={"text-center"} scope="col">#</th>
                            <th className={"text-center"} scope="col">Cluster Id</th>
                            <th className={"text-center"} scope="col">Cluster Name</th>
                            <th className={"text-center"} scope="col">ContactPoints</th>
                            <th className={"text-center"} scope="col">Port</th>
                            <th className={"text-center"} scope="col">Local Datacenter</th>
                        </tr>
                        </thead>
                        <tbody className="table-group-divider">
                        {
                            (clusters && clusters.length > 0) ? clusters.map((info, infoIndex) => {
                                return (
                                    <tr key={infoIndex}>
                                        <td className={"text-center"}>
                                            <div className={"btn-group btn-group-sm"}>
                                                <button className={"btn btn-sm btn-outline-info"}
                                                   onClick={e => {
                                                       setDetailClusterId(info.clusterId);
                                                       setShowClusterModal(true);
                                                   }}>
                                                    <i className="bi bi-pencil-fill"></i>
                                                </button>
                                                <button className={"btn btn-sm btn-outline-info"}
                                                   onClick={e => {
                                                       sessionOneReset(info.clusterId)
                                                   }}>
                                                    <OverlayTrigger placement="top" overlay={
                                                        <Tooltip id="tooltip">
                                                            Session Reset
                                                        </Tooltip>
                                                    }>
                                                        <i className="bi bi-x-octagon-fill"></i>
                                                    </OverlayTrigger>
                                                </button>
                                                <button className={"btn btn-sm btn-outline-info"}
                                                   onClick={e => {
                                                       removeClusterId(info.clusterId);
                                                   }}>
                                                    <i className="bi bi-trash"></i>
                                                </button>
                                            </div>
                                        </td>
                                        <td className={"text-center"}>
                                            <Link
                                                className={"text-decoration-none"}
                                                to={`/cluster/${info.clusterId}`}>
                                                {info.clusterId}
                                            </Link>
                                        </td>
                                        <td className={"text-center"}>
                                            <Link
                                                className={"text-decoration-none"}
                                                to={`/cluster/${info.clusterId}`}>
                                                {info.clusterName}
                                            </Link>
                                        </td>
                                        <td className={"text-center text-truncate"}>{info.contactPoints}</td>
                                        <td className={"text-center"}>{info.port}</td>
                                        <td className={"text-center"}>{info.localDatacenter}</td>
                                    </tr>
                                )
                            }) : <tr>
                                <td className={"text-center"} colSpan={6}>No Data</td>
                            </tr>
                        }
                        </tbody>
                    </table>
                </div>
            </Spinner>

            {
                showClusterModal && <ClusterManageModal
                    show={showClusterModal}
                    clusterId={detailClusterId}
                    handleClose={() => closeClusterModal()}/>
            }
        </>
    )
}

export default AdminClusterPage;
