import axios from "axios";
import {toast} from "react-toastify";
import {useNavigate, useParams} from "react-router-dom";
import {useState} from "react";
import useCassdio from "commons/hooks/useCassdio";
import {CassdioUtils} from "utils/cassdioUtils";

export default function useTable() {
    const navigate = useNavigate();
    const routeParams = useParams();
    const {errorCatch} = useCassdio();

    const [queryLoading, setQueryLoading] = useState(false)

    const doTableTruncate = () => {
        if (!window.confirm("If you truncate, it cannot be recovered. Do you still want to proceed?")) {
            return;
        }
        //TODO : progressbar

        axios({
            method: "DELETE",
            url: `/api/cassandra/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}/table/${routeParams.tableName}/truncate`,
            params: {},
        }).then((response) => {
            toast.info("complete")
            doGetTableList(null);
        }).catch((error) => {
            errorCatch(error);
        }).finally(() => {
        })
    }
    const doTableDrop = () => {
        if (!window.confirm("If you Drop, it cannot be recovered. Do you still want to proceed?")) {
            return;
        }

        //TODO : progressbar

        axios({
            method: "DELETE",
            url: `/api/cassandra/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}/table/${routeParams.tableName}`,
            params: {},
        }).then((response) => {
            toast.info("complete")
            navigate(`/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}`)
        }).catch((error) => {
            errorCatch(error);
        }).finally(() => {
        })
    }

    const initQueryResult = {
        rows: [],
        rowHeader: [],
        columnList: [],
        convertedRowHeader: [],
    };


    const [queryResult, setQueryResult] = useState(initQueryResult)
    const [nextCursor, setNextCursor] = useState('')

    const doGetTableList = (cursor, setLoading) => {
        if (cursor === null) {
            setQueryResult(initQueryResult)
        }
        if (!setLoading) {
            setQueryLoading(true);
        } else {
            setLoading(true)
        }

        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}/table/${routeParams.tableName}/row`,
            params: {
                pageSize: 50,
                timeoutSeconds: 3,
                cursor: cursor,
            },
        }).then((response) => {
            setNextCursor(response.data.result.nextCursor)

            setQueryResult({
                rows: [...queryResult.rows, ...response.data.result.rows],
                rowHeader: response.data.result.rowHeader,
                columnList: response.data.result.columnList,
                convertedRowHeader: CassdioUtils.convertRowHeader(response.data.result.columnList, response.data.result.rowHeader),
            })
        }).catch((error) => {
            errorCatch(error);
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
        doGetTableList,
        queryLoading,
        queryResult,
        nextCursor
    }
}
