import {useEffect} from "react";
import {BrowserRouter, Route, Routes} from "react-router-dom";

import './App.css';

import CassdioHeader from "./components/layout/cassdio-header";

import HomeMainView from "./pages/home-main-view";
import NotFound from "./pages/not-found";
import ClusterMainView from "./pages/cluster/cluster-main-view";
import KeyspaceHome from "./pages/cluster/components/keyspace/keyspace-home";
import ClusterHome from "./pages/cluster/components/cluster-home";
import TableHome from "./pages/cluster/components/keyspace/table/table-home";
import QueryHome from "./pages/cluster/components/query-home";
import MetricsHome from "./pages/cluster/components/metrics-home";

import useCassdio from "./commons/hooks/useCassdio";
import {useCassdioState} from "./commons/context/cassdioContext";
import NodesHome from "./pages/cluster/components/nodes-home";
import CassdioToast from "./components/cassdio-toast";
import CompactionHome from "./pages/cluster/components/compaction-home";
import ClientHome from "./pages/cluster/components/client-home";

function App() {
    const {doBootstrap} = useCassdio();
    const {} = useCassdioState();

    useEffect(() => {
        //show component

        doBootstrap();

        // return () => {
        //     //hide component
        //
        // };
    }, []);

    return (
        <div className={"min-vh-100"}>
            <BrowserRouter>

                <CassdioHeader/>
                <Routes>
                    <Route path="/" element={
                        <HomeMainView/>
                    }></Route>
                    {/*s:cluster*/}
                    <Route path="/cluster/:clusterId" element={
                        <ClusterMainView><ClusterHome/></ClusterMainView>
                    }></Route>
                    <Route path="/cluster/:clusterId/nodes" element={
                        <ClusterMainView><NodesHome/></ClusterMainView>
                    }></Route>
                    <Route path="/cluster/:clusterId/client" element={
                        <ClusterMainView><ClientHome/></ClusterMainView>
                    }></Route>
                    <Route path="/cluster/:clusterId/keyspace/:keyspaceName/compaction" element={
                        <ClusterMainView><CompactionHome/></ClusterMainView>
                    }></Route>
                    <Route path="/cluster/:clusterId/query" element={
                        <ClusterMainView><QueryHome/></ClusterMainView>
                    }></Route>
                    <Route path="/cluster/:clusterId/metrics" element={
                        <ClusterMainView><MetricsHome/></ClusterMainView>
                    }></Route>
                    <Route path="/cluster/:clusterId/keyspace/:keyspaceName" element={
                        <ClusterMainView><KeyspaceHome/></ClusterMainView>
                    }></Route>
                    <Route path="/cluster/:clusterId/keyspace/:keyspaceName/table/:tableName" element={
                        <ClusterMainView><TableHome/></ClusterMainView>
                    }></Route>
                    {/*e:cluster*/}
                    <Route path="*" element={
                        <NotFound/>
                    }></Route>
                </Routes>

                <CassdioToast/>
            </BrowserRouter>
        </div>
    )
}

export default App;
