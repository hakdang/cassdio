import React, {useEffect, useState} from "react";
import {Link, Outlet, useParams} from "react-router-dom";

import {OverlayTrigger, Tooltip} from "react-bootstrap";
import CassdioSidebar from "components/layout/cassdio-sidebar";
import useKeyspace from "hooks/useKeyspace";

const ClusterLayout = ({}) => {
    const routeParams = useParams();

    const [clusterId, setClusterId] = useState(``)
    const {
        doGetKeyspaceNames,
        keyspaceNamesLoading,
        keyspaceGeneralNames,
        keyspaceSystemNames,
    } = useKeyspace();


    useEffect(() => {
        //show component
        setClusterId(routeParams.clusterId);
        doGetKeyspaceNames(routeParams.clusterId, false);

        return () => {
            //hide component
        };
    }, [clusterId]);

    return (
        <div className="container-fluid h-100">
            <div className="row">

                <CassdioSidebar>
                    <ul className="nav flex-column">
                        <li className="nav-item">
                            <Link
                                className={`nav-link d-flex align-items-center gap-2 link-body-emphasis text-decoration-none`}
                                to={`/cluster/${clusterId}`}>
                                <i className="bi bi-house"></i> Dashboard
                            </Link>
                        </li>
                        <li className="nav-item">
                            <Link
                                className={`nav-link d-flex align-items-center gap-2 link-body-emphasis text-decoration-none`}
                                to={`/cluster/${clusterId}/query`}>
                                <i className="bi bi-journal-code"></i> Query Editor
                            </Link>
                        </li>
                        <li className="nav-item">
                            <Link
                                className={`nav-link d-flex align-items-center gap-2 link-body-emphasis text-decoration-none`}
                                to={`/cluster/${clusterId}/monitoring`}>
                                <i className="bi bi-eye"></i> Monitoring
                            </Link>
                        </li>
                        {/*<li className="nav-item">*/}
                        {/*    <Link*/}
                        {/*        className={`nav-link d-flex align-items-center gap-2 link-body-emphasis text-decoration-none`}*/}
                        {/*        to={`/cluster/${clusterId}/client`}>*/}
                        {/*        <i className="bi bi-easel3"></i> Clients (v4+ only)*/}
                        {/*    </Link>*/}
                        {/*</li>*/}

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
                        <a className="link-danger"
                           role={"button"}
                           onClick={e => doGetKeyspaceNames(true)}>

                            <OverlayTrigger placement="top" overlay={
                                <Tooltip id="tooltip">
                                    Keyspace reload with cache evict
                                </Tooltip>
                            }>
                                <i className="bi bi-arrow-clockwise fs-5"></i>
                            </OverlayTrigger>


                        </a>
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
                                                to={`/cluster/${clusterId}/keyspace/${info.keyspaceName}`}
                                                className={`nav-link d-flex align-items-center link-body-emphasis text-decoration-none gap-2`}>
                                                <i className="bi bi-database"></i> {info.keyspaceName}
                                            </Link>
                                        </li>
                                    )
                                })
                        }

                    </ul>

                    <hr className="mt-3 mb-1"/>

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
                                                to={`/cluster/${clusterId}/keyspace/${info.keyspaceName}`}
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
                    <Outlet/>
                </main>
            </div>
        </div>
    )
}


export default ClusterLayout;
