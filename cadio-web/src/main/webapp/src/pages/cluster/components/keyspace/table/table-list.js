import {Link} from "react-router-dom";

const TableList = ({clusterId, keyspace, tableList}) => {
    return (
        <>
            <div className="table-responsive small">
                <table className="table table-striped table-sm">
                    <thead>
                    <tr>
                        <th className={"text-center"} scope="col">Name</th>
                        <th className={"text-center"} scope="col">Comment</th>
                        <th className={"text-center"} scope="col">Options</th>
                    </tr>
                    </thead>
                    <tbody>
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
        </>
    )
}

export default TableList;
