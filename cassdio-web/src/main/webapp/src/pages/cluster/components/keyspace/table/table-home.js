import {Link, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import TableDetailModal from "./table-detail-modal";
import axios from "axios";
import TableDataManageModal from "./table-data-manage-modal";
import TableExportModal from "./table-export-modal";
import TableImportModal from "./table-import-modal";
import useCassdio from "../../../../../commons/hooks/useCassdio";
import {CassdioUtils} from "../../../../../utils/cassdioUtils";

const TableHome = (props) => {
    const routeParams = useParams();
    const [showDetail, setShowDetail] = useState(false);
    const [showExport, setShowExport] = useState(false);
    const [showImport, setShowImport] = useState(false);
    const [showDataManage, setShowDataManage] = useState(false);
    const [tableName, setTableName] = useState('');
    const {errorCatch, openToast} = useCassdio();
    const initQueryResult = {
        wasApplied: null,
        rows: [],
        columnNames: [],
    };

    const [queryLoading, setQueryLoading] = useState(false)
    const [queryResult, setQueryResult] = useState(initQueryResult)
    const [nextCursor, setNextCursor] = useState('')

    const getList = (cursor) => {
        if (cursor === null) {
            setQueryResult({
                wasApplied: null,
                rows: [],
                columnNames: [],
            })
        }

        setQueryLoading(true);

        axios({
            method: "POST",
            url: `/api/cassandra/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}/table/${routeParams.tableName}/row`,
            data: {
                pageSize: 50,
                timeoutSeconds: 3,
                cursor: cursor,
            },
        }).then((response) => {
            setNextCursor(response.data.result.nextCursor)

            setQueryResult({
                wasApplied: response.data.result.wasApplied,
                rows: [...queryResult.rows, ...response.data.result.rows],
                columnNames: response.data.result.columnNames,
            })
        }).catch((error) => {
            errorCatch(error);
        }).finally(() => {
            setQueryLoading(false);
        })
    }

    useEffect(() => {
        //show component

        setQueryResult(initQueryResult);
        getList(null)

        return () => {
            //hide component

        };
    }, []);

    return (
        <>
            <div className={"row pt-3"}>
                <nav className={"breadcrumb-arrow"} aria-label="breadcrumb">
                    <ol className="breadcrumb">
                        <li className="breadcrumb-item">
                            <Link to={`/cluster/${routeParams.clusterId}`}
                                  className={"link-body-emphasis text-decoration-none"}>
                                Cluster
                            </Link>
                        </li>
                        <li className="breadcrumb-item">
                            <Link to={`/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}`}
                                  className={"link-body-emphasis text-decoration-none"}>
                                {routeParams.keyspaceName}
                            </Link>
                        </li>
                        <li className="breadcrumb-item active" aria-current="page">
                            {routeParams.tableName}
                        </li>
                    </ol>
                </nav>
            </div>

            <div
                className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h2 className="h2">
                    {routeParams.tableName || 'Table'}
                    {
                        queryLoading &&
                        <div className="ms-2 spinner-border text-primary" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </div>
                    }
                </h2>
                <div className="btn-toolbar mb-2 mb-md-0">
                    <button type="button" className="btn btn-sm btn-outline-secondary me-2"
                            onClick={() => {
                                setTableName(routeParams.tableName);
                                setShowDetail(true);
                            }}>
                        Detail
                    </button>

                    <div className="btn-group me-2">
                        <button type="button" className="btn btn-sm btn-outline-secondary"
                                onClick={() => {
                                    setShowExport(true);
                                }}
                        >Export
                        </button>
                        <button type="button" className="btn btn-sm btn-outline-secondary"
                                onClick={() => {
                                    setShowImport(true);
                                }}
                        >Import
                        </button>
                    </div>

                    <button type="button"
                            className="btn btn-sm btn-outline-primary d-flex align-items-center gap-1"
                            onClick={() => {
                                setShowDataManage(true);
                            }}>
                        New Line
                    </button>
                </div>
            </div>

            {/*TODO : 위치 변경*/}

            <div className="table-responsive small">
                <table className="table table-sm table-fixed table-lock-height table-hover">
                    <thead>
                    <tr className={"table-dark"}>
                        <th className={"text-center"}
                            scope="col">#
                        </th>
                        {
                            queryResult.columnNames.map((info, infoIndex) => {
                                return (
                                    <th className={"text-center"} key={`resultHeader${infoIndex}`}
                                        scope="col">{info}</th>
                                )
                            })
                        }
                    </tr>
                    </thead>
                    <tbody className="table-group-divider" style={{maxHeight: "100vh"}}>
                    {
                        queryResult.rows.length <= 0 ? <>
                                <tr>
                                    <td className={"text-center"} colSpan={queryResult.columnNames.length + 1}>
                                        데이터가 없습니다.
                                    </td>
                                </tr>
                            </> :
                            queryResult.rows.map((row, rowIndex) => {
                                return (
                                    <tr key={`resultBody${rowIndex}`}>
                                        <td className={"text-center"}>
                                            <div className="btn-group btn-group-sm">
                                                <button type="button" className={"btn btn-sm btn-outline-primary"}
                                                        onClick={() => openToast("복사")}>
                                                    <i className="bi bi-clipboard2"></i>
                                                </button>
                                                <button type="button" className={"btn btn-sm btn-outline-primary"}
                                                        onClick={() => openToast("수정")}>
                                                    <i className="bi bi-pencil-square"></i>
                                                </button>
                                                <button type="button" className={"btn btn-sm btn-outline-danger"}
                                                        onClick={() => openToast("삭제")}>
                                                    <i className="bi bi-trash3-fill"></i>
                                                </button>
                                            </div>
                                        </td>
                                        {
                                            queryResult.columnNames.map((info, infoIndex) => {
                                                return (
                                                    <td className={"text-center text-break"}
                                                        key={`resultItem${infoIndex}`}>
                                                        {
                                                            CassdioUtils.renderData(row[info])
                                                        }
                                                    </td>
                                                )
                                            })
                                        }
                                    </tr>
                                )
                            })
                    }

                    </tbody>
                </table>
            </div>

            {
                nextCursor &&
                <div className="d-grid gap-2 col-6 mx-auto">
                    <button className="btn btn-outline-secondary" type="button"
                            onClick={() => getList(nextCursor)}>More

                    </button>
                </div>
            }

            <TableDetailModal
                show={showDetail}
                clusterId={routeParams.clusterId}
                keyspaceName={routeParams.keyspaceName}
                tableName={tableName}
                handleClose={() => setShowDetail(false)}
            />

            <TableDataManageModal
                show={showDataManage}
                handleClose={() => setShowDataManage(false)}
            />

            <TableExportModal
                show={showExport}
                handleClose={() => setShowExport(false)}
            />

            <TableImportModal
                show={showImport}
                handleClose={() => setShowImport(false)}
            />

        </>
    )
}

export default TableHome;
