import {useParams} from "react-router-dom";
import useCassdio from "../../hooks/useCassdio";
import {useEffect, useState} from "react";
import {toast} from "react-toastify";
import axios from "axios";
import QueryEditor from "../../components/cluster/query-editor";
import QueryResult from "../../components/cluster/query-result";
import QueryTraceViewModal from "../../components/cluster/modal/query-trace-view-modal";

const ClusterQueryPage = () => {
    const routeParams = useParams();

    const {errorCatch} = useCassdio();
    const [showQueryTrace, setShowQueryTrace] = useState(false);

    const [queryParam, setQueryParam] = useState(
        {
            query: "",
            nextCursor: "",
        }
    );

    const [queryOptions, setQueryOptions] = useState({
        limit: 10,
        trace: true,
    });

    const initQueryResult = {
        wasApplied: null,
        rows: [],
        rowHeader: [],
        queryTrace: {},
    };

    const [queryResult, setQueryResult] = useState(initQueryResult)

    const queryExecute = (query, cursor, setLoading) => {
        if (!query) {
            toast.warn("Please enter a Query!")
            return;
        }

        if (cursor === null) {
            setQueryResult(initQueryResult)
        }

        setLoading(true);

        axios({
            method: "POST",
            url: `/api/cassandra/cluster/${routeParams.clusterId}/query`,
            data: {
                query: query,
                pageSize: queryOptions.limit,
                trace: queryOptions.trace,
                timeoutSeconds: 3,
                cursor: cursor,
            },
        }).then((response) => {
            setQueryParam({
                query: query,
                nextCursor: response.data.result.nextCursor
            })

            setQueryResult({
                wasApplied: response.data.result.wasApplied,
                rows: [...queryResult.rows, ...response.data.result.rows],
                rowHeader: response.data.result.rowHeader,
                queryTrace: response.data.result.queryTrace,
            })
        }).catch((error) => {
            errorCatch(error);
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
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);


    return (
        <>
            <div
                className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h2 className="h2">Query Editor</h2>
                {/*<div className="btn-toolbar mb-2 mb-md-0">*/}
                {/*    <div className="btn-group me-2">*/}
                {/*        <button type="button" className="btn btn-sm btn-outline-secondary">Share</button>*/}
                {/*        <button type="button" className="btn btn-sm btn-outline-secondary">Export</button>*/}
                {/*    </div>*/}
                {/*    <button type="button"*/}
                {/*            className="btn btn-sm btn-outline-secondary dropdown-toggle d-flex align-items-center gap-1">*/}
                {/*        This week*/}
                {/*    </button>*/}
                {/*</div>*/}
            </div>

            <QueryEditor
                queryExecute={queryExecute}
                queryOptions={queryOptions}
                setQueryOptions={setQueryOptions}
            />
            {
                queryResult && <QueryResult
                    queryExecute={queryExecute}
                    result={queryResult}
                    query={queryParam.query}
                    queryOptions={queryOptions}
                    setShowQueryTrace={setShowQueryTrace}
                    nextCursor={queryParam.nextCursor}
                />
            }
            {
                queryResult && queryResult.queryTrace && showQueryTrace &&
                <QueryTraceViewModal
                    show={showQueryTrace}
                    handleClose={() => setShowQueryTrace(false)}
                    queryTrace={queryResult.queryTrace}
                />
            }


        </>
    )
}

export default ClusterQueryPage;