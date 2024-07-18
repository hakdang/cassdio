import Spinner from "../../components/spinner";
import {Link} from "react-router-dom";
import useCassdio from "../../commons/hooks/useCassdio";
import {useEffect, useState} from "react";
import axios from "axios";
import ClusterManageModal from "./cluster-manage-modal";
import {toast} from "react-toastify";

const ClusterList = () => {

    const {errorCatch} = useCassdio();

    const [clustersLoading, setClustersLoading] = useState(false);
    const [clusters, setClusters] = useState([]);

    const [showClusterModal, setShowClusterModal] = useState(false);
    const [detailClusterId, setDetailClusterId] = useState(null);

    const closeClusterModal = () => {
        setShowClusterModal(false)
        setDetailClusterId(null);
    }

    const getClusterList = () => {
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
        if (!window.confirm("제거하시겠습니까?")) {
            return;
        }

        axios({
            method: "DELETE",
            url: `/api/cassandra/cluster/${clusterId}`,
            params: {}
        }).then((response) => {
            toast.info("제거 완료");
            getClusterList();
        }).catch((error) => {
            errorCatch(error)
        }).finally(() => {
        });
    }

    useEffect(() => {
        //show component

        getClusterList();

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
                        <button type="button" className="btn btn-sm btn-outline-secondary"
                                onClick={e => setShowClusterModal(true)}>
                            New Cluster
                        </button>
                    </div>
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
                                            <a className={"btn btn-sm btn-outline-warning"}
                                               onClick={e => {
                                                   setDetailClusterId(info.clusterId);
                                                   setShowClusterModal(true);
                                               }}>
                                                <i className="bi bi-pencil-fill"></i>
                                            </a>
                                            <a className={"btn btn-sm btn-outline-danger"}
                                               onClick={e => {
                                                   removeClusterId(info.clusterId);
                                               }}>
                                                <i className="bi bi-trash"></i>
                                            </a>
                                        </td>
                                        <td className={"text-center"}>
                                            <Link to={`/cluster/${info.clusterId}`}>
                                                {info.clusterId}
                                            </Link>
                                        </td>
                                        <td className={"text-center"}>
                                            <Link to={`/cluster/${info.clusterId}`}>
                                                {info.clusterName}
                                            </Link>
                                        </td>
                                        <td className={"text-center text-truncate"}>{info.contactPoints}</td>
                                        <td className={"text-center"}>{info.port}</td>
                                        <td className={"text-center"}>{info.localDatacenter}</td>
                                    </tr>
                                )
                            }) : <tr>
                                <td className={"text-center"} scope="col" colSpan={6}>No Data</td>
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
                    getClusterList={getClusterList}
                    handleClose={() => closeClusterModal()}/>
            }

        </>
    )
}

export default ClusterList;
