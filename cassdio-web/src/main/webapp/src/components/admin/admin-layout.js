import {Link, Outlet} from "react-router-dom";
import React from "react";

import CassdioSidebar from "../layout/cassdio-sidebar";

const AdminLayout = () => {
    return (
        <div className="container-fluid h-100">
            <div className="row">

                <CassdioSidebar>
                    <ul className="nav flex-column">
                        <li className="nav-item">
                            <Link
                                className={`nav-link d-flex align-items-center gap-2 link-body-emphasis text-decoration-none`}
                                to={`/admin`}>
                                <i className="bi bi-house"></i> Home
                            </Link>
                        </li>
                        <li className="nav-item">
                            <Link
                                className={`nav-link d-flex align-items-center gap-2 link-body-emphasis text-decoration-none`}
                                to={`/admin/cluster`}>
                                <i className="bi bi-database-fill"></i> Cluster
                            </Link>
                        </li>
                    </ul>
                </CassdioSidebar>

                <main className="col-md-9 ms-sm-auto col-lg-10 px-md-4 mb-5">
                    <Outlet/>
                </main>
            </div>
        </div>
    )
}

export default AdminLayout;
