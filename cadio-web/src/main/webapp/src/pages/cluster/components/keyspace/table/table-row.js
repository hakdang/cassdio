import axios from "axios";
import {useEffect, useState} from "react";
import {axiosCatch} from "../../../../../utils/axiosUtils";
import {useParams} from "react-router-dom";

const TableRow = () => {
    const routeParams = useParams();

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
            axiosCatch(error);
        }).finally(() => {
            setQueryLoading(false);
        })
    }

    const renderData = (data) => {
        if (typeof data === "object") {
            return JSON.stringify(data);
        }

        return data;
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
        <div className={"row mt-3"}>
            <div className={"col"}>
                <h5 className={"h5"}>
                    Rows

                    {
                        queryLoading &&
                        <div className="ms-2 spinner-border spinner-border-sm" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </div>
                    }
                </h5>

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
                                                            onClick={() => alert("복사")}>
                                                        <i className="bi bi-clipboard2"></i>
                                                    </button>
                                                    <button type="button" className={"btn btn-sm btn-outline-primary"}
                                                            onClick={() => alert("수정")}>
                                                        <i className="bi bi-pencil-square"></i>
                                                    </button>
                                                    <button type="button" className={"btn btn-sm btn-outline-danger"}
                                                            onClick={() => alert("삭제")}>
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
                                                                renderData(row[info])
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
            </div>

        </div>
    )
}

export default TableRow;
