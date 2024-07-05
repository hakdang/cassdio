import {Link, useParams} from "react-router-dom";
import {useClusterState} from "../../context/clusterContext";
import {useEffect, useState} from "react";
import axios from "axios";
import KeyspaceDetailDescribe from "./keyspace-detail-describe";
import Spinner from "../../../../components/spinner";
import KeyspaceTableList from "./keyspace-table-list";
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

        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}`,
            params: {}
        }).then((response) => {
            setDescribe(response.data.describe)
        }).catch((error) => {
            console.error(error)
        }).finally(() => {
            setDetailLoading(false)
        });

        fetchData(true)

        return () => {
            //hide component

        };
    }, [routeParams.clusterId, routeParams.keyspaceName]);

    const fetchData = async (reset) => {
        if (reset) {
            setTableLoading(true)
            setTableCursor(null)
            setTableList([])
        }

        try {
            const response = await axios({
                method: "GET",
                url: `/api/cassandra/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}/table`,
                params: {
                    size: 10,
                    cursor: reset ? null : tableCursor
                }
            })

            setTableList(prevList => [...prevList, ...response.data.result.items]);
            setTableCursor(response.data.result.cursor.next)

            setTableLoading(false)
        } catch (error) {
            axiosCatch(error)
        }
    }

    return (
        <>
            <div className={"row pt-3"}>
                <nav className={"breadcrumb-arrow"} aria-label="breadcrumb">
                    <ol className="breadcrumb">
                        <li className="breadcrumb-item">
                            <Link to={`/cluster/${routeParams.clusterId}`}
                                  className={"link-body-emphasis text-decoration-none"}>
                                Cluster
                            </Link>
                        </li>
                        <li className="breadcrumb-item active" aria-current="page">
                            {routeParams.keyspaceName}
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
                <KeyspaceTableList clusterId={routeParams.clusterId} keyspace={routeParams.keyspaceName}
                                   tableList={tableList}/>
                {
                    tableCursor &&
                    <div className="d-grid gap-2 col-6 mx-auto">
                        <button className="btn btn-outline-secondary" type="button"
                                onClick={() => fetchData(false)}>More
                        </button>
                    </div>
                }
            </Spinner>
        </>
    )
}

export default KeyspaceHome;
