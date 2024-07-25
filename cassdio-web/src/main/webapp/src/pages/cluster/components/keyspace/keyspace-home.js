import {Link, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import axios from "axios";
import Spinner from "components/spinner";
import KeyspaceTableList from "./keyspace-table-list";
import useCassdio from "commons/hooks/useCassdio";
import {CassdioUtils} from "utils/cassdioUtils";
import ClusterBreadcrumb from "../cluster-breadcrumb";

const KeyspaceHome = () => {

    const routeParams = useParams();
    const {errorCatch} = useCassdio();

    const [detailLoading, setDetailLoading] = useState(false);
    const [keyspaceDescribe, setKeyspaceDescribe] = useState('');
    const [keyspaceDetail, setKeyspaceDetail] = useState({
        rowHeader: [],
        row: {}
    });
    const [tableList, setTableList] = useState({});

    useEffect(() => {
        //show component
        setKeyspaceDescribe('');
        setDetailLoading(true)
        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}`,
            params: {
                withTableList: true,
            }
        }).then((response) => {
            setKeyspaceDescribe(response.data.result.describe)
            setKeyspaceDetail(response.data.result.detail);

            setTableList(response.data.result.tableList)

        }).catch((error) => {
            errorCatch(error)
        }).finally(() => {
            setDetailLoading(false)
        });

        return () => {
            //hide component

        };
    }, [routeParams.clusterId, routeParams.keyspaceName]);

    return (
        <>
            <ClusterBreadcrumb
                clusterId={routeParams.clusterId}
                keyspaceName={routeParams.keyspaceName}
                active={"KEYSPACE"}
            />

            <div
                className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h2 className="h2">
                    Keyspace
                </h2>
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

                {
                    keyspaceDetail && <>
                        <div className="table-responsive small">
                            <table
                                className="table table-sm table-hover">
                                <tbody>

                                {
                                    keyspaceDetail.rowHeader.map((info, infoIndex) => {
                                        return (
                                            <tr key={`resultBody${infoIndex}`}>
                                                <th className={"text-center text-break"}>
                                                    {info.columnName}
                                                </th>
                                                <td className={"text-center text-break"}
                                                    key={`resultItem${infoIndex}`}>
                                                    {
                                                        CassdioUtils.renderData(keyspaceDetail.row[info.columnName])
                                                    }
                                                </td>
                                            </tr>
                                        )
                                    })
                                }
                                </tbody>
                            </table>
                        </div>

                    </>
                }

                {
                    keyspaceDescribe && <>
                        <div className={"row mb-3"}>
                            <h3 className={"h3"}>Describe</h3>
                            <div className={"col"}>
                                <code className={"text-break"}>
                                    {keyspaceDescribe}
                                </code>
                            </div>
                        </div>
                    </>
                }

                <div className={"row mt-3"}>
                    <div className={"col-md-6 col-sm-12"}>
                        <h2 className="h3">Monitoring</h2>
                        <div className="table-responsive small">
                            <table className="table table-sm table-hover">
                                <tbody>
                                <tr>
                                    <th>Compaction History</th>
                                    <td>
                                        <Link
                                            to={`/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}/compaction`}>
                                            <th>Link</th>
                                        </Link>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <div className={"row mt-3"}>
                    <div className={"col-md-6 col-sm-12"}>
                        <h2 className="h3">Tables</h2>
                        {
                            tableList &&
                            <KeyspaceTableList clusterId={routeParams.clusterId}
                                               keyspaceName={routeParams.keyspaceName}
                                               tableList={tableList}/>
                        }


                    </div>
                    {/*<div className={"col-md-6 col-sm-12"}>*/}
                    {/*    <h2 className="h3">Views</h2>*/}


                    {/*</div>*/}
                </div>
            </Spinner>
        </>
    )
}

export default KeyspaceHome;
