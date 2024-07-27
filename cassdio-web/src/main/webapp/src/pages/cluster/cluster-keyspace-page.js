import {Link, useParams} from "react-router-dom";
import useCassdio from "../../hooks/useCassdio";
import React, {useEffect, useState} from "react";
import axios from "axios";
import ClusterKeyspaceBreadcrumb from "../../components/cluster/cluster-keyspace-breadcrumb";
import Spinner from "../../components/common/spinner";
import {CassdioUtils} from "../../utils/cassdioUtils";
import KeyspaceTableList from "../../components/cluster/keyspace-table-list";

const ClusterKeyspacePage = () => {


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
            <ClusterKeyspaceBreadcrumb
                clusterId={routeParams.clusterId}
                keyspaceName={routeParams.keyspaceName}
                active={"KEYSPACE"}
            />

            <div
                className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h2 className="h2">
                    Keyspace
                </h2>
                <div className="btn-toolbar mb-2 mb-md-0">
                    <div className="btn-group me-2">
                        <Link role="button" className="btn btn-sm btn-outline-secondary"
                         to={`/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}/query`}>
                            <i className="bi bi-journal-code"></i> Query
                        </Link>
                        {/*<button type="button" className="btn btn-sm btn-outline-secondary">Export</button>*/}
                    </div>
                    {/*<button type="button"*/}
                    {/*        className="btn btn-sm btn-outline-secondary dropdown-toggle d-flex align-items-center gap-1">*/}
                    {/*    This week*/}
                    {/*</button>*/}
                </div>
            </div>
            <Spinner loading={detailLoading}>
                <div className={"row"}>
                    <div className={"col-sm-8"}>


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
                                    <h4 className={"h4"}>Describe</h4>
                                    <div className={"col"}>
                                        <code className={"text-break"}>
                                            {keyspaceDescribe}
                                        </code>
                                    </div>
                                </div>
                            </>
                        }
                    </div>
                    <div className={"col-sm-4"}>
                        <ul className={"list-group list-group-flush"}>
                            <li className="list-group-item d-flex justify-content-between align-items-start">
                                <Link
                                    to={`/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}/compaction`}
                                >
                                    Compaction History
                                </Link>
                            </li>
                        </ul>
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

export default ClusterKeyspacePage;
