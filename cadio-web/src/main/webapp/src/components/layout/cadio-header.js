import {Link} from "react-router-dom";
import cadioHelper from "../../utils/cadioHelper";

const CadioHeader = () => {

    return (
        <header className="navbar sticky-top bg-info  flex-md-nowrap shadow" data-bs-theme="dark">
            <Link className={`navbar-brand col-md-3 col-lg-2 me-0 px-3 fs-6 text-black`}
                  to="/">
                Cadio
            </Link>

            <ul className="navbar-nav flex-row d-md-none">
                <li className="nav-item text-nowrap">
                    <button className="nav-link px-3 text-white" type="button" data-bs-toggle="offcanvas"
                            data-bs-target={`#${cadioHelper.CADIO_SIDEBAR_ID}`}
                            aria-controls={`${cadioHelper.CADIO_SIDEBAR_ID}`}
                            aria-expanded="false"
                            aria-label="Toggle navigation">
                        <i className="bi bi-list"></i>
                    </button>
                </li>
            </ul>
        </header>
    )
}

export default CadioHeader;
