import {Link} from "react-router-dom";

const KeyspaceTableList = ({clusterId, keyspace, tableList}) => {
    return (
        <ol className="list-group ">
            {tableList.map((info, infoIndex) => (
                <li key={infoIndex} className="list-group-item d-flex justify-content-between align-items-start">
                    <div className="ms-2 me-auto">
                        <div className="fw-bold">{info.table_name}</div>
                        {info.comment}
                    </div>
                    <div className={"btn-group btn-group-sm"}>
                        <Link
                            className={"btn btn-sm btn-outline-primary"}
                            to={`/cluster/${clusterId}/keyspace/${keyspace}/table/${info.table_name}`}>Detail</Link>
                    </div>
                    {/*{JSON.stringify(info)}*/}
                </li>
            ))}
        </ol>
    )
}

export default KeyspaceTableList;
