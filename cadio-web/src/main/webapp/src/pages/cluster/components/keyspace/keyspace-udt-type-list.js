const KeyspaceUDTTypeList = ({typeList}) => {
    return (
        <>
            <div className="table-responsive small">
                <table className="table table-sm table-hover">
                    <thead>
                    <tr className={"type-dark"}>
                        <th className={"text-center"} scope="col">Name</th>
                        <th className={"text-center"} scope="col">Field & Data Type</th>
                    </tr>
                    </thead>
                    <tbody className="table-group-divider" style={{maxHeight: "50vh"}}>
                    {typeList.map((type) => (
                        <tr key={type.typeName}>
                            <th className="text-center">
                                {type.typeName}
                            </th>
                            <td className="text-center">
                                {JSON.stringify(type.columns)}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </>
    )
}

export default KeyspaceUDTTypeList;
