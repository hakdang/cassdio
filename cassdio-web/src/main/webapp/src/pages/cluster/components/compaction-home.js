import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import axios from "axios";
import Spinner from "components/spinner";
import useCassdio from "commons/hooks/useCassdio";
import {ByteFormatUtils} from "../../../utils/byteFormat";

const CompactionHome = () => {

    const routeParams = useParams();
    const {errorCatch} = useCassdio();

    const [loading, setLoading] = useState(false);
    const [histories, setHistories] = useState([]);

    useEffect(() => {
        console.log(routeParams.keyspaceName)
        setLoading(true)
        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${routeParams.clusterId}/compaction-history`,
            params: {
                keyspace: routeParams.keyspaceName
            }
        }).then((response) => {
            setHistories(response.data.result.histories)
        }).catch((error) => {
            errorCatch(error)
        }).finally(() => {
            setLoading(false)
        });

        return () => {
        };
    }, [routeParams.clusterId, routeParams.keyspaceName]);

    return (
        <>
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
                                            {compaction.keyspaceName}
                                        </td>
                                        <td className={"text-center"}>
                                            {compaction.columnFamilyName}
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

export default CompactionHome;
