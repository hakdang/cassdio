import {Link} from "react-router-dom";
import {useEffect, useState} from "react";
import axios from "axios";
import CadioHeader from "../components/layout/cadio-header";
import CadioSidebar from "../components/layout/cadio-sidebar";

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
        <div className="container-fluid h-100">
            <div className="row">

                <CadioSidebar>
                    <ul className="nav flex-column">
                        <li className="nav-item">
                            <Link
                                className={`nav-link d-flex align-items-center gap-2 link-body-emphasis text-decoration-none`}
                                to={`/`}>
                                <i className="bi bi-house"></i> Home
                            </Link>
                            <Link
                                className={`nav-link d-flex align-items-center gap-2 link-body-emphasis text-decoration-none`}
                                to={`/system`}>
                                <i className="bi bi-gear"></i> System
                            </Link>
                        </li>
                    </ul>
                </CadioSidebar>

                <main className="col-md-9 ms-sm-auto col-lg-10 px-md-4">
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
                                <th className={"text-center"} scope="col">Cluster Name</th>
                                <th className={"text-center"} scope="col">ContactPoints</th>
                                <th className={"text-center"} scope="col">Port</th>
                                <th className={"text-center"} scope="col">Local Datacenter</th>
                            </tr>
                            </thead>
                            <tbody>
                            {
                                clustersLoading ? <tr>
                                    <td colSpan={5}>
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
                                                    <td className={"text-center"}>
                                                        <Link to={`/cluster/${info.clusterId}`}>
                                                            {info.clusterName}
                                                        </Link>
                                                    </td>
                                                    <td className={"text-center"}>{info.contactPoints}</td>
                                                    <td className={"text-center"}>{info.port}</td>
                                                    <td className={"text-center"}>{info.localDatacenter}</td>
                                                </tr>
                                            )
                                        })
                                    }


                                </>
                            }
                            </tbody>
                        </table>
                    </div>
                </main>
            </div>
        </div>
    )
}

export default HomeView;
