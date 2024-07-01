import {Link, useParams} from "react-router-dom";
import {useEffect} from "react";
import QueryEditor from "../query-editor";
import useCluster from "./hooks/useCluster";
import {useClusterState} from "./context/clusterContext";

const ClusterView = (props) => {
    const routeParams = useParams();

    const {doGetKeyspaceList} = useCluster();
    const {
        keyspaceList,
        keyspaceListLoading,
    } = useClusterState();

    useEffect(() => {
        //show component

        console.log("routeParams ", routeParams.clusterId)

        doGetKeyspaceList();

        return () => {
            //hide component

        };
    }, []);

    return (
        <>
            <div className="sidebar border border-right col-md-3 col-lg-2 p-0 bg-body-tertiary">
                <div className="offcanvas-md offcanvas-end bg-body-tertiary" tabIndex="-1" id="sidebarMenu"
                     aria-labelledby="sidebarMenuLabel">
                    <div className="offcanvas-header">
                        <h5 className="offcanvas-title" id="sidebarMenuLabel">Cadio</h5>
                        <button type="button" className="btn-close" data-bs-dismiss="offcanvas"
                                data-bs-target="#sidebarMenu" aria-label="Close"></button>
                    </div>
                    <div className="offcanvas-body d-md-flex flex-column p-0 pt-lg-3 overflow-y-auto">
                        <ul className="nav flex-column">
                            <li className="nav-item">
                                <Link className={`nav-link d-flex align-items-center gap-2 active`}
                                      to={`/cluster/${routeParams.clusterId}`}>
                                    Cluster Home
                                </Link>
                            </li>
                            <li className="nav-item">
                                <a className="nav-link d-flex align-items-center gap-2" href="#">
                                    Query Editor
                                </a>
                            </li>
                            <li className="nav-item">
                                <a className="nav-link d-flex align-items-center gap-2" href="#">
                                    Metrics
                                </a>
                            </li>
                        </ul>

                        <h6 className="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-body-secondary ">
                            <span>Keyspace</span>
                            {/*<a className="link-secondary" href="#" aria-label="Add a new report">*/}
                            {/*</a>*/}
                        </h6>
                        <ul className="nav flex-column mb-auto">
                            {
                                keyspaceListLoading ?
                                    <li className="nav-item text-center">
                                        <div className="spinner-border text-danger" role="status">
                                            <span className="visually-hidden">Loading...</span>
                                        </div>
                                    </li> :
                                    keyspaceList.map((info, infoIndex) => {
                                        return (
                                            <li className="nav-item" key={`sidebarKeyspace${infoIndex}`}>
                                                <Link
                                                    to={`/cluster/${routeParams.clusterId}/keyspace/${info.keyspaceName}`}
                                                    className={`nav-link d-flex align-items-center gap-2`}>
                                                    {info.keyspaceName}
                                                </Link>
                                            </li>
                                        )
                                    })
                            }

                        </ul>

                        <hr className="my-3"/>

                        <ul className="nav flex-column mb-auto">
                            <li className="nav-item">
                                <a className="nav-link d-flex align-items-center gap-2" href="#">
                                    Settings
                                </a>
                            </li>
                            <li className="nav-item">
                                <a className="nav-link d-flex align-items-center gap-2" href="#">
                                    Sign out
                                </a>
                            </li>
                        </ul>

                    </div>
                </div>
            </div>

            <main className="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                {props.children}

            </main>
        </>
    )
}

export default ClusterView;
