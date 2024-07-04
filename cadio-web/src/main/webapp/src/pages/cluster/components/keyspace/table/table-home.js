import {Link, useParams} from "react-router-dom";
import {useClusterState} from "../../../context/clusterContext";
import {useEffect} from "react";

const TableHome = (props) => {
    const routeParams = useParams();

    //const {doGetKeyspaceList} = useCluster();
    const {
        keyspaceList,
        keyspaceListLoading,
    } = useClusterState();

    useEffect(() => {
        //show component

        console.log("routeParams ", routeParams.clusterId)
        console.log("routeParams ", routeParams.keyspaceName)
        console.log("routeParams ", routeParams.tableName)

        return () => {
            //hide component

        };
    }, []);

    return (
        <>
            <div className={"row pt-3"}>
                <nav className={"breadcrumb-arrow"} aria-label="breadcrumb">
                    <ol className="breadcrumb">
                        <li className="breadcrumb-item">
                            <Link to={`/cluster/${routeParams.clusterId}`}
                                  className={"link-body-emphasis text-decoration-none"}>
                                Cluster
                            </Link>
                        </li>
                        <li className="breadcrumb-item">
                            <Link to={`/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}`}
                                  className={"link-body-emphasis text-decoration-none"}>
                                {routeParams.keyspaceName}
                            </Link>
                        </li>
                        <li className="breadcrumb-item active" aria-current="page">
                            {routeParams.tableName}
                        </li>
                    </ol>
                </nav>
            </div>

            <div
                className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h2 className="h2">Table</h2>
                {/*<div className="btn-toolbar mb-2 mb-md-0">*/}
                {/*    <div className="btn-group me-2">*/}
                {/*        <button type="button" className="btn btn-sm btn-outline-secondary">Share</button>*/}
                {/*        <button type="button" className="btn btn-sm btn-outline-secondary">Export</button>*/}
                {/*    </div>*/}
                {/*    <button type="button"*/}
                {/*            className="btn btn-sm btn-outline-secondary dropdown-toggle d-flex align-items-center gap-1">*/}
                {/*        This week*/}
                {/*    </button>*/}
                {/*</div>*/}
            </div>

            <ul className="nav nav-tabs">
                <li className="nav-item">
                    <Link
                        to={`/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}/table/${routeParams.tableName}`}
                        className={`nav-link link-body-emphasis text-decoration-none ${props.submenu === 'HOME' && `active`}`}>
                        Home
                    </Link>
                </li>
                <li className="nav-item">
                    <Link
                        to={`/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}/table/${routeParams.tableName}/row`}
                        className={`nav-link link-body-emphasis text-decoration-none ${props.submenu === 'ROW' && `active`}`}>
                        Rows
                    </Link>
                </li>
                <li className="nav-item dropdown ">
                    <a className={`nav-link dropdown-toggle link-body-emphasis text-decoration-none`}
                       data-bs-toggle="dropdown" role="button"
                       aria-expanded="false">Data</a>
                    <ul className="dropdown-menu">
                        <li>
                            <Link
                                to={`/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}/table/${routeParams.tableName}/export`}
                                className={`dropdown-item ${props.submenu === 'EXPORT' && `active`}`}>
                                Export
                            </Link>
                        </li>
                        <li>
                            <Link
                                to={`/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}/table/${routeParams.tableName}/import`}
                                className={`dropdown-item ${props.submenu === 'IMPORT' && `active`}`}>
                                Import
                            </Link>
                        </li>
                        {/*<li>*/}
                        {/*    <hr className="dropdown-divider"/>*/}
                        {/*</li>*/}
                        {/*<li><a className="dropdown-item" href="#">Separated link</a></li>*/}
                    </ul>
                </li>
                {/*<li className="nav-item">*/}
                {/*    <a className="nav-link" href="#">Link</a>*/}
                {/*</li>*/}
                {/*<li className="nav-item">*/}
                {/*    <a className="nav-link disabled" aria-disabled="true">Disabled</a>*/}
                {/*</li>*/}
            </ul>

            {props.children}
        </>
    )
}

export default TableHome;
