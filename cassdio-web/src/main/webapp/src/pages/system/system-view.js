import {Link} from "react-router-dom";

import CassdioSidebar from "../../components/layout/cassdio-sidebar";


const SystemView = (props) => {

    return (
        <div className="container-fluid h-100  mb-5">
            <div className="row">

                <CassdioSidebar>
                    <ul className="nav flex-column">
                        <li className="nav-item">
                            <Link
                                className={`nav-link d-flex align-items-center gap-2 link-body-emphasis text-decoration-none`}
                                to={`/system`}>
                                <i className="bi bi-house"></i> System Home
                            </Link>
                            <Link
                                className={`nav-link d-flex align-items-center gap-2 link-body-emphasis text-decoration-none`}
                                to={`/system/cluster`}>
                                <i className="bi bi-house"></i> Cluster Manage
                            </Link>
                        </li>
                    </ul>
                </CassdioSidebar>

                <main className="col-md-9 ms-sm-auto col-lg-10 px-md-4  mb-5">
                    {props.children}
                </main>
            </div>
        </div>
    )
}

export default SystemView;
