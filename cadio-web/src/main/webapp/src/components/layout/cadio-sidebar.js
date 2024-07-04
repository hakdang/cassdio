import cadioHelper from "../../utils/cadioHelper";

const CadioSidebar = (props) => {

    return (
        <div className="sidebar border border-right col-md-3 col-lg-2 p-0 bg-info-subtle">
            <div className="offcanvas-md offcanvas-end bg-info-subtle" tabIndex="-1"
                 id={`${cadioHelper.CADIO_SIDEBAR_ID}`}
                 aria-labelledby="sidebarMenuLabel">
                <div className="offcanvas-header">
                    <h5 className="offcanvas-title" id="sidebarMenuLabel">Cadio</h5>
                    <button type="button" className="btn-close" data-bs-dismiss="offcanvas"
                            data-bs-target={`#${cadioHelper.CADIO_SIDEBAR_ID}`} aria-label="Close"></button>
                </div>
                <div className="offcanvas-body d-md-flex flex-column p-0 pt-lg-3 overflow-y-auto min-vh-100">
                    {props.children}
                </div>
            </div>
        </div>
    )
}

export default CadioSidebar;
