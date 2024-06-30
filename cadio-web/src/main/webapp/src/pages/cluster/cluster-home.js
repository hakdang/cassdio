import {Link, useParams} from "react-router-dom";
import {useEffect} from "react";
import QueryEditor from "../query-editor";

const ClusterHome = () => {
    const routeParams = useParams();

    useEffect(() => {
        //show component

        console.log("routeParams ", routeParams.clusterId)

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
                                      to="/cluster/1">
                                   Cluster Information
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
                            {/*<li className="nav-item">*/}
                            {/*    <a className="nav-link d-flex align-items-center gap-2" href="#">*/}

                            {/*        Products*/}
                            {/*    </a>*/}
                            {/*</li>*/}
                            {/*<li className="nav-item">*/}
                            {/*    <a className="nav-link d-flex align-items-center gap-2" href="#">*/}
                            {/*        Customers*/}
                            {/*    </a>*/}
                            {/*</li>*/}
                            {/*<li className="nav-item">*/}
                            {/*    <a className="nav-link d-flex align-items-center gap-2" href="#">*/}
                            {/*        Reports*/}
                            {/*    </a>*/}
                            {/*</li>*/}
                            {/*<li className="nav-item">*/}
                            {/*    <a className="nav-link d-flex align-items-center gap-2" href="#">*/}
                            {/*        Integrations*/}
                            {/*    </a>*/}
                            {/*</li>*/}
                        </ul>

                        <h6 className="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-body-secondary ">
                            <span>Keyspace</span>
                            {/*<a className="link-secondary" href="#" aria-label="Add a new report">*/}
                            {/*</a>*/}
                        </h6>
                        <ul className="nav flex-column mb-auto">
                            <li className="nav-item">
                                <a className="nav-link d-flex align-items-center gap-2" href="#">
                                    system
                                </a>
                            </li>
                            <li className="nav-item">
                                <a className="nav-link d-flex align-items-center gap-2" href="#">
                                    system_schema
                                </a>
                            </li>
                            <li className="nav-item">
                                <a className="nav-link d-flex align-items-center gap-2" href="#">
                                    system_auth
                                </a>
                            </li>
                            <li className="nav-item">
                                <a className="nav-link d-flex align-items-center gap-2" href="#">
                                    system_virtual_schema
                                </a>
                            </li>
                            <li className="nav-item">
                                <a className="nav-link d-flex align-items-center gap-2" href="#">
                                    system_traces
                                </a>
                            </li>
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
                <div
                    className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h1 className="h2">Dashboard</h1>
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

                <h2>Sample Query Editor</h2>

                <QueryEditor/>

            </main>
        </>
    )
}

export default ClusterHome;
