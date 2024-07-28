import {createBrowserRouter} from "react-router-dom";
import CassdioDefaultLayout from "components/layout/cassdio-default-layout";
import bootstrapApi from "remotes/bootstrapApi";
import CassdioHomePage from "pages/cassdio-home-page";
import ClusterLayout from "components/cluster/cluster-layout";
import ClusterDashboardPage from "pages/cluster/cluster-dashboard-page";
import ClusterNodesPage from "pages/cluster/cluster-nodes-page";
import ClusterClientPage from "pages/cluster/cluster-client-page";
import ClusterMetricsPage from "pages/cluster/cluster-metrics-page";
import ClusterKeyspacePage from "pages/cluster/cluster-keyspace-page";
import ClusterQueryPage from "pages/cluster/cluster-query-page";
import ClusterKeyspaceCompactionPage from "pages/cluster/cluster-keyspace-comapction-page";
import NotFound from "pages/not-found";
import AdminLayout from "components/admin/admin-layout";
import AdminHomePage from "pages/admin/admin-home-page";
import ClusterTablePage from "pages/cluster/cluster-table-page";
import ClusterMonitoringDashboardPage from "pages/cluster/cluster-monitoring-dashboard-page";

const Router = createBrowserRouter([
        {
            path: `/`,
            element: <CassdioDefaultLayout/>,
            loader: async () => {
                const bootstrap = await bootstrapApi({});
                return {
                    bootstrap,
                }
            },
            children: [
                {
                    index: true,
                    element: <CassdioHomePage/>
                }, {
                    path: `cluster/:clusterId`,
                    element: <ClusterLayout/>,
                    loader: async ({params}) => {
                        //const checkData = await bootstrapApi();
                        console.log("cluster : ", params)

                        return {}
                    },
                    children: [
                        {
                            index: true,
                            element: <ClusterDashboardPage/>,
                            loader: () => {
                                //const checkData = await bootstrapApi();
                                console.log("cluster")
                                return {}
                            },
                        }, {
                            path: `query`,
                            element: <ClusterQueryPage/>,
                            loader: () => {
                                //const checkData = await bootstrapApi();
                                console.log("query client")
                                return {}
                            },

                        }, {
                            path: `keyspace/:keyspaceName`,
                            element: <ClusterKeyspacePage/>,
                            loader: () => {
                                //const checkData = await bootstrapApi();
                                console.log("keyspace client")
                                return {}
                            },
                        }, {
                            path: `keyspace/:keyspaceName/query`,
                            element: <ClusterQueryPage/>,
                            loader: () => {
                                //const checkData = await bootstrapApi();
                                console.log("keyspace query client")
                                return {}
                            },
                        }, {
                            path: `keyspace/:keyspaceName/table/:tableName`,
                            element: <ClusterTablePage/>,
                            loader: () => {
                                //const checkData = await bootstrapApi();
                                console.log("keyspace client")
                                return {}
                            },
                        }, {
                            path: `keyspace/:keyspaceName/compaction`,
                            element: <ClusterKeyspaceCompactionPage/>,
                            loader: () => {
                                //const checkData = await bootstrapApi();
                                console.log("keyspace client")
                                return {}
                            },
                        }, {
                            path: `monitoring`,
                            element: <ClusterMonitoringDashboardPage/>,
                            loader: () => {
                                //const checkData = await bootstrapApi();
                                return {
                                }
                            },
                        }, {
                            path: `monitoring/nodes`,
                            element: <ClusterNodesPage/>,
                            loader: () => {
                                //const checkData = await bootstrapApi();
                                return {
                                }
                            },
                        }, {
                            path: `monitoring/client`,
                            element: <ClusterClientPage/>,
                            loader: () => {
                                //const checkData = await bootstrapApi();
                                return {
                                }
                            }
                        }, {
                            path: `monitoring/metrics`,
                            element: <ClusterMetricsPage/>,
                            loader: () => {
                                //const checkData = await bootstrapApi();
                                return {
                                }
                            },
                        }
                    ]
                }, {
                    path: `admin`,
                    element: <AdminLayout/>,
                    loader: () => {
                        console.log("admin")
                        return {}
                    },
                    children: [
                        {
                            index: true,
                            element: <AdminHomePage/>
                        },

                    ]
                }
            ]
        }, {
            path: `*`,
            element: <NotFound/>
        }
    ]
)

export default Router;
