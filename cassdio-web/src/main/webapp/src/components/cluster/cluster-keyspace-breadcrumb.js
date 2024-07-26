import {Link} from "react-router-dom";

const ClusterKeyspaceBreadcrumb = ({active, clusterId, keyspaceName = null, tableName = null,}) => {
    return (
        <div className={"row pt-3"}>
            <nav className={"breadcrumb-arrow"} aria-label="breadcrumb">
                <ol className="breadcrumb">
                    {
                        clusterId &&
                        <li className={`breadcrumb-item ${active === 'CLUSTER' ? 'active' : ''}`}>
                            {
                                active === 'CLUSTER' ? <>Cluster</>
                                    : <Link to={`/cluster/${clusterId}`}
                                            className={"link-body-emphasis text-decoration-none"}>
                                        Cluster
                                    </Link>
                            }
                        </li>
                    }
                    {
                        clusterId && keyspaceName &&
                        <li className={`breadcrumb-item ${active === 'KEYSPACE' ? 'active' : ''}`}>
                            {
                                active === 'KEYSPACE' ? <>{keyspaceName}</>
                                    : <Link to={`/cluster/${clusterId}/keyspace/${keyspaceName}`}
                                            className={"link-body-emphasis text-decoration-none"}>
                                        {keyspaceName}
                                    </Link>

                            }

                        </li>
                    }
                    {
                        clusterId && keyspaceName && tableName &&
                        <li className={`breadcrumb-item ${active === 'TABLE' ? 'active' : ''}`}>
                            {
                                active === 'TABLE' ? <> {tableName}</>
                                    : <Link to={`/cluster/${clusterId}/keyspace/${keyspaceName}/table/${tableName}`}
                                            className={"link-body-emphasis text-decoration-none"}>
                                        {tableName}
                                    </Link>
                            }


                        </li>
                    }
                </ol>
            </nav>
        </div>
    )
}

export default ClusterKeyspaceBreadcrumb;
