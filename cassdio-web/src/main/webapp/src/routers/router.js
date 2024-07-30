import {createBrowserRouter} from "react-router-dom";

import CassdioDefaultLayout from "components/layout/cassdio-default-layout";
import ClusterLayout from "components/cluster/cluster-layout";
import AdminLayout from "components/admin/admin-layout";

import CassdioHomePage from "pages/cassdio-home-page";
import ClusterDashboardPage from "pages/cluster/cluster-dashboard-page";
import ClusterNodesPage from "pages/cluster/cluster-nodes-page";
import ClusterClientPage from "pages/cluster/cluster-client-page";
import ClusterMetricsPage from "pages/cluster/cluster-metrics-page";
import ClusterKeyspacePage from "pages/cluster/cluster-keyspace-page";
import ClusterQueryPage from "pages/cluster/cluster-query-page";
import ClusterKeyspaceCompactionPage from "pages/cluster/cluster-keyspace-comapction-page";
import ClusterTablePage from "pages/cluster/cluster-table-page";
import ClusterMonitoringDashboardPage from "pages/cluster/cluster-monitoring-dashboard-page";

import AdminHomePage from "pages/admin/admin-home-page";

import NotFound from "pages/not-found";

import bootstrapApi from "remotes/bootstrapApi";

const Router = createBrowserRouter([
        {
            path: `/`,
            element: <CassdioDefaultLayout/>,
            loader: async () => {
                const bootstrap = await bootstrapApi();
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
                        return {}
                    },
                    children: [
                        {
                            index: true,
                            element: <ClusterDashboardPage/>,
                            loader: () => {
                                return {}
                            },
                        }, {
                            path: `query`,
                            element: <ClusterQueryPage/>,
                            loader: () => {
                                return {}
                            },

                        }, {
                            path: `keyspace/:keyspaceName`,
                            element: <ClusterKeyspacePage/>,
                            loader: () => {
                                return {}
                            },
                        }, {
                            path: `keyspace/:keyspaceName/query`,
                            element: <ClusterQueryPage/>,
                            loader: () => {
                                return {}
                            },
                        }, {
                            path: `keyspace/:keyspaceName/table/:tableName`,
                            element: <ClusterTablePage/>,
                            loader: () => {
                                return {}
                            },
                        }, {
                            path: `keyspace/:keyspaceName/compaction`,
                            element: <ClusterKeyspaceCompactionPage/>,
                            loader: () => {
                                return {}
                            },
                        }, {
                            path: `monitoring`,
                            element: <ClusterMonitoringDashboardPage/>,
                            loader: () => {
                                return {
                                }
                            },
                        }, {
                            path: `monitoring/nodes`,
                            element: <ClusterNodesPage/>,
                            loader: () => {
                                return {
                                }
                            },
                        }, {
                            path: `monitoring/client`,
                            element: <ClusterClientPage/>,
                            loader: () => {
                                return {
                                }
                            }
                        }, {
                            path: `monitoring/metrics`,
                            element: <ClusterMetricsPage/>,
                            loader: () => {
                                return {
                                }
                            },
                        }
                    ]
                }, {
                    path: `admin`,
                    element: <AdminLayout/>,
                    loader: () => {
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
