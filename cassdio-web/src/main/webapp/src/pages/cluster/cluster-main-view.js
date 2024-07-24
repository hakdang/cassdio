import {useEffect} from "react";
import {Link, useParams} from "react-router-dom";
import {useClusterState} from "./context/clusterContext";
import CassdioSidebar from "components/layout/cassdio-sidebar";
import useKeyspace from "./hooks/useKeyspace";

const ClusterMainView = (props) => {
    const routeParams = useParams();

    const {doGetKeyspaceNames} = useKeyspace();
    const {
        keyspaceGeneralNames,
        keyspaceSystemNames,
        keyspaceNamesLoading,
    } = useClusterState();

    useEffect(() => {
        //show component

        doGetKeyspaceNames();

        return () => {
            //hide component

        };
    }, [routeParams.clusterId]);

    return (
        <div className="container-fluid h-100">
            <div className="row">

                <CassdioSidebar>
                    <ul className="nav flex-column">
                        <li className="nav-item">
                            <Link
                                className={`nav-link d-flex align-items-center gap-2 link-body-emphasis text-decoration-none`}
                                to={`/cluster/${routeParams.clusterId}`}>
                                <i className="bi bi-house"></i> Cluster Home
                            </Link>
                        </li>
                        <li className="nav-item">
                            <Link
                                className={`nav-link d-flex align-items-center gap-2 link-body-emphasis text-decoration-none`}
                                to={`/cluster/${routeParams.clusterId}/nodes`}>
                                <i className="bi bi-server"></i> Node List
                            </Link>
                        </li>
                        <li className="nav-item">
                            <Link
                                className={`nav-link d-flex align-items-center gap-2 link-body-emphasis text-decoration-none`}
                                to={`/cluster/${routeParams.clusterId}/query`}>
                                <i className="bi bi-journal-code"></i> Query Editor
                            </Link>
                        </li>
                        {/*<li className="nav-item">*/}
                        {/*    <Link*/}
                        {/*        className={`nav-link d-flex align-items-center gap-2 link-body-emphasis text-decoration-none`}*/}
                        {/*        to={`/cluster/${clusterId}/metrics`}>*/}
                        {/*        <i className="bi bi-laptop"></i> Metrics*/}
                        {/*    </Link>*/}
                        {/*</li>*/}
                    </ul>

                    <h6 className="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-body-secondary ">
                        <span>Keyspace</span>
                        {/*<a className="link-secondary" href="#" aria-label="Add a new report">*/}
                        {/*</a>*/}
                    </h6>
                    <ul className="nav flex-column">
                        {
                            keyspaceNamesLoading ?
                                <li className="nav-item text-center">
                                    <div className="spinner-border text-danger" role="status">
                                        <span className="visually-hidden">Loading...</span>
                                    </div>
                                </li> :
                                keyspaceGeneralNames && keyspaceGeneralNames.length > 0 && keyspaceGeneralNames.map((info, infoIndex) => {
                                    return (
                                        <li className="nav-item" key={`sidebarKeyspace${infoIndex}`}>
                                            <Link
                                                to={`/cluster/${routeParams.clusterId}/keyspace/${info.keyspaceName}`}
                                                className={`nav-link d-flex align-items-center link-body-emphasis text-decoration-none gap-2`}>
                                                <i className="bi bi-database"></i> {info.keyspaceName}
                                            </Link>
                                        </li>
                                    )
                                })
                        }

                    </ul>

                    <h6 className="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 text-body-secondary ">
                        <span>System Keyspace</span>
                        {/*<a className="link-secondary" href="#" aria-label="Add a new report">*/}
                        {/*</a>*/}
                    </h6>
                    <ul className="nav flex-column">
                        {
                            keyspaceNamesLoading ?
                                <li className="nav-item text-center">
                                    <div className="spinner-border text-danger" role="status">
                                        <span className="visually-hidden">Loading...</span>
                                    </div>
                                </li> :
                                keyspaceSystemNames && keyspaceSystemNames.length > 0 && keyspaceSystemNames.map((info, infoIndex) => {
                                    return (
                                        <li className="nav-item" key={`sidebarKeyspace${infoIndex}`}>
                                            <Link
                                                to={`/cluster/${routeParams.clusterId}/keyspace/${info.keyspaceName}`}
                                                className={`nav-link d-flex align-items-center link-body-emphasis text-decoration-none gap-2`}>
                                                <i className="bi bi-database"></i> {info.keyspaceName}
                                            </Link>
                                        </li>
                                    )
                                })
                        }
                    </ul>
                </CassdioSidebar>

                <main className="col-md-9 ms-sm-auto col-lg-10 px-md-4 mb-5">
                    {props.children}
                </main>
            </div>
        </div>
    )
}

export default ClusterMainView;
