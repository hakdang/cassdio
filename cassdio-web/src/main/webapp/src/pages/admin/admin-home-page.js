import React from "react";
import {Link} from "react-router-dom";

const AdminHomePage = () => {
    return (
        <>
            <div
                className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h2 className="h2">Admin Home</h2>
            </div>

            <div className={"row"}>
                <div className={"col-sm-12 col-md-4"}>
                    <div className="card">
                        <div className="card-body">
                            <h5 className="card-title">Cluster Manage</h5>
                            <Link to={`/admin/cluster`} className="btn btn-primary">Go To</Link>
                        </div>
                    </div>
                </div>
            </div>
        </>
    )
}

export default AdminHomePage;
