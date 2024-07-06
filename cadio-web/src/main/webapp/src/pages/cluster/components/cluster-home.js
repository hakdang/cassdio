import {Link, useParams} from "react-router-dom";
import {useClusterState} from "../context/clusterContext";
import {useEffect} from "react";
import useCluster from "../hooks/useCluster";

const ClusterHome = () => {

    const routeParams = useParams();
    const {doGetKeyspaceList} = useCluster();
    //const {doGetKeyspaceList} = useCluster();
    const {
        keyspaceList,
        keyspaceListLoading,
    } = useClusterState();

    useEffect(() => {
        //show component

        doGetKeyspaceList();

        return () => {
            //hide component

        };
    }, []);

    return (
        <>
            <div className={"row pt-3"}>
                <nav className={"breadcrumb-arrow"} aria-label="breadcrumb">
                    <ol className="breadcrumb">
                        <li className="breadcrumb-item active">
                            Cluster
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

            {
                keyspaceListLoading ? <div className="d-flex justify-content-center">
                        <div className="spinner-border" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </div>
                    </div> :
                    <div className="table-responsive small">
                        <table className="table table-sm table-hover">
                            <thead>
                            <tr className={"table-dark"}>
                                <th className={"text-center"} scope="col">Keyspace Name</th>
                                <th className={"text-center"} scope="col">replication</th>
                            </tr>
                            </thead>
                            <tbody className="table-group-divider">
                            {
                                keyspaceList && keyspaceList.length > 0 && keyspaceList.map((info, infoIndex) => {
                                    return (
                                        <tr key={infoIndex}>
                                            <th>
                                                <Link
                                                    to={`/cluster/${routeParams.clusterId}/keyspace/${info.keyspaceName}`}
                                                    className={``}>
                                                    <i className="bi bi-database"></i> {info.keyspaceName}
                                                </Link>
                                            </th>
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
            }
        </>
    )
}

export default ClusterHome;
