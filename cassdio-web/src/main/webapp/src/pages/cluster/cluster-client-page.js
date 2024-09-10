import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import Spinner from "components/common/spinner";
import ClusterMonitoringNavLink from "components/cluster/cluster-monitoring-nav-link";
import clusterClientApi from "remotes/clusterClientApi";

const ClusterClientPage = () => {

    const routeParams = useParams();

    const [loading, setLoading] = useState(false);
    const [clients, setClients] = useState([]);
    const [groupedClients, setGroupedClients] = useState([])

    const pageInit = () => {
        setLoading(true)
        clusterClientApi({
            clusterId: routeParams.clusterId
        }).then((data) => {
            if (!data.ok) {
                return;
            }

            setClients(data.result.clients)

            const groupedClients = data.result.clients.reduce((acc, client) => {
                if (!acc[client.address]) {
                    acc[client.address] = {
                        address: client.address,
                        connectionCount: 0,
                        requestCount: 0
                    };
                }
                acc[client.address].connectionCount += 1; // Each entry represents a connection
                acc[client.address].requestCount += client.requestCount;
                return acc;
            }, {});

            const groupedClientsArray = Object.entries(groupedClients)
                .sort(([, a], [, b]) => b.connectionCount - a.connectionCount)
                .flatMap(([address, data]) => [address, data]);
            setGroupedClients(groupedClientsArray);
        }).finally(() => {
            setLoading(false)
        });
    }

    useEffect(() => {

        pageInit();

        return () => {
        };
    }, [routeParams.clusterId, pageInit]);

    return (
        <>
            <ClusterMonitoringNavLink active={"CLIENT"}/>

            <div
                className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h2 className="h2">
                    Active Client Connections
                </h2>
            </div>

            <h3 className={"h3"}>Summary</h3>

            <Spinner loading={loading}>
                <div className="table-responsive small">
                    <table className="table table-sm table-hover">
                        <thead>
                        <tr className={"table-dark"}>
                            <th className={"text-center"} scope="col">Address</th>
                            <th className={"text-center"} scope="col">Connection Count</th>
                            <th className={"text-center"} scope="col">Request Count</th>
                        </tr>
                        </thead>
                        <tbody className="table-group-divider">
                        {
                            groupedClients && groupedClients.length > 0 && groupedClients.map((client, index) => {
                                return (
                                    <tr key={index}>
                                        <td className={"text-center"}>{client.address}</td>
                                        <td className={"text-center"}>{client.connectionCount}</td>
                                        <td className={"text-center"}>{client.requestCount}</td>
                                    </tr>
                                )
                            })
                        }
                        </tbody>
                    </table>
                </div>
            </Spinner>

            <h3 className={"h3"}>Client List</h3>

            <Spinner loading={loading}>
                <div className="table-responsive small">
                    <table className="table table-sm table-hover">
                        <thead>
                        <tr className={"table-dark"}>
                            <th className={"text-center"} scope="col">Address</th>
                            <th className={"text-center"} scope="col">UserName</th>
                            <th className={"text-center"} scope="col">Driver Version</th>
                            <th className={"text-center"} scope="col">Request Count</th>
                        </tr>
                        </thead>
                        <tbody className="table-group-divider">
                        {
                            clients && clients.length > 0 && clients.map((client, index) => {
                                return (
                                    <tr key={index}>
                                        <td className={"text-center"}>
                                            {client.address}
                                        </td>
                                        <td className={"text-center"}>
                                            {client.username}
                                        </td>
                                        <td className={"text-center"}>
                                            {client.driverVersion || '-'}
                                        </td>
                                        <td className={"text-center"}>
                                            {client.requestCount}
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

export default ClusterClientPage;
