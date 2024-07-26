import {Link} from "react-router-dom";
import TableDetailModal from "./modal/table-detail-modal";
import {useEffect, useState} from "react";
import axios from "axios";
import useCassdio from "../../hooks/useCassdio";

const KeyspaceTableList = ({clusterId, keyspaceName, tableList}) => {
    const [moreLoading, setMoreLoading] = useState(false);
    const [showDetail, setShowDetail] = useState(false);
    const [tableName, setTableName] = useState('');

    const [rows, setRows] = useState(tableList.rows || []);
    const [nextCursor, setNextCursor] = useState(tableList.nextCursor);

    const {errorCatch} = useCassdio();

    const moreList = (cursor) => {
        setMoreLoading(true)
        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${clusterId}/keyspace/${keyspaceName}/table`,
            params: {
                cursor: cursor
            }
        }).then((response) => {
            setRows([...rows, ...response.data.result.tableList.rows]);
            setNextCursor(response.data.result.tableList.nextCursor);

        }).catch((error) => {
            errorCatch(error)
        }).finally(() => {
            setMoreLoading(false)
        });
    }

    useEffect(() => {
        //show component

        return () => {
            //hide component

        };
    }, []);

    return (
        <div className={"overflow-y-auto"} style={{maxHeight:"350px"}}>
            <ul className="list-group ">
                {
                    rows && rows.length <= 0 ? <>
                        <li
                            className="list-group-item d-flex justify-content-between align-items-start">
                            No Data
                        </li>
                    </> : <>
                        {
                            rows.map((info, infoIndex) => (
                                <li key={infoIndex}
                                    className="list-group-item d-flex justify-content-between align-items-start">
                                    <div className="ms-2 me-auto">
                                        <div className="fw-bold">
                                            <Link
                                                className={"link-body-emphasis text-decoration-none"}
                                                to={`/cluster/${clusterId}/keyspace/${keyspaceName}/table/${info.table_name}`}>
                                                {info.table_name}
                                            </Link>
                                        </div>
                                        {info.comment}
                                    </div>
                                    <div className={"btn-group btn-group-sm"}>
                                        <a className={"btn btn-sm btn-outline-primary"} role={"button"}
                                           onClick={() => {
                                               setTableName(info.table_name);
                                               setShowDetail(true);
                                           }}>
                                            Detail
                                        </a>
                                    </div>
                                </li>
                            ))}
                    </>
                }
            </ul>

            {
                nextCursor &&
                <div className={"row mt-3"}>
                    <div className="d-grid gap-2 col-8 mx-auto">
                        <button className="btn btn-outline-secondary" type="button"
                                onClick={() => moreList(nextCursor)}>
                            More
                            {
                                moreLoading &&
                                <div className="ms-2 spinner-border spinner-border-sm" role="status">
                                    <span className="visually-hidden">Loading...</span>
                                </div>
                            }

                        </button>
                    </div>
                </div>
            }

            <TableDetailModal
                show={showDetail}
                clusterId={clusterId}
                keyspaceName={keyspaceName}
                tableName={tableName}
                handleClose={() => setShowDetail(false)}
            />
        </div>
    )
}

export default KeyspaceTableList;
