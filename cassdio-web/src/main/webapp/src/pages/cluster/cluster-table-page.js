import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import {OverlayTrigger, Tooltip} from "react-bootstrap";

import useTable from "hooks/useTable";

import ClusterKeyspaceBreadcrumb from "components/cluster/cluster-keyspace-breadcrumb";

import DataRowItem from "components/cluster/data-row-item";
import TableDetailModal from "components/cluster/modal/table-detail-modal";
import TableExportModal from "components/cluster/modal/table-export-modal";
import TableImportModal from "components/cluster/modal/table-import-modal";
import TableRowDetailModal from "components/cluster/modal/table-row-detail-modal";

const ClusterTablePage = () => {

    const {
        doTableTruncate,
        doTableDrop,
        doGetTableRows,
        queryLoading,
        queryResult,
        nextCursor,
    } = useTable();

    const routeParams = useParams();
    const [showDetail, setShowDetail] = useState(false);
    const [showExport, setShowExport] = useState(false);
    const [showImport, setShowImport] = useState(false);
    const [showRowDetail, setShowRowDetail] = useState(false);
    const [tableName, setTableName] = useState('');
    const [moreQueryLoading, setMoreQueryLoading] = useState(false)
    const [rowDetailView, setRowDetailView] = useState(null);

    useEffect(() => {
        //show component
        setTableName(routeParams.tableName);

        doGetTableRows(null)

        return () => {
            //hide component
            setTableName('');
        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [tableName]);

    return (
        <>
            <ClusterKeyspaceBreadcrumb
                clusterId={routeParams.clusterId}
                keyspaceName={routeParams.keyspaceName}
                tableName={routeParams.tableName}
                active={"TABLE"}
            />

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
                                setShowDetail(true);
                            }}>
                        Detail
                    </button>

                    {/*<div className="btn-group me-2">*/}
                    {/*    <button type="button" className="btn btn-sm btn-outline-secondary"*/}
                    {/*            onClick={() => {*/}

                    {/*                setShowExport(true);*/}
                    {/*            }}*/}
                    {/*    >Export*/}
                    {/*    </button>*/}
                    {/*    <button type="button" className="btn btn-sm btn-outline-secondary"*/}
                    {/*            onClick={() => {*/}
                    {/*                setShowImport(true);*/}
                    {/*            }}*/}
                    {/*    >Import*/}
                    {/*    </button>*/}
                    {/*</div>*/}

                    {/* MAP, Set 등 다 구현하려면 어려움 추후 대응 필요해보임*/}
                    {/*<button type="button"*/}
                    {/*        className="btn btn-sm btn-outline-primary d-flex align-items-center gap-1"*/}
                    {/*        onClick={() => {*/}
                    {/*            setShowDataManage(true);*/}
                    {/*        }}>*/}
                    {/*    New Line*/}
                    {/*</button>*/}

                    <div className="dropdown">
                        <button className="btn btn-outline-danger btn-sm dropdown-toggle" type="button"
                                data-bs-toggle="dropdown"
                                aria-expanded="false">
                            <i className="bi bi-three-dots"></i>
                        </button>
                        <ul className="dropdown-menu">
                            <li><a className="dropdown-item" role={"button"} onClick={doTableTruncate}>Truncate</a></li>
                            <li><a className="dropdown-item" role={"button"} onClick={doTableDrop}>Drop</a></li>
                        </ul>
                    </div>
                </div>
            </div>

            {/*TODO : 위치 변경*/}
            <div className="table-responsive small">
                <table className="table table-sm table-fixed table-lock-height table-hover">
                    <thead>
                    <tr className={"table-dark"}>
                        <th className={"text-center"} scope="col">#</th>
                        {
                            queryResult.convertedRowHeader.map((info, infoIndex) => {
                                return (
                                    <th className={"text-center text-truncate"} key={`resultHeader${infoIndex}`}
                                        scope="col">
                                        <OverlayTrigger placement="bottom" overlay={
                                            <Tooltip id="tooltip">
                                                {info.column_name} ({info.type})
                                            </Tooltip>
                                        }>
                                                <span style={{cursor: "pointer"}}>
                                                     {
                                                         info.kind === 'partition_key' &&
                                                         <i className="bi bi-p-square me-1"></i>
                                                     }
                                                    {
                                                        info.kind === 'clustering' &&
                                                        <i className="bi bi-c-square me-1"></i>
                                                    }
                                                    {info.column_name}
                                                </span>
                                        </OverlayTrigger>
                                    </th>
                                )
                            })
                        }
                    </tr>
                    </thead>
                    <tbody className="table-group-divider" style={{maxHeight: "100vh"}}>
                    {
                        queryResult.rows.length <= 0 ? <>
                                <tr>
                                    <td className={"text-center"} colSpan={queryResult.convertedRowHeader.length + 1}>
                                        No Data
                                    </td>
                                </tr>
                            </> :
                            queryResult.rows.map((row, rowIndex) => {
                                return (
                                    <tr key={`resultBody${rowIndex}`}>
                                        <td className={"text-center"}>
                                            <div className="btn-group btn-group-sm">
                                                <button type="button" className={"btn btn-sm btn-outline-primary"}
                                                        onClick={() => {
                                                            setRowDetailView(row);
                                                            setShowRowDetail(true);
                                                        }}>
                                                    <i className="bi bi-book"></i>
                                                </button>
                                                {/*UI 구성 등 오래걸릴 듯*/}
                                                {/*<button type="button" className={"btn btn-sm btn-outline-primary"}*/}
                                                {/*        onClick={() => openToast("수정")}>*/}
                                                {/*    <i className="bi bi-pencil-square"></i>*/}
                                                {/*</button>*/}
                                                {/*<button type="button" className={"btn btn-sm btn-outline-danger"}*/}
                                                {/*        onClick={() => toast.info("삭제")}>*/}
                                                {/*    <i className="bi bi-trash3-fill"></i>*/}
                                                {/*</button>*/}
                                            </div>
                                        </td>
                                        {
                                            queryResult.convertedRowHeader.map((info, infoIndex) => {
                                                return (
                                                    <td className={"text-center text-break text-truncate"}
                                                        key={`resultItem${infoIndex}`}>
                                                        <DataRowItem data={row[info.column_name]}/>
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
                <div className={"row"}>
                    <div className="d-grid gap-2 col-6 mx-auto">
                        <button className="btn btn-outline-secondary" type="button"
                                onClick={() => doGetTableRows(nextCursor, setMoreQueryLoading)}>
                            More

                            {
                                moreQueryLoading &&
                                <div className="ms-2 spinner-border spinner-border-sm" role="status">
                                    <span className="visually-hidden">Loading...</span>
                                </div>
                            }
                        </button>
                    </div>
                </div>
            }

            {
                showDetail && <TableDetailModal
                    show={showDetail}
                    clusterId={routeParams.clusterId}
                    keyspaceName={routeParams.keyspaceName}
                    tableName={tableName}
                    handleClose={() => setShowDetail(false)}
                />
            }

            {
                showExport && <TableExportModal
                    show={showExport}
                    handleClose={() => setShowExport(false)}
                />
            }

            {
                showImport && <TableImportModal
                    show={showImport}
                    handleClose={() => setShowImport(false)}
                />
            }
            {
                showRowDetail && rowDetailView && <TableRowDetailModal
                    show={showRowDetail}
                    rowDetailView={rowDetailView}
                    convertedRowHeader={queryResult.convertedRowHeader}
                    handleClose={() => setShowRowDetail(false)}
                />
            }
        </>
    )
}

export default ClusterTablePage;
