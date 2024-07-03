import {Link} from "react-router-dom";
import {useState} from "react";
import axios from "axios";

const QueryEditor = () => {

    const [queryLoading, setQueryLoading] = useState(false)

    const [queryParam, setQueryParam] = useState(
        {
            query: "SELECT * FROM testdb.test_table_1;",
            nextCursor: "",
        }
    );

    const [queryResult, setQueryResult] = useState({
        wasApplied: null,
        rows: [],
        columnNames: [],
    })

    const queryExecute = (cursor) => {
        if (!queryParam.query) {
            alert("쿼리를 입력해 주세요.")
            return;
        }

        if (cursor === null) {
            setQueryResult({
                wasApplied: null,
                rows: [],
                columnNames: [],
            })
        }

        setQueryLoading(true);

        axios({
            method: "POST",
            url: "/api/cassandra/cluster/query",
            data: {
                query: queryParam.query,
                pageSize: 2,
                timeoutSeconds: 3,
                cursor: cursor,
            },
        }).then((response) => {
            console.log("response : ", response);

            setQueryParam(t => {
                return {
                    ...t,
                    nextCursor: response.data.result.nextCursor
                }
            })

            setQueryResult({
                wasApplied: response.data.result.wasApplied,
                rows: [...queryResult.rows, ...response.data.result.rows],
                columnNames: response.data.result.columnNames,
            })
        }).catch((error) => {

        }).finally(() => {
            setQueryLoading(false);
        })
    }

    return (
        <>
            <div className="btn-toolbar pb-1" role="toolbar" aria-label="Toolbar with button groups">
                <div className="btn-group btn-group-sm me-2 " role="group" aria-label="Third group">
                    <button type="button" className="btn btm-sm btn-danger" onClick={() => queryExecute(null)}>
                        <i className="bi bi-play-fill"></i> Execute

                        {
                            queryLoading &&
                            <div className="ms-2 spinner-border spinner-border-sm" role="status">
                                <span className="visually-hidden">Loading...</span>
                            </div>
                        }

                    </button>
                </div>
                <div className="btn-group btn-group-sm me-2" role="group" aria-label="First group">
                    <button className="btn btn-sm btn-primary" type="button" data-bs-toggle="collapse"
                            data-bs-target="#queryOptionCollapse" aria-expanded="false"
                            aria-controls="queryOptionCollapse">
                        <i className="bi bi-gear-wide-connected"></i> Options
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

            <div className="collapse" id="queryOptionCollapse">
                <div className="card card-body mt-1 mb-3">
                    <div className="row">
                        <div className="col">
                            <div className="form-floating">
                                <select className="form-select form-select-sm" id="floatingSelect">
                                    <option value="1">One</option>
                                    <option value="2">Two</option>
                                    <option value="3">Three</option>
                                </select>
                                <label htmlFor="floatingSelect">Limit</label>
                            </div>
                        </div>
                        <div className="col">
                            <div className="form-floating">
                                <select className="form-select form-select-sm" id="floatingSelect">
                                    <option value="1">One</option>
                                    <option value="2">Two</option>
                                    <option value="3">Three</option>
                                </select>
                                <label htmlFor="floatingSelect">ConsistencyLevel</label>
                            </div>
                        </div>
                        <div className="col">
                            <div className="form-floating mb-3">
                                <input type="email" className="form-control form-control-sm" id="floatingInput"
                                       placeholder="name@example.com"/>
                                <label htmlFor="floatingInput">Query Timeout</label>
                            </div>
                        </div>
                    </div>

                    <div className="row g-3">
                        <div className="col">
                            <div className="form-check form-switch">
                                <input className="form-check-input" type="checkbox" role="switch"
                                       id="flexSwitchCheckDefault"/>
                                <label className="form-check-label" htmlFor="flexSwitchCheckDefault">
                                    Tracing On
                                </label>
                            </div>
                        </div>
                        <div className="col">

                        </div>
                    </div>

                </div>
            </div>

            <div className="form-floating">
                {/*현재는 단일 쿼리만 실행 가능하도록*/}
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

            {
                queryResult.wasApplied && <>
                    <h4 className={"h4 mt-3"}>Result</h4>

                    <p>wasApplied : {queryResult.wasApplied}</p>

                    <div className="table-responsive small">
                        <table className="table table-striped table-hover table-sm">
                            <thead>
                            <tr>
                                {
                                    queryResult.columnNames.map((info, infoIndex) => {
                                        return (
                                            <th className={"text-center"} key={`resultHeader${infoIndex}`} scope="col">{info}</th>
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
                                                        <td className={"text-center"} key={`resultItem${infoIndex}`}>{row[info]}</td>
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
                        queryParam.nextCursor &&
                        <div className="d-grid gap-2">
                            <button className="btn btn-primary" type="button"
                                    onClick={() => queryExecute(queryParam.nextCursor)}>More
                            </button>
                        </div>
                    }
                </>
            }
        </>
    )
}

export default QueryEditor;
