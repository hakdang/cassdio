import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import "bootstrap"
import 'bootstrap-icons/font/bootstrap-icons.css'

import ConsoleNavbar from "./components/layout/console-navbar";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import HomeView from "./pages/home-view";
import NotFound from "./pages/not-found";
import ClusterView from "./pages/cluster/cluster-view";
import KeyspaceHome from "./pages/cluster/components/keyspace/keyspace-home";
import ClusterHome from "./pages/cluster/components/cluster-home";
import TableHome from "./pages/cluster/components/keyspace/table/table-home";
import QueryHome from "./pages/cluster/components/query-home";
import MetricsHome from "./pages/cluster/components/metrics-home";
import {useEffect} from "react";
import useCadio from "./pages/commons/hooks/useCadio";
import {useCadioState} from "./pages/commons/context/cadioContext";
import InitializeView from "./pages/initialize-view";
import NodesHome from "./pages/cluster/components/nodes-home";
import SystemView from "./pages/system/system-view";
import TableRow from "./pages/cluster/components/keyspace/table/table-row";
import TableInformation from "./pages/cluster/components/keyspace/table/table-information";
import TableImport from "./pages/cluster/components/keyspace/table/table-import";
import TableExport from "./pages/cluster/components/keyspace/table/table-export";

function App() {
    const {doBootstrap} = useCadio();
    const {
        bootstrapLoading,
        systemAvailable
    } = useCadioState();

    useEffect(() => {
        //show component

        doBootstrap();

        return () => {
            //hide component

        };
    }, [systemAvailable]);

    return (
        <div className="App">
            <BrowserRouter>
                {
                    systemAvailable && <ConsoleNavbar/>
                }

                <div className="container-fluid">
                    <div className="row">

                        {
                            bootstrapLoading === false ?
                                systemAvailable === false ?
                                    <InitializeView/> :
                                    <Routes>
                                        <Route path="/" element={<HomeView/>}></Route>
                                        <Route path="/cluster/:clusterId"
                                               element={<ClusterView><ClusterHome/></ClusterView>}></Route>
                                        <Route path="/cluster/:clusterId/nodes"
                                               element={<ClusterView><NodesHome/></ClusterView>}></Route>

                                        <Route path="/cluster/:clusterId/query"
                                               element={<ClusterView><QueryHome/></ClusterView>}></Route>
                                        <Route path="/cluster/:clusterId/metrics"
                                               element={<ClusterView><MetricsHome/></ClusterView>}></Route>

                                        <Route path="/cluster/:clusterId/keyspace/:keyspaceName"
                                               element={<ClusterView><KeyspaceHome/></ClusterView>}></Route>
                                        <Route path="/cluster/:clusterId/keyspace/:keyspaceName/table/:tableName"
                                               element={
                                                   <ClusterView><TableHome
                                                       submenu={"HOME"}><TableInformation/></TableHome></ClusterView>}></Route>
                                        <Route path="/cluster/:clusterId/keyspace/:keyspaceName/table/:tableName/row"
                                               element={
                                                   <ClusterView><TableHome
                                                       submenu={"ROW"}><TableRow/></TableHome></ClusterView>}></Route>
                                        <Route path="/cluster/:clusterId/keyspace/:keyspaceName/table/:tableName/import"
                                               element={
                                                   <ClusterView><TableHome submenu={"IMPORT"}><TableImport/></TableHome></ClusterView>}></Route>
                                        <Route path="/cluster/:clusterId/keyspace/:keyspaceName/table/:tableName/export"
                                               element={
                                                   <ClusterView><TableHome submenu={"EXPORT"}><TableExport/></TableHome></ClusterView>}></Route>
                                        <Route path="/system"
                                               element={<SystemView/>}></Route>
                                        {/* 상단에 위치하는 라우트들의 규칙을 모두 확인, 일치하는 라우트가 없는경우 처리 */}
                                        <Route path="*" element={<NotFound/>}></Route>
                                    </Routes>
                                : <div className="mt-5 d-flex justify-content-center">
                                    <div className="spinner-border text-danger" role="status">
                                        <span className="visually-hidden">Loading...</span>
                                    </div>
                                </div>
                        }


                    </div>
                </div>

            </BrowserRouter>
            <div className="container">
                <footer className="d-flex flex-wrap justify-content-between align-items-center py-3 my-4 border-top">
                    <div className="col-md-4 d-flex align-items-center">
                        <span className="mb-3 mb-md-0 text-body-secondary">&copy; 2024 Company, Inc</span>
                    </div>

                </footer>
            </div>
        </div>
    )
}

export default App;
