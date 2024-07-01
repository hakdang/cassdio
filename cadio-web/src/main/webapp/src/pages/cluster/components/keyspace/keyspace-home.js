import {Link, useParams} from "react-router-dom";
import {useClusterState} from "../../context/clusterContext";
import {useEffect} from "react";

const KeyspaceHome = () => {

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
                        <li className="breadcrumb-item active" aria-current="page">
                            <Link to={`/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}`}>
                                {routeParams.keyspaceName}
                            </Link>
                        </li>
                    </ol>
                </nav>
            </div>

            <div
                className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h2 className="h2">Keyspace</h2>
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

            <Link to={`/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}/table/testTable`}>Table Example</Link>
        </>
    )
}

export default KeyspaceHome;
