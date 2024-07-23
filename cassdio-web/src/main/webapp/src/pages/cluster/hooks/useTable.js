import axios from "axios";
import {toast} from "react-toastify";
import {useNavigate, useParams} from "react-router-dom";
import {useState} from "react";
import useCassdio from "commons/hooks/useCassdio";

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
            doGetList(null);
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
        columnHeader: [],
        columnList: [],
    };


    const [queryResult, setQueryResult] = useState(initQueryResult)
    const [nextCursor, setNextCursor] = useState('')

    const doGetList = (cursor, setLoading) => {
        if (cursor === null) {
            setQueryResult({
                rows: [],
                columnHeader: [],
            })
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
                columnHeader: response.data.result.columnHeader,
                columnList: response.data.result.columnList,
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
        doGetList,
        queryLoading,
        queryResult,
        nextCursor
    }
}
