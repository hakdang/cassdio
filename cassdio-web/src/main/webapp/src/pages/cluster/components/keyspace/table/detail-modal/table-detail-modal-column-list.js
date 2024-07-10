import {CassdioUtils} from "../../../../../../utils/cassdioUtils";

const TableDetailModalColumnList = (props) => {
    const columnList = props.columnList;
    return (
        <div className="table-responsive small">
            <table className="table table-sm table-fixed table-lock-height table-hover">
                <thead>
                <tr className={"table-dark"}>
                    {
                        columnList.columns.map((info, infoIndex) => {
                            return (
                                <th className={"text-center"}
                                    key={`resultHeader${infoIndex}`} scope="col">
                                    {info.columnName} <br/><small>({info.type})</small>
                                </th>
                            )
                        })
                    }
                </tr>
                </thead>
                <tbody className="table-group-divider" style={{maxHeight: "100vh"}}>
                {
                    columnList.rows.length <= 0 ? <>
                            <tr>
                                <td className={"text-center"}
                                    colSpan={columnList.columns.length}>
                                    데이터가 없습니다.
                                </td>
                            </tr>
                        </> :
                        columnList.rows.map((row, rowIndex) => {
                            return (
                                <tr key={`resultBody${rowIndex}`}>
                                    {
                                        columnList.columns.map((info, infoIndex) => {
                                            return (
                                                <td className={"text-center text-break"}
                                                    key={`resultItem${infoIndex}`}>
                                                    {
                                                        CassdioUtils.renderData(row[info.columnName])
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
    )
}

export default TableDetailModalColumnList;
