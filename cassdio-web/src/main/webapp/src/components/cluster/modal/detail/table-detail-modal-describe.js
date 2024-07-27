const TableDetailModalDescribe = (props) => {
    const describe = props.describe;

    return (
        <div className={"row"}>
            <div className={"col"}>
                <pre className={"w-100 text-break"} >
                        {describe}
                </pre>
            </div>
        </div>
    )
}

export default TableDetailModalDescribe;
