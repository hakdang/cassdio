import {Link} from "react-router-dom";
import {useState} from "react";
import axios from "axios";

const QueryEditor = () => {

    const [queryParam, setQueryParam] = useState(
        {
            query: "SELECT * FROM testdb.test_table_1;",
            nextToken: "",
        }
    );

    const [queryResult, setQueryResult] = useState({
        wasApplied: null,
        rows: [],
        columnNames: [],
    })

    const queryExecute = () => {
        setQueryResult({
            wasApplied: null,
            rows: [],
            columnNames: [],
        })

        axios({
            method: "POST",
            url: "/api/cassandra/cluster/query",
            data: {
                query: queryParam.query,
                nextToken: queryParam.nextToken,
            },
        }).then((response) => {
            console.log("response : ", response);
            setQueryResult({
                wasApplied: response.data.result.wasApplied,
                rows: response.data.result.rows,
                columnNames: response.data.result.columnNames,
            })
        }).catch((error) => {

        }).finally(() => {
        })
    }

    return (
        <>
            <div className="btn-toolbar pb-1" role="toolbar" aria-label="Toolbar with button groups">
                <div className="btn-group btn-group-sm me-2 " role="group" aria-label="Third group">
                    <button type="button" className="btn btm-sm btn-danger" onClick={queryExecute}>
                        <i className="bi bi-play-fill"></i> Execute
                    </button>
                </div>
                {/*<div className="btn-group btn-group-sm me-2" role="group" aria-label="First group">*/}
                {/*    <button type="button" className="btn btm-sm btn-primary">*/}
                {/*        <i className="bi bi-play-fill"></i>*/}
                {/*    </button>*/}
                {/*    <button type="button" className="btn btm-sm btn-primary">2</button>*/}
                {/*    <button type="button" className="btn btm-sm btn-primary">3</button>*/}
                {/*    <button type="button" className="btn btm-sm btn-primary">4</button>*/}
                {/*</div>*/}
                {/*<div className="btn-group btn-group-sm" role="group" aria-label="Second group">*/}
                {/*    <button type="button" className="btn btm-sm btn-secondary">5</button>*/}
                {/*    <button type="button" className="btn btm-sm btn-secondary">6</button>*/}
                {/*    <button type="button" className="btn btm-sm btn-secondary">7</button>*/}
                {/*</div>*/}
            </div>

            <div className="form-floating">
                <textarea className="form-control" placeholder="Query" id="queryEditor"
                          style={{"height": "300px"}}
                          value={queryParam.query || ''}
                          rows={10}
                          onChange={evt => setQueryParam(t => {
                              return {...t, query: evt.target.value}
                          })}
                ></textarea>
                <label htmlFor="queryEditor">Query</label>
            </div>

            <h4 className={"h4 mt-3"}>Result</h4>
            {
                queryResult.wasApplied && <>
                    <p>wasApplied : {queryResult.wasApplied}</p>

                    <div className="table-responsive small">
                        <table className="table table-striped table-hover table-sm">
                            <thead>
                            <tr>
                                {
                                    queryResult.columnNames.map((info, infoIndex) => {
                                        return (
                                            <th key={`resultHeader${infoIndex}`} scope="col">{info}</th>
                                        )
                                    })
                                }
                            </tr>
                            </thead>
                            <tbody>
                            {
                                queryResult.rows.map((row, rowIndex) => {
                                    return (
                                        <tr key={`resultBody${rowIndex}`}>
                                            {
                                                queryResult.columnNames.map((info, infoIndex) => {
                                                    return (
                                                        <td key={`resultItem${infoIndex}`}>{row[info]}</td>
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
                </>
            }


        </>
    )
}

export default QueryEditor;
