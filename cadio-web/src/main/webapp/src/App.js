import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import "bootstrap"
import 'bootstrap-icons/font/bootstrap-icons.css'

import CadioHeader from "./components/layout/cadio-header";
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
import NodesHome from "./pages/cluster/components/nodes-home";
import SystemView from "./pages/system/system-view";
import TableRow from "./pages/cluster/components/keyspace/table/table-row";
import TableInformation from "./pages/cluster/components/keyspace/table/table-information";
import TableImport from "./pages/cluster/components/keyspace/table/table-import";
import TableExport from "./pages/cluster/components/keyspace/table/table-export";
import LoadingView from "./pages/loading-view";
import SystemHome from "./pages/system/components/system-home";
import SystemClusterManageHome from "./pages/system/components/cluster/system-cluster-manage-home";

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
        <div className={"min-vh-100"}>
            <BrowserRouter>
                <LoadingView
                    bootstrapLoading={bootstrapLoading}
                    systemAvailable={systemAvailable}
                >
                    <CadioHeader/>

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
                                   <ClusterView><TableHome
                                       submenu={"IMPORT"}><TableImport/></TableHome></ClusterView>}></Route>
                        <Route path="/cluster/:clusterId/keyspace/:keyspaceName/table/:tableName/export"
                               element={
                                   <ClusterView><TableHome
                                       submenu={"EXPORT"}><TableExport/></TableHome></ClusterView>}></Route>
                        <Route path="/system"
                               element={<SystemView>
                                    <SystemHome/>
                               </SystemView>}></Route>
                        <Route path="/system/cluster"
                               element={<SystemView>
                                    <SystemClusterManageHome/>
                               </SystemView>}></Route>
                        {/* 상단에 위치하는 라우트들의 규칙을 모두 확인, 일치하는 라우트가 없는경우 처리 */}
                        <Route path="*" element={<NotFound/>}></Route>
                    </Routes>
                </LoadingView>
            </BrowserRouter>

        </div>
    )
}

export default App;
