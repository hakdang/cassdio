import {Link, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import axios from "axios";
import useCassdio from "commons/hooks/useCassdio";
import Spinner from "components/spinner";
import ClusterBreadcrumb from "./cluster-breadcrumb";

const ClusterHome = () => {
    const {errorCatch} = useCassdio();
    const routeParams = useParams();
    const [keyspaceList, setKeyspaceList] = useState([]);
    const [keyspaceLoading, setKeyspaceLoading] = useState(false);

    useEffect(() => {
        //show component
        setKeyspaceLoading(true)

        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${routeParams.clusterId}/keyspace`,
            params: {}
        }).then((response) => {
            setKeyspaceList(response.data.result.keyspaceList);

        }).catch((error) => {
            errorCatch(error)
        }).finally(() => {
            setKeyspaceLoading(false)
        });
        //

        return () => {
            //hide component

        };
    }, []);

    return (
        <>
            <ClusterBreadcrumb
                clusterId={routeParams.clusterId}
                active={"CLUSTER"}
            />

            <div
                className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h2 className="h2">
                    Keyspace
                </h2>
            </div>

            <Spinner loading={keyspaceLoading} mode={'justify-content-center'}>
                <div className="table-responsive small">
                    <table className="table table-sm table-hover">
                        <thead>
                        <tr className={"table-dark"}>
                            <th className={"text-center"} scope="col">Keyspace Name</th>
                            <th className={"text-center"} scope="col">Replication</th>
                        </tr>
                        </thead>
                        <tbody className="table-group-divider">
                        {
                            keyspaceList && keyspaceList.length > 0 && keyspaceList.map((info, infoIndex) => {
                                return (
                                    <tr key={infoIndex}>
                                        <th className={"text-center"}>
                                            <Link to={`/cluster/${routeParams.clusterId}/keyspace/${info.keyspaceName}`}>
                                                <i className="bi bi-database"></i> {info.keyspaceName}
                                            </Link>
                                        </th>
                                        <td className={"text-center"}>
                                            {JSON.stringify(info.replication)}
                                        </td>
                                    </tr>
                                )
                            })
                        }
                        </tbody>
                    </table>
                </div>
            </Spinner>
        </>
    )
}

export default ClusterHome;
