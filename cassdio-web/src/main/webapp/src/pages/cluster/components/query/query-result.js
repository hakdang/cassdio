import {useEffect, useState} from "react";
import DataRowItem from "../data-row-item";
import {OverlayTrigger, Tooltip} from "react-bootstrap";

const QueryResult = ({queryExecute, result, query, nextCursor, setShowQueryTrace}) => {
    //const queryOptions = props.queryOptions;

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
                result && result.wasApplied && <>

                    <div
                        className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 border-bottom">
                        <h4 className="h4">
                            Result
                        </h4>

                        <div className="btn-toolbar mb-2 mb-md-0">
                            {
                                result.queryTrace &&
                                <div className="btn-group me-2">
                                    <button
                                        type="button"
                                        className="btn btn-sm btn-outline-secondary"
                                        onClick={e => {
                                            setShowQueryTrace(true);
                                        }}
                                    >
                                        Trace
                                    </button>
                                </div>
                            }
                        </div>
                    </div>

                    <code className={"mb-5 mt-1"}>
                        {query}
                    </code>

                    <div className="table-responsive small">
                        <table className="table table-sm table-fixed table-lock-height table-hover">
                            <thead>
                            <tr className={"table-dark"}>
                                {
                                    result.rowHeader.map((info, infoIndex) => {
                                        return (
                                            <th className={"text-center text-truncate"}
                                                key={`resultHeader${infoIndex}`}
                                                scope="col">

                                                <OverlayTrigger placement="bottom" overlay={
                                                    <Tooltip id="tooltip">
                                                        {info.columnName}<br/>
                                                        ({info.type})
                                                    </Tooltip>
                                                }>
                                                    <span style={{cursor: "pointer"}}>{info.columnName}</span>
                                                </OverlayTrigger>
                                            </th>
                                        )
                                    })
                                }
                            </tr>
                            </thead>
                            <tbody className="table-group-divider" style={{maxHeight: "50vh"}}>
                            {
                                result.rows.length <= 0 ? <>
                                        <tr>
                                            <td className={"text-center"} colSpan={result.rowHeader.length}>
                                                No Data
                                            </td>
                                        </tr>
                                    </> :
                                    result.rows.map((row, rowIndex) => {
                                        return (
                                            <tr key={`resultBody${rowIndex}`}>
                                                {
                                                    result.rowHeader.map((info, infoIndex) => {
                                                        return (
                                                            <td className={`text-center text-break text-truncate`}
                                                                key={`resultItem${infoIndex}`}>
                                                                <DataRowItem data={row[info.columnName]}/>
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

                    {
                        nextCursor &&
                        <div className={"row"}>
                            <div className="d-grid gap-2 col-6 mx-auto">
                                <button className="btn btn-outline-secondary" type="button"
                                        onClick={() => queryExecute(query, nextCursor, setMoreQueryLoading)}>
                                    More
                                    {
                                        moreQueryLoading &&
                                        <div className="ms-2 spinner-border spinner-border-sm" role="status">
                                            <span className="visually-hidden">Loading...</span>
                                        </div>
                                    }
                                </button>
                            </div>
                        </div>
                    }

                    <div className={"mb-5"}></div>
                </>
            }
        </>
    )
}

export default QueryResult;
