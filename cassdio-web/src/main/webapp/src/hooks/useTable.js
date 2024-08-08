import {toast} from "react-toastify";
import {useNavigate, useParams} from "react-router-dom";
import {useState} from "react";
import clusterTableTruncateApi from "../remotes/clusterTableTruncateApi";
import clusterTableDropApi from "../remotes/clusterTableDropApi";
import clusterTableRowApi from "../remotes/clusterTableRowApi";

export default function useTable() {
    const navigate = useNavigate();
    const routeParams = useParams();

    const [queryLoading, setQueryLoading] = useState(false)

    const doTableTruncate = () => {
        if (!window.confirm("If you truncate, it cannot be recovered. Do you still want to proceed?")) {
            return;
        }
        //TODO : progressbar

        clusterTableTruncateApi({
            clusterId: routeParams.clusterId,
            keyspaceName: routeParams.keyspaceName,
            tableName: routeParams.tableName,
        }).then((data) => {
            if (!data.ok) {
                return;
            }

            toast.info("complete")
            doGetTableRows(null);

        }).finally(() => {
        })
    }
    const doTableDrop = () => {
        if (!window.confirm("If you Drop, it cannot be recovered. Do you still want to proceed?")) {
            return;
        }

        //TODO : progressbar

        clusterTableDropApi({
            clusterId: routeParams.clusterId,
            keyspaceName: routeParams.keyspaceName,
            tableName: routeParams.tableName,
        }).then((data) => {
            if (!data.ok) {
                return;
            }

            toast.info("complete")
            navigate(`/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}`)
        }).finally(() => {
        })
    }

    const initQueryResult = {
        rows: [],
        rowHeader: [],
        columnList: [],
    };

    const doInitQueryResult = () => {
        setQueryResult(initQueryResult);
    }

    const [queryResult, setQueryResult] = useState(initQueryResult)
    const [nextCursor, setNextCursor] = useState('')

    const doGetTableRows = (cursor, setLoading) => {
        if (cursor === null) {
            setQueryResult(initQueryResult)
        }
        if (!setLoading) {
            setQueryLoading(true);
        } else {
            setLoading(true)
        }

        clusterTableRowApi({
            clusterId: routeParams.clusterId,
            keyspaceName: routeParams.keyspaceName,
            tableName: routeParams.tableName,
            cursor: cursor,
        }).then((data) => {
            setNextCursor(data.result.nextCursor)

            setQueryResult({
                rows: [...queryResult.rows, ...data.result.rows],
                rowHeader: data.result.rowHeader,
                columnList: data.result.columnList,
            })
        }).finally(() => {
            if (!setLoading) {
                setQueryLoading(false);
            } else {
                setLoading(false)
            }
        })
    }

    return {
        doTableTruncate,
        doTableDrop,
        doGetTableRows,
        doInitQueryResult,
        queryLoading,
        queryResult,
        nextCursor
    }
}
