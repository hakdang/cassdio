import {useEffect, useState} from "react";

const QueryResult = (props) => {
    const queryExecute = props.queryExecute;
    const result = props.result;

    const query = props.query;
    const nextCursor = props.nextCursor;

    const [moreQueryLoading, setMoreQueryLoading] = useState(false);

    useEffect(() => {
        //show component

        return () => {
            //hide component

        };
    }, []);

    return (
        <>
            {
                result.wasApplied && <>
                    <h4 className={"h4 mt-3"}>Result</h4>

                    {/*<p>wasApplied : {result.wasApplied}</p>*/}

                    <code className={"mb-3"}>
                        {query}
                    </code>

                    <div className="table-responsive small">
                        <table className="table table-sm table-fixed table-lock-height table-hover">
                            <thead>
                            <tr className={"table-dark"}>
                                {
                                    result.columnNames.map((info, infoIndex) => {
                                        return (
                                            <th className={"text-center"} key={`resultHeader${infoIndex}`}
                                                scope="col">{info}</th>
                                        )
                                    })
                                }
                            </tr>
                            </thead>
                            <tbody className="table-group-divider" style={{maxHeight: "50vh"}}>
                            {
                                result.rows.length <= 0 ? <>
                                        <tr>
                                            <td className={"text-center"} colSpan={result.columnNames.length}>데이터가 없습니다.
                                            </td>
                                        </tr>
                                    </> :
                                    result.rows.map((row, rowIndex) => {
                                        return (
                                            <tr key={`resultBody${rowIndex}`}>
                                                {
                                                    result.columnNames.map((info, infoIndex) => {
                                                        return (
                                                            <td className={"text-center"}
                                                                key={`resultItem${infoIndex}`}>{row[info]}</td>
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

                    {
                        nextCursor &&
                        <div className="d-grid gap-2 col-6 mx-auto">
                            <button className="btn btn-outline-secondary" type="button"
                                    onClick={() => queryExecute(query, nextCursor, setMoreQueryLoading)}>More

                                {
                                    moreQueryLoading &&
                                    <div className="ms-2 spinner-border spinner-border-sm" role="status">
                                        <span className="visually-hidden">Loading...</span>
                                    </div>
                                }
                            </button>
                        </div>
                    }
                </>
            }
        </>
    )
}

export default QueryResult;
