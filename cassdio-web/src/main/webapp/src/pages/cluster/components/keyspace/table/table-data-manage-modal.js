import {useEffect, useState} from "react";
import {Modal} from "react-bootstrap";
import useCassdio from "commons/hooks/useCassdio";
import axios from "axios";

const TableDataManageModal = (props) => {

    const show = props.show;
    const handleClose = props.handleClose;

    const clusterId = props.clusterId;
    const keyspaceName = props.keyspaceName;
    const tableName = props.tableName;

    const {errorCatch} = useCassdio();
    const [columnList, setColumnList] = useState({
        rows: [],
        columnHeader: [],
    })

    const [columnListLoading, setColumnListLoading] = useState(false);

    const kindSort = {
        "partition_key": 0,
        "clustering": 1,
    }

    function ascSort(a, b, compositeSort = undefined) {
        if (a > b) {
            return compositeSort ? compositeSort : 1;
        }

        if (a < b) {
            return compositeSort ? compositeSort : -1;
        }

        return compositeSort ? compositeSort : 0;
    }

    function descSort(a, b, compositeSort = undefined) {
        if (a > b) {
            return -1;
        }

        if (a < b) {
            return 1;
        }

        return 0;
    }

    const [dataManage, setDataManage] = useState({});

    const changeHandler = e => {
        // Deriving the filter that a checkbox is associated too, and getting its value on change
        const property = e.target.name;
        const val = e.target.value;
        setDataManage(
            prevState => {
                const filters = prevState || {};
                filters[property] = val;
                return {
                    ...filters,
                };
            }
        );
    };

    function deMakeInsertQuery() {

        axios({
            method: "POST",
            url: `/api/cassandra/cluster/${clusterId}/keyspace/${keyspaceName}/table/${tableName}/row/insert/query`,
            //해당 API 에는 column_name, kind, type 이 필수값이어야만 함.(TODO : testcase 추가 필요)
            data: dataManage
        }).then((response) => {
            console.log("response : ", response.data.result);

        }).catch((error) => {
            errorCatch(error)
        }).finally(() => {
        });

    }

    useEffect(() => {
        //show component
        setColumnListLoading(true);

        //수정처리일 경우 단건조회 형태로 조회하고, withColumnList 로 하여 컬럼정보를 가져올 수 있도록 한다.

        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${clusterId}/keyspace/${keyspaceName}/table/${tableName}/column`,
            //해당 API 에는 column_name, kind, type 이 필수값이어야만 함.(TODO : testcase 추가 필요)
            params: {}
        }).then((response) => {
            console.log("response : ", response.data.result);

            //TODO : 정렬 고도화
            const tempRows = response.data.result.columnList.rows;
            tempRows.sort(function (a, b, compositeSort) {
                const aKindSort = kindSort[a.kind] || 2;
                const bKindSort = kindSort[b.kind] || 2;

                console.log("kind", aKindSort, bKindSort, a, b)

                return descSort(aKindSort, bKindSort, descSort(a.clustering_order, b.clustering_order, undefined))
            });

            setColumnList({
                rows: tempRows,
                columnHeader: response.data.result.columnList.columnHeader,
            })
        }).catch((error) => {
            errorCatch(error)
        }).finally(() => {
            setColumnListLoading(false)
        });

        return () => {
            //hide component

        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return (
        <>
            <Modal show={show} size={"xl"} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Table Data Manage(New or Modify)</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {
                        columnListLoading ? <>
                            <div className="d-flex justify-content-center">
                                <div className="spinner-border" role="status">
                                    <span className="visually-hidden">Loading...</span>
                                </div>
                            </div>
                        </> : <>

                            {/*//TODO : 개발 중*/}
                            <table className={"table table-sm"}>
                                <tbody>
                                <tr>
                                    <th colSpan={4}>TTL</th>
                                </tr>
                                <tr>
                                    <th colSpan={4}>Timestamp</th>
                                </tr>
                                <tr>
                                    <th colSpan={4}>IfExist</th>
                                </tr>
                                {
                                    columnList.rows.map((row, rowIndex) => {
                                        return (
                                            <tr key={rowIndex}>
                                                {
                                                    <>
                                                        <th>
                                                            {row['column_name']}
                                                        </th>
                                                        <th>
                                                            {row['kind']}
                                                        </th>
                                                        <th>
                                                            {row['type']}
                                                        </th>
                                                        <td>
                                                            {/* TODO : type 이 int 면 number, 그외 것들은 text 로 처리?*/}


                                                            <input type={"text"}
                                                                   className={"form-control form-control-sm"}
                                                                   name={`${row['column_name']}`}
                                                                   value={dataManage[row['column_name']] || ''}
                                                                   onChange={changeHandler}
                                                            />
                                                        </td>
                                                    </>
                                                }
                                            </tr>
                                        )
                                    })
                                }
                                </tbody>
                            </table>

                        </>

                    }


                </Modal.Body>
                <Modal.Footer>
                    <button className={"btn btn-sm btn-outline-primary"} onClick={e => {
                        deMakeInsertQuery();
                        console.log("dataManage ", dataManage)
                    }}>
                        Generate Query
                    </button>
                    <button className={"btn btn-sm btn-outline-secondary"} onClick={handleClose}>
                        Close
                    </button>
                </Modal.Footer>
            </Modal>
        </>
    )
}

export default TableDataManageModal;
