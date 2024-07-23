import {Link} from "react-router-dom";

const ClusterBreadcrumb = (props) => {
    const clusterId = props.clusterId;
    const keyspaceName = props.keyspaceName || null;
    const tableName = props.tableName || null;
    const active = props.active;

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

export default ClusterBreadcrumb;
