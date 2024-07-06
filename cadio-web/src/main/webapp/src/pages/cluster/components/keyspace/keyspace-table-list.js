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
                {tableList.map((info, infoIndex) => (
                    <li key={infoIndex} className="list-group-item d-flex justify-content-between align-items-start">
                        <div className="ms-2 me-auto">
                            <div className="fw-bold">{info.table_name}</div>
                            {info.comment}
                        </div>
                        <div className={"btn-group btn-group-sm"}>
                            <button className={"btn btn-sm btn-outline-primary"}
                            onClick={() => {
                                setTableName(info.table_name);
                                setShowDetail(true);
                            }}>
                                Detail
                            </button>
                            <Link
                                className={"btn btn-sm btn-outline-primary"}
                                to={`/cluster/${clusterId}/keyspace/${keyspaceName}/table/${info.table_name}`}>Rows</Link>
                        </div>
                    </li>
                ))}
            </ol>
        </>
    )
}

export default KeyspaceTableList;
