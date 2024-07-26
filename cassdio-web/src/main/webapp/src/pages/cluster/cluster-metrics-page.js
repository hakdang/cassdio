const ClusterMetricsPage = () => {
    return (
        <>
            <div
                className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h2 className="h2">Cluster Metrics</h2>
                <div className="btn-toolbar mb-2 mb-md-0">
                    <div className="btn-group me-2">
                        <button type="button" className="btn btn-sm btn-outline-secondary">Share</button>
                        <button type="button" className="btn btn-sm btn-outline-secondary">Export</button>
                    </div>
                    <button type="button"
                            className="btn btn-sm btn-outline-secondary dropdown-toggle d-flex align-items-center gap-1">
                        This week
                    </button>
                </div>
            </div>

        </>
    )
}

export default ClusterMetricsPage;
