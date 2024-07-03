import {Link, useParams} from "react-router-dom";
import {useClusterState} from "../../context/clusterContext";
import {useEffect, useState} from "react";
import axios from "axios";
import KeyspaceDetailDescribe from "./keyspace-detail-describe";
import Spinner from "../../../../components/spinner";
import TableList from "./table/table-list";
import {axiosCatch} from "../../../../utils/axiosUtils";

const KeyspaceHome = () => {

    const routeParams = useParams();

    //const {doGetKeyspaceList} = useCluster();
    const {
        keyspaceList,
        keyspaceListLoading,
    } = useClusterState();

    const [detailLoading, setDetailLoading] = useState(false);
    const [describe, setDescribe] = useState('');
    const [tableLoading, setTableLoading] = useState(false);
    const [tableCursor, setTableCursor] = useState(null)
    const [tableList, setTableList] = useState([]);

    useEffect(() => {
        //show component
        setDescribe('');
        setDetailLoading(true)
        setTableLoading(true)
        setTableList([]);
        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}`,
            params: {}
        }).then((response) => {
            console.log("res ", response);
            setDescribe(response.data.describe)
        }).catch((error) => {
            //TODO : error catch
        }).finally(() => {
            setDetailLoading(false)
        });

        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}/table`,
            params: {
                size: 50,
                cursor: tableCursor // TODO: 스크롤 페이지네이션 처리
            }
        }).then((response) => {
            console.log("res ", response);
            setTableList(response.data.result.items)
            if (response.data.result.cursor.hasNext) {
                setTableCursor(response.data.result.cursor.next)
            }
        }).catch((error) => {
            console.log(error)
            //TODO : error catch
            axiosCatch(error)
        }).finally(() => {
            setTableLoading(false)
        });

        return () => {
            //hide component

        };
    }, [routeParams.clusterId, routeParams.keyspaceName]);

    return (
        <>
            <div className={"row pt-3"}>
                <nav aria-label="breadcrumb">
                    <ol className="breadcrumb">
                        <li className="breadcrumb-item">
                            <Link to={`/cluster/${routeParams.clusterId}`}
                                  className={"link-body-emphasis text-decoration-none"}>
                                Cluster {routeParams.clusterId}
                            </Link>
                        </li>
                        <li className="breadcrumb-item active" aria-current="page">
                            <Link to={`/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}`}
                                  className={"link-body-emphasis text-decoration-none"}>
                                {routeParams.keyspaceName}
                            </Link>
                        </li>
                    </ol>
                </nav>
            </div>

            <div
                className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h2 className="h2">Keyspace</h2>
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

            <Spinner loading={detailLoading}>
                <KeyspaceDetailDescribe describe={describe}/>

            </Spinner>

            <br/>
            <div
                className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h2 className="h2">Tables</h2>
            </div>

            <Spinner loading={tableLoading}>
                <TableList clusterId={routeParams.clusterId} keyspace={routeParams.keyspaceName} tableList={tableList}/>
            </Spinner>
        </>
    )
}

export default KeyspaceHome;
