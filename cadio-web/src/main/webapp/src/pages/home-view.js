import {Link} from "react-router-dom";
import {useEffect, useState} from "react";
import axios from "axios";

const HomeView = () => {

    const [clustersLoading, setClustersLoading] = useState(false);
    const [clusters, setClusters] = useState([]);

    useEffect(() => {
        //show component

        setClustersLoading(true)

        axios({
            method: "GET",
            url: `/api/cassandra/cluster`,
            params: {}
        }).then((response) => {
            setClusters(response.data.result.clusters)
        }).catch((error) => {
            //TODO : error catch
        }).finally(() => {
            setClustersLoading(false)
        });

        return () => {
            //hide component

        };
    }, []);

    return (
        <>
            <main className="col-12 px-md-4">
                <div
                    className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h2 className="h2">Cadio Dashboard</h2>
                    {/*<div className="btn-toolbar mb-2 mb-md-0">*/}
                    {/*    <div className="btn-group me-2">*/}
                    {/*        <Link className={"btn btn-sm btn-outline-secondary"}*/}
                    {/*              to={"/system"}>*/}
                    {/*            System*/}
                    {/*        </Link>*/}
                    {/*        /!*<button type="button" className="btn btn-sm btn-outline-secondary">Export</button>*!/*/}
                    {/*    </div>*/}
                    {/*    /!*<button type="button"*!/*/}
                    {/*    /!*        className="btn btn-sm btn-outline-secondary dropdown-toggle d-flex align-items-center gap-1">*!/*/}
                    {/*    /!*    This week*!/*/}
                    {/*    /!*</button>*!/*/}
                    {/*</div>*/}
                </div>

                <h4 className="h4">Clusters</h4>

                <div className="table-responsive small">
                    <table className="table table-striped table-sm">
                        <thead>
                        <tr>
                            <th scope="col">Cluster Name</th>
                            <th scope="col">ContactPoints</th>
                            <th scope="col">Port</th>
                            <th scope="col">Local Datacenter</th>
                            <th scope="col">Link</th>
                        </tr>
                        </thead>
                        <tbody>
                        {
                            clustersLoading ? <tr>
                                <td colSpan={6}>
                                    <div className="text-center">
                                        <div className="spinner-border" role="status">
                                            <span className="visually-hidden">Loading...</span>
                                        </div>
                                    </div>
                                </td>
                            </tr> : <>
                                {
                                    clusters && clusters.length > 0 && clusters.map((info, infoIndex) => {
                                        return (
                                            <tr key={infoIndex}>
                                                <td>{info.clusterName}</td>
                                                <td>{info.contactPoints}</td>
                                                <td>{info.port}</td>
                                                <td>{info.localDatacenter}</td>
                                                <td>
                                                    <Link className={"btn btn-sm btn-primary"}
                                                          to={`/cluster/${info.clusterId}`}>
                                                        바로가기
                                                    </Link>
                                                </td>
                                            </tr>
                                        )
                                    })
                                }


                            </>
                        }
                        </tbody>
                    </table>
                </div>

                <Link className={"btn btn-sm btn-outline-secondary"}
                      to={"/system"}>
                    System
                </Link>

            </main>
        </>
    )
}

export default HomeView;
