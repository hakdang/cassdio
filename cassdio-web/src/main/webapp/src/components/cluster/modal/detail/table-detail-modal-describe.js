const TableDetailModalDescribe = (props) => {
    const describe = props.describe;

    return (
        <div className={"row"}>
            <div className={"col"}>
                <code className={"w-100 text-break"} style={{whiteSpace: "pre"}}>
                    {describe}
                </code>
            </div>
        </div>
    )
}

export default TableDetailModalDescribe;
