import {Link} from "react-router-dom";
import React from "react";

const ClusterKeyspaceBreadcrumb = ({children, active, clusterId, keyspaceName = null, tableName = null,}) => {
    return (
        <div className={"row pt-3"}>
            <nav className={"breadcrumb-arrow"} aria-label="breadcrumb">
                <ol className="breadcrumb">
                    {
                        clusterId &&
                        <li className={`breadcrumb-item ${active === 'CLUSTER' ? 'active' : ''}`}>
                            {
                                active === 'CLUSTER' ? <><i className="bi bi-database-fill"></i> Cluster</>
                                    : <Link to={`/cluster/${clusterId}`}
                                            className={"link-body-emphasis text-decoration-none"}>
                                        <i className="bi bi-database-fill"></i> Cluster
                                    </Link>
                            }
                        </li>
                    }
                    {
                        clusterId && keyspaceName &&
                        <li className={`breadcrumb-item ${active === 'KEYSPACE' ? 'active' : ''}`}>
                            {
                                active === 'KEYSPACE' ? <><i className="bi bi-database"></i> {keyspaceName}</>
                                    : <Link to={`/cluster/${clusterId}/keyspace/${keyspaceName}`}
                                            className={"link-body-emphasis text-decoration-none"}>
                                        <i className="bi bi-database"></i> {keyspaceName}
                                    </Link>

                            }
                        </li>
                    }
                    {
                        clusterId && keyspaceName && tableName &&
                        <li className={`breadcrumb-item ${active === 'TABLE' ? 'active' : ''}`}>
                            {
                                active === 'TABLE' ? <><i className="bi bi-table"></i> {tableName}</>
                                    : <Link to={`/cluster/${clusterId}/keyspace/${keyspaceName}/table/${tableName}`}
                                            className={"link-body-emphasis text-decoration-none"}>
                                        <i className="bi bi-table"></i> {tableName}
                                    </Link>
                            }
                        </li>
                    }
                    {
                        children
                    }
                </ol>
            </nav>
        </div>
    )
}

export default ClusterKeyspaceBreadcrumb;
