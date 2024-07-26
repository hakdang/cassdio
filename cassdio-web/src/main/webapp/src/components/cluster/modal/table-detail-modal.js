import {useEffect, useState} from "react";
import axios from "axios";
import {Modal} from "react-bootstrap";
import useCassdio from "hooks/useCassdio";
import TableDetailModalInfo from "./detail/table-detail-modal-info";
import TableDetailModalDescribe from "./detail/table-detail-modal-describe";
import TableDetailModalColumnList from "./detail/table-detail-modal-column-list";
import {CassdioUtils} from "../../../utils/cassdioUtils";

const TableDetailModal = (props) => {

    const show = props.show;
    const handleClose = props.handleClose;

    const clusterId = props.clusterId;
    const keyspaceName = props.keyspaceName;
    const tableName = props.tableName;
    const {errorCatch} = useCassdio();
    const [tableLoading, setTableLoading] = useState(false);
    const [tableInfo, setTableInfo] = useState({
        detail: {
            row: {},
            rowHeader: [],
        },
        describe: '',
        columnList: {
            rows: [],
            rowHeader: [],
        },
    })

    useEffect(() => {
        //show component

        if (!tableName) {
            return;
        }
        setTableLoading(true);

        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${clusterId}/keyspace/${keyspaceName}/table/${tableName}`,
            params: {
                withTableDescribe: true,
            }
        }).then((response) => {
            console.log("detail : ", response);

            const sortedColumnList = CassdioUtils.columnListSorting(
                response.data.result.columnList
            );

            setTableInfo({
                detail: response.data.result.detail,
                describe: response.data.result.describe,
                columnList: sortedColumnList,
            })

        }).catch((error) => {
            errorCatch(error)
        }).finally(() => {
            setTableLoading(false)
        });
        return () => {
            //hide component

        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [tableName]);

    return (
        <Modal show={show} size={"xl"} onHide={handleClose}>
            <Modal.Header closeButton>
                <Modal.Title>Table Detail</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {
                    tableLoading ? <>
                        <div className="d-flex justify-content-center">
                            <div className="spinner-border" role="status">
                                <span className="visually-hidden">Loading...</span>
                            </div>
                        </div>
                    </> : <>
                        <div className="">
                            <nav>
                                <div className="nav nav-tabs mb-3" id="nav-tab" role="tablist">
                                    <button className="nav-link active" id="nav-info-tab"
                                            data-bs-toggle="tab" data-bs-target="#nav-info" type="button" role="tab"
                                            aria-controls="nav-info" aria-selected="true">
                                        Information
                                    </button>
                                    {
                                        tableInfo.describe &&
                                        <button className="nav-link" id="nav-descirbe-tab"
                                                data-bs-toggle="tab"
                                                data-bs-target="#nav-descirbe"
                                                type="button" role="tab"
                                                aria-controls="nav-descirbe"
                                                aria-selected="false">
                                            Describe
                                        </button>
                                    }

                                    <button className="nav-link" id="nav-columns-tab"
                                            data-bs-toggle="tab" data-bs-target="#nav-columns" type="button" role="tab"
                                            aria-controls="nav-columns" aria-selected="false">
                                        Columns
                                    </button>
                                </div>
                            </nav>
                            <div className="tab-content p-3 border" id="nav-tabContent">
                                <div className="tab-pane fade active show" id="nav-info" role="tabpanel"
                                     aria-labelledby="nav-info-tab">
                                    {
                                        tableInfo.detail && <TableDetailModalInfo detail={tableInfo.detail}/>
                                    }
                                </div>
                                <div className="tab-pane fade" id="nav-descirbe" role="tabpanel"
                                     aria-labelledby="nav-descirbe-tab">
                                    {
                                        tableInfo.describe && <TableDetailModalDescribe describe={tableInfo.describe}/>
                                    }
                                </div>
                                <div className="tab-pane fade" id="nav-columns" role="tabpanel"
                                     aria-labelledby="nav-columns-tab">

                                    {
                                        tableInfo.columnList && tableInfo.columnList.rows.length > 0 &&
                                        <TableDetailModalColumnList columnList={tableInfo.columnList}/>
                                    }
                                </div>
                            </div>
                        </div>
                    </>
                }
            </Modal.Body>
            <Modal.Footer>
                <button className={"btn btn-sm btn-outline-secondary"} onClick={handleClose}>
                    Close
                </button>
            </Modal.Footer>
        </Modal>
    )
}

export default TableDetailModal;
