import {Link, useParams} from "react-router-dom";
import {useClusterState} from "../../context/clusterContext";
import {useEffect, useState} from "react";
import axios from "axios";
import KeyspaceDetailDescribe from "./keyspace-detail-describe";
import Spinner from "../../../../components/spinner";

const KeyspaceHome = () => {

    const routeParams = useParams();

    //const {doGetKeyspaceList} = useCluster();
    const {
        keyspaceList,
        keyspaceListLoading,
    } = useClusterState();

    const [detailLoading, setDetailLoading] = useState(false);
    const [describe, setDescribe] = useState('');

    useEffect(() => {
        //show component

        console.log("routeParams ", routeParams.clusterId)
        console.log("routeParams ", routeParams.keyspaceName)
        setDescribe('');
        setDetailLoading(true)
        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}`,
            params: {}
        }).then((response) => {
            console.log("res ", response);
            setDescribe(response.data.describe)
        }).catch((error) => {
            //TODO : error catch
        }).finally(() => {
            console.log("finally")
            setDetailLoading(false)
        });

        return () => {
            //hide component

        };
    }, [routeParams.clusterId, routeParams.keyspaceName]);

    return (
        <>
            <div className={"row pt-3"}>
                <nav aria-label="breadcrumb">
                    <ol className="breadcrumb">
                        <li className="breadcrumb-item">
                            <Link to={`/cluster/${routeParams.clusterId}`}
                                  className={"link-body-emphasis text-decoration-none"}>
                                Cluster {routeParams.clusterId}
                            </Link>
                        </li>
                        <li className="breadcrumb-item active" aria-current="page">
                            <Link to={`/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}`}
                                  className={"link-body-emphasis text-decoration-none"}>
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

            <Spinner loading={detailLoading}>
                <KeyspaceDetailDescribe describe={describe}/>

                
            </Spinner>


            <br/>
            <hr/>
            <Link to={`/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}/table/testTable`}>Table
                Example</Link>


        </>
    )
}

export default KeyspaceHome;
