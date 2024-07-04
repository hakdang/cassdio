const TableRow = () => {

    return (
        <div className={"row mt-3"}>
            <div className={"col"}>
                <h5 className={"h5"}>Rows</h5>

                <div className="table-responsive small">
                    <table className="table table-striped table-sm">
                        <thead>
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">Header</th>
                            <th scope="col">Header</th>
                            <th scope="col">Header</th>
                            <th scope="col">Header</th>
                        </tr>
                        </thead>
                        <tbody>
                        {
                            new Array(1000).fill({
                                t1: "test",
                                t2: "value",
                                t3: "tttt",
                                t4: "adfasdfasd"
                            }).map((info, infoIndex) => {
                                return (
                                    <tr key={infoIndex}>
                                        <td>{info.t1}</td>
                                        <td>{info.t2}</td>
                                        <td>{info.t3}</td>
                                        <td>{info.t4}</td>
                                        <td></td>
                                    </tr>
                                )
                            })
                        }

                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    )
}

export default TableRow;
