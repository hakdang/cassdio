import {CassdioUtils} from "../../../../../../utils/cassdioUtils";

const TableDetailModalInfo = (props) => {

    const detail = props.detail;

    return (
        <div className="table-responsive small">
            <table
                className="table table-sm table-hover">
                <tbody>

                {
                    detail.columns.map((info, infoIndex) => {
                        return (
                            <tr key={`resultBody${infoIndex}`}>
                                <th className={"text-center text-break"}>
                                    {info.columnName}
                                </th>
                                <td className={"text-center text-break"}
                                    key={`resultItem${infoIndex}`}>
                                    {
                                        CassdioUtils.renderData(detail.row[info.columnName])
                                    }
                                </td>
                            </tr>
                        )
                    })
                }
                </tbody>
            </table>
        </div>
    )
}

export default TableDetailModalInfo;
