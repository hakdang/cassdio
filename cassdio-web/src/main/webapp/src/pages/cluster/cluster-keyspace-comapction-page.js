import {Link, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import ClusterKeyspaceBreadcrumb from "../../components/cluster/cluster-keyspace-breadcrumb";
import Spinner from "../../components/common/spinner";
import {ByteFormatUtils} from "../../utils/byteFormat";
import clusterCompactionHistoryApi from "../../remotes/clusterCompactionHistoryApi";

const ClusterKeyspaceCompactionPage = () => {

    const routeParams = useParams();

    const [loading, setLoading] = useState(false);
    const [histories, setHistories] = useState([]);

    const clusterCompactionHistory = () => {
        clusterCompactionHistoryApi({
            clusterId: routeParams.clusterId,
            keyspaceName: routeParams.keyspaceName,
        }).then((data) => {
            if (!data.ok) {
                return;
            }

            setHistories(data.result.histories)
        }).finally(() => {
            setLoading(false)
        });
    }

    useEffect(() => {
        setLoading(true)
        clusterCompactionHistory();

        return () => {
        };
    }, [routeParams.clusterId, routeParams.keyspaceName]);

    return (
        <>
            <ClusterKeyspaceBreadcrumb
                clusterId={routeParams.clusterId}
                keyspaceName={routeParams.keyspaceName}
            />

            <div
                className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h2 className="h2">
                    Compaction History
                </h2>
            </div>

            <Spinner loading={loading}>
                <div className="table-responsive small">
                    <table className="table table-sm table-hover">
                        <thead>
                        <tr className={"table-dark"}>
                            <th className={"text-center"} scope="col">Keyspace</th>
                            <th className={"text-center"} scope="col">Table</th>
                            <th className={"text-center"} scope="col">Compacted At</th>
                            <th className={"text-center"} scope="col">Bytes In</th>
                            <th className={"text-center"} scope="col">Bytes Out</th>
                            <th className={"text-center"} scope="col">Row Merged</th>
                        </tr>
                        </thead>
                        <tbody className="table-group-divider">
                        {
                            histories && histories.length > 0 && histories.map((compaction, infoIndex) => {
                                return (
                                    <tr key={infoIndex}>
                                        <td className={"text-center"}>
                                            <Link
                                                to={`/cluster/${routeParams.clusterId}/keyspace/${compaction.keyspaceName}`}
                                                className={"link-body-emphasis text-decoration-underline"}>
                                                {compaction.keyspaceName}
                                            </Link>
                                        </td>
                                        <td className={"text-center"}>
                                            <Link
                                                to={`/cluster/${routeParams.clusterId}/keyspace/${compaction.keyspaceName}/table/${compaction.columnFamilyName}`}
                                                className={"link-body-emphasis text-decoration-underline"}>
                                                {compaction.columnFamilyName}
                                            </Link>
                                        </td>
                                        <td className={"text-center"}>
                                            {compaction.compactedAt}
                                        </td>
                                        <td className={"text-center"}>
                                            {ByteFormatUtils.formatBytes(compaction.bytesIn)}
                                        </td>
                                        <td className={"text-center"}>
                                            {ByteFormatUtils.formatBytes(compaction.bytesOut)}
                                        </td>
                                        <td className={"text-center"}>
                                            {JSON.stringify(compaction.rowMerged)}
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

export default ClusterKeyspaceCompactionPage;
