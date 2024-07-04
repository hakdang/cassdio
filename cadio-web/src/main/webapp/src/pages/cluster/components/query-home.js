import QueryEditor from "./query/query-editor";
import axios from "axios";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import QueryResult from "./query/query-result";
import {axiosCatch} from "../../../utils/axiosUtils";

const QueryHome = () => {
    const routeParams = useParams();

    const [queryParam, setQueryParam] = useState(
        {
            query: "",
            nextCursor: "",
        }
    );

    const initQueryResult = {
        wasApplied: null,
        rows: [],
        columnNames: [],
    };

    const [queryResult, setQueryResult] = useState(initQueryResult)

    const queryExecute = (query, cursor, setLoading) => {
        if (!query) {
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

        setLoading(true);

        axios({
            method: "POST",
            url: `/api/cassandra/cluster/${routeParams.clusterId}/query`,
            data: {
                query: query,
                pageSize: 50,
                timeoutSeconds: 3,
                cursor: cursor,
            },
        }).then((response) => {
            console.log("response query execute ", response.data.result.nextCursor)
            setQueryParam({
                query: query,
                nextCursor: response.data.result.nextCursor
            })

            setQueryResult({
                wasApplied: response.data.result.wasApplied,
                rows: [...queryResult.rows, ...response.data.result.rows],
                columnNames: response.data.result.columnNames,
            })
        }).catch((error) => {
            axiosCatch(error);
        }).finally(() => {
            setLoading(false);
        })
    }

    useEffect(() => {
        //show component
        setQueryResult(initQueryResult);

        return () => {
            //hide component

        };
    }, []);


    return (
        <>
            <div
                className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h2 className="h2">Query Editor</h2>
                <div className="btn-toolbar mb-2 mb-md-0">
                    <div className="btn-group me-2">
                        <button type="button" className="btn btn-sm btn-outline-secondary">Share</button>
                        <button type="button" className="btn btn-sm btn-outline-secondary">Export</button>
                    </div>
                    <button type="button"
                            className="btn btn-sm btn-outline-secondary dropdown-toggle d-flex align-items-center gap-1">
                        This week
                    </button>
                </div>
            </div>

            <QueryEditor
                queryExecute={queryExecute}
            />
            <QueryResult
                queryExecute={queryExecute}
                result={queryResult}
                query={queryParam.query}
                nextCursor={queryParam.nextCursor}
            />
        </>
    )
}

export default QueryHome;
