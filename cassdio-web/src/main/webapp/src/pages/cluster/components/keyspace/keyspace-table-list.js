import {Link} from "react-router-dom";
import TableDetailModal from "./table/table-detail-modal";
import {useState} from "react";

const KeyspaceTableList = ({clusterId, keyspaceName, tableList}) => {
    const [showDetail, setShowDetail] = useState(false);
    const [tableName, setTableName] = useState('');

    return (
        <>

            <TableDetailModal
                show={showDetail}
                clusterId={clusterId}
                keyspaceName={keyspaceName}
                tableName={tableName}
                handleClose={() => setShowDetail(false)}
            />

            <ol className="list-group ">
                {
                    tableList && tableList.length <= 0 ? <>
                        <li
                            className="list-group-item d-flex justify-content-between align-items-start">
                            등록된 테이블이 없습니다.
                        </li>
                    </> : <>
                        {
                            tableList.map((info, infoIndex) => (
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
            </ol>
        </>
    )
}

export default KeyspaceTableList;
