import {Link, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import ClusterBreadcrumb from "../../components/cluster/cluster-breadcrumb";
import Spinner from "../../components/common/spinner";
import axios from "axios";
import useCassdio from "../../hooks/useCassdio";

const ClusterDashboardPage = ({}) => {

    const routeParams = useParams();
    const [clusterId, setClusterId] = useState(``)

    const {errorCatch} = useCassdio();
    const [keyspaceList, setKeyspaceList] = useState([]);
    const [keyspaceLoading, setKeyspaceLoading] = useState(false);

    useEffect(() => {
        //show component
        setClusterId(routeParams.clusterId);

        setKeyspaceLoading(true)

        //TODO: 모듈화 해야함.
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

        return () => {
            //hide component
        };
    }, [clusterId]);

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

export default ClusterDashboardPage;
