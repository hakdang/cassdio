import {Link} from "react-router-dom";
import {Spinner} from "react-bootstrap";

const KeyspaceTableList = ({clusterId, keyspace, tableList, tableLoading}) => {
    return (
        <div className="table-responsive small">
            <table className="table table-sm table-hover">
                <thead>
                <tr className={"table-dark"}>
                    <th className={"text-center"} scope="col">Name</th>
                    <th className={"text-center"} scope="col">Comment</th>
                    <th className={"text-center"} scope="col">Options</th>
                </tr>
                </thead>
                <tbody className="table-group-divider" style={{maxHeight: "50vh"}}>
                {tableList.map((table) => (
                    <tr key={table.id}>
                        <th className="text-center">
                            <Link
                                to={`/cluster/${clusterId}/keyspace/${keyspace}/table/${table.tableName}`}>{table.tableName}</Link>
                        </th>
                        <td className="text-center">
                            {table.comment ? table.comment : '-'}
                        </td>
                        <td className="text-center">
                            {JSON.stringify(table.options)}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    )
}

export default KeyspaceTableList;
