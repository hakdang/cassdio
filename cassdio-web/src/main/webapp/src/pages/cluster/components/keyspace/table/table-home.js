import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import TableDetailModal from "./table-detail-modal";
import TableDataManageModal from "./table-data-manage-modal";
import TableExportModal from "./table-export-modal";
import TableImportModal from "./table-import-modal";
import {OverlayTrigger, Tooltip} from "react-bootstrap";
import DataRowItem from "../../data-row-item";
import ClusterBreadcrumb from "pages/cluster/components/cluster-breadcrumb";
import useTable from "pages/cluster/hooks/useTable";
import TableRowDetailModal from "./detail-modal/table-row-detail-modal";
import {CassdioUtils} from "../../../../../utils/cassdioUtils";

const TableHome = (props) => {

    const {
        doTableTruncate,
        doTableDrop,
        doGetTableList,
        queryLoading,
        queryResult,
        nextCursor,
    } = useTable();

    const routeParams = useParams();
    const [showDetail, setShowDetail] = useState(false);
    const [showExport, setShowExport] = useState(false);
    const [showImport, setShowImport] = useState(false);
    const [showDataManage, setShowDataManage] = useState(false);
    const [showRowDetail, setShowRowDetail] = useState(false);
    const [tableName, setTableName] = useState('');
    const [moreQueryLoading, setMoreQueryLoading] = useState(false)

    useEffect(() => {
        //show component
        setTableName(routeParams.tableName);

        doGetTableList(null)

        return () => {
            //hide component
            setTableName('');
        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [tableName]);

    return (
        <>
            <ClusterBreadcrumb
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

            {
                CassdioUtils.columnListSorting(queryResult.columnList.rows)
            }

            {/*TODO : 위치 변경*/}
            <div className="table-responsive small">
                <table className="table table-sm table-fixed table-lock-height table-hover">
                    <thead>
                    <tr className={"table-dark"}>
                        <th className={"text-center"} scope="col">#</th>
                        {
                            queryResult.rowHeader.map((info, infoIndex) => {
                                return (
                                    <th className={"text-center text-truncate"} key={`resultHeader${infoIndex}`}
                                        scope="col">
                                        <OverlayTrigger placement="bottom" overlay={
                                            <Tooltip id="tooltip">
                                                {info.columnName} <br/>
                                                (type : {info.type})
                                            </Tooltip>
                                        }>
                                            <span style={{cursor: "pointer"}}>{info.columnName}</span>
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
                                    <td className={"text-center"} colSpan={queryResult.rowHeader.length + 1}>
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
                                                        onClick={() => setShowRowDetail(true)}>
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
                                            queryResult.rowHeader.map((info, infoIndex) => {
                                                return (
                                                    <td className={"text-center text-break text-truncate"}
                                                        key={`resultItem${infoIndex}`}>
                                                        <DataRowItem data={row[info.columnName]}/>
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
                                onClick={() => doGetTableList(nextCursor, setMoreQueryLoading)}>
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
                showDataManage && <TableDataManageModal
                    show={showDataManage}
                    clusterId={routeParams.clusterId}
                    keyspaceName={routeParams.keyspaceName}
                    tableName={tableName}
                    handleClose={() => setShowDataManage(false)}
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
                showRowDetail && <TableRowDetailModal
                    show={showRowDetail}
                    handleClose={() => setShowRowDetail(false)}
                />
            }
        </>
    )
}

export default TableHome;
