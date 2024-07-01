import {Link, useParams} from "react-router-dom";
import {useClusterState} from "../../../context/clusterContext";
import {useEffect} from "react";

const TableHome = () => {

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
                <nav aria-label="breadcrumb">
                    <ol className="breadcrumb">
                        <li className="breadcrumb-item">
                            <Link to={`/cluster/${routeParams.clusterId}`}>
                                Cluster {routeParams.clusterId}
                            </Link>
                        </li>
                        <li className="breadcrumb-item" aria-current="page">
                            <Link to={`/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}`}>
                                {routeParams.keyspaceName}
                            </Link>
                        </li>
                        <li className="breadcrumb-item active" aria-current="page">
                            <Link
                                to={`/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}/table/${routeParams.tableName}`}
                                className={"link-body-emphasis text-decoration-none"}>
                                {routeParams.tableName}
                            </Link>
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
                    <a className="nav-link active" aria-current="page" href="#">Home</a>
                </li>
                <li className="nav-item">
                    <a className="nav-link" aria-current="page" href="#">Detail</a>
                </li>
                <li className="nav-item dropdown">
                    <a className="nav-link dropdown-toggle" data-bs-toggle="dropdown" href="#" role="button"
                       aria-expanded="false">Data</a>
                    <ul className="dropdown-menu">
                        <li><a className="dropdown-item" href="#">Export</a></li>
                        <li><a className="dropdown-item" href="#">Import</a></li>
                        <li><a className="dropdown-item" href="#">Something else here</a></li>
                        <li>
                            <hr className="dropdown-divider"/>
                        </li>
                        <li><a className="dropdown-item" href="#">Separated link</a></li>
                    </ul>
                </li>
                <li className="nav-item">
                    <a className="nav-link" href="#">Link</a>
                </li>
                <li className="nav-item">
                    <a className="nav-link disabled" aria-disabled="true">Disabled</a>
                </li>
            </ul>

            <div className="table-responsive small">
                <table className="table table-striped table-sm">
                    <thead>
                    <tr>
                        <th scope="col">#</th>
                        <th scope="col">Header</th>
                        <th scope="col">Header</th>
                        <th scope="col">Header</th>
                        <th scope="col">Header</th>
                    </tr>
                    </thead>
                    <tbody>
                    {
                        new Array(1000).fill({
                            t1: "test",
                            t2: "value",
                            t3: "tttt",
                            t4: "adfasdfasd"
                        }).map((info, infoIndex) => {
                            return (
                                <tr>
                                    <td>{info.t1}</td>
                                    <td>{info.t2}</td>
                                    <td>{info.t3}</td>
                                    <td>{info.t4}</td>
                                    <td></td>
                                </tr>
                            )
                        })
                    }

                    </tbody>
                </table>
            </div>


        </>
    )
}

export default TableHome;
