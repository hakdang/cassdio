import {Link} from "react-router-dom";

import CassdioSidebar from "../components/layout/cassdio-sidebar";

import ClusterList from "../components/cluster/cluster-list";

const CassdioHomePage = () => {
    return (
        <div className="container-fluid h-100 mb-5">
            <div className="row">

                <CassdioSidebar>
                    <ul className="nav flex-column">
                        <li className="nav-item">
                            <Link
                                className={`nav-link d-flex align-items-center gap-2 link-body-emphasis text-decoration-none`}
                                to={`/`}>
                                <i className="bi bi-house"></i> Home
                            </Link>
                        </li>
                        <li className="nav-item">
                            <Link
                                className={`nav-link d-flex align-items-center gap-2 link-body-emphasis text-decoration-none`}
                                to={`/admin`}>
                                <i className="bi bi-gear-wide-connected"></i> Admin
                            </Link>
                        </li>
                    </ul>
                </CassdioSidebar>

                <main className="col-md-9 ms-sm-auto col-lg-10 px-md-4  mb-5">
                    <ClusterList/>
                </main>
            </div>
        </div>
    )
}

export default CassdioHomePage;
