import {Link, useParams} from "react-router-dom";

const ClusterMonitoringNavLink = ({active}) => {
    const routeParams = useParams();

    return (
        <div className="nav-scroller py-1 mb-3">
            <nav className="nav nav-underline">
                <Link className={`nav-item nav-link link-body-emphasis ${active === 'NODES' ? 'active' : ''}`}
                      to={`/cluster/${routeParams.clusterId}/monitoring/nodes`}>
                    Nodes
                </Link>
                <Link className={`nav-item nav-link link-body-emphasis ${active === 'CLIENT' ? 'active' : ''}`}
                      to={`/cluster/${routeParams.clusterId}/monitoring/client`}>
                    Client
                </Link>
                <Link className={`nav-item nav-link link-body-emphasis ${active === 'METRICS' ? 'active' : ''}`}
                      to={`/cluster/${routeParams.clusterId}/monitoring/metrics`}>
                    Metrics
                </Link>

            </nav>
        </div>
    )
}

export default ClusterMonitoringNavLink;
