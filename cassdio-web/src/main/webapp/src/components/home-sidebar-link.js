import {Link} from "react-router-dom";
import {useCassdioState} from "../context/cassdioContext";

const HomeSidebarLink = () => {
    return (
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
    )
}

export default HomeSidebarLink
