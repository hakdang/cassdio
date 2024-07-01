import {Link, useParams} from "react-router-dom";
import QueryEditor from "./query/query-editor";
import {useClusterState} from "../context/clusterContext";
import {useEffect} from "react";

const ClusterHome = () => {

    const routeParams = useParams();

    //const {doGetKeyspaceList} = useCluster();
    const {
        keyspaceList,
        keyspaceListLoading,
    } = useClusterState();

    useEffect(() => {
        //show component

        console.log("routeParams ", routeParams.clusterId)

        return () => {
            //hide component

        };
    }, []);

    return (
        <>
            <div className={"row pt-3"}>
                <nav aria-label="breadcrumb">
                    <ol className="breadcrumb">
                        <li className="breadcrumb-item active">
                            <Link to={`/cluster/${routeParams.clusterId}`} className={"link-body-emphasis text-decoration-none"}>
                                Cluster {routeParams.clusterId}
                            </Link>
                        </li>
                    </ol>
                </nav>
            </div>

            <div
                className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h2 className="h2">Keyspace</h2>
                <div className="btn-toolbar mb-2 mb-md-0">
                    <div className="btn-group me-2">
                    <button type="button" className="btn btn-sm btn-outline-secondary">Share</button>
                        <button type="button" className="btn btn-sm btn-outline-secondary">Export</button>
                    </div>
                    <button type="button"
                            className="btn btn-sm btn-outline-secondary dropdown-toggle d-flex align-items-center gap-1">
                        This week
                    </button>
                </div>
            </div>

            <div className="table-responsive small">
                <table className="table table-striped table-sm">
                    <thead>
                    <tr>
                        <th className={"text-center"} scope="col">Keyspace Name</th>
                        <th className={"text-center"} scope="col">Durable Writes</th>
                        <th className={"text-center"} scope="col">replication</th>
                    </tr>
                    </thead>
                    <tbody>
                    {
                        keyspaceListLoading ?
                            <tr>
                                <td className={"text-center"} colSpan={5}>
                                    <div className="spinner-border text-danger" role="status">
                                        <span className="visually-hidden">Loading...</span>
                                    </div>
                                </td>
                            </tr> :
                            keyspaceList.map((info, infoIndex) => {
                                return (
                                    <tr key={infoIndex}>
                                        <th className={"text-center"}>
                                            <Link
                                                to={`/cluster/${routeParams.clusterId}/keyspace/${info.keyspaceName}`}
                                                className={`nav-link d-flex align-items-center gap-2`}>
                                                <i className="bi bi-database"></i> {info.keyspaceName}
                                            </Link>
                                        </th>
                                        <td className={"text-center"}>
                                            {info.durableWrites}
                                        </td>
                                        <td className={"text-center"}>
                                            {JSON.stringify(info.replication)}
                                        </td>
                                    </tr>
                                )
                            })
                    }

                    </tbody>
                </table>
            </div>
        </>
    )
}

export default ClusterHome;
