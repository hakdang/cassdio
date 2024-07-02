import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import axios from "axios";
import {DateUtils} from "../../../utils/timeUtils";
import Spinner from "../../../components/spinner";

const NodesHome = () => {

    const routeParams = useParams();

    const [nodeLoading, setNodeLoading] = useState(false);
    const [nodeList, setNodeList] = useState([]);
    const [availableNodeSize, setAvailableNodeSize] = useState(0)
    const [totalNodeSize, setTotalNodeSize] = useState(0)

    useEffect(() => {
        //show component
        setNodeList([])

        setNodeLoading(true)
        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${routeParams.clusterId}/node`,
        }).then((response) => {
            console.log("res ", response);
            const nodes = response.data.result.items
            setNodeList(nodes)
            setTotalNodeSize(nodes.length)
            setAvailableNodeSize(nodes.filter(node => node.nodeState === 'UP').length)
        }).catch((error) => {
            console.log(error)
            //TODO : error catch
        }).finally(() => {
            setNodeLoading(false)
        });

        return () => {
            //hide component

        };
    }, [routeParams.clusterId, routeParams.keyspaceName]);

    const nodesByDcAndRack = nodeList.reduce((acc, node) => {
        const { datacenter, rack } = node;
        if (!acc[datacenter]) {
            acc[datacenter] = {};
        }
        if (!acc[datacenter][rack]) {
            acc[datacenter][rack] = [];
        }
        acc[datacenter][rack].push(node);
        return acc;
    }, {});

    return (
        <>
            <div
                className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h2 className="h2">Node List</h2>
            </div>

            <div
                className={`-flex justify-content-between flex-wrap flex-md-nowrap align-items-center alert ${availableNodeSize <= 0 || availableNodeSize === totalNodeSize ? 'alert-primary' : 'alert-danger'}`}>
                Available nodes: {availableNodeSize}/{totalNodeSize}
            </div>

            <Spinner loading={nodeLoading}>
                <div className="table-responsive small">
                    {Object.keys(nodesByDcAndRack).map((datacenter) => (
                        <div key={datacenter} className="mb-5">
                            <h3 className="mb-3 border-bottom">Datacenter: {datacenter}</h3>
                            {Object.keys(nodesByDcAndRack[datacenter]).map((rack) => (
                                <div key={rack} className="mb-4">
                                    <h5 className="mb-2">{rack}</h5>
                                    <table className="table table-striped table-sm">
                                        <thead>
                                        <tr>
                                            <th className="text-center" scope="col">ID</th>
                                            <th className="text-center" scope="col">Node State</th>
                                            <th className="text-center" scope="col">Host IP</th>
                                            <th className="text-center" scope="col">DC</th>
                                            <th className="text-center" scope="col">Rack</th>
                                            <th className="text-center" scope="col">Cassandra Version</th>
                                            <th className="text-center" scope="col">Up-Time</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {nodesByDcAndRack[datacenter][rack].map((node) => (
                                            <tr key={node.nodeId} className={node.nodeState !== 'UP' ? 'table-danger' : ''}>
                                                <td className="text-center">{node.nodeId}</td>
                                                <td className="text-center">{node.nodeState}</td>
                                                <td className="text-center">{node.hostIp}</td>
                                                <td className="text-center">{node.datacenter}</td>
                                                <td className="text-center">{node.rack}</td>
                                                <td className="text-center">{node.cassandraVersion}</td>
                                                <td className="text-center">{DateUtils.yyyyMMDDHHmmss(node.upSinceMillis)}</td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                </div>
                            ))}
                        </div>
                    ))}
                </div>
            </Spinner>
        </>
    )
}

export default NodesHome;
