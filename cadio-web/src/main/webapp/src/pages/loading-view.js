import InitializeView from "./initialize-view";

const LoadingView = (props) => {
    const bootstrapLoading = props.bootstrapLoading;
    const systemAvailable = props.systemAvailable;

    if (bootstrapLoading) {
        return (
            <div className="container">
                <div className="row">

                    <div className="mt-5 d-flex justify-content-center">
                        <div className="spinner-border text-danger" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </div>
                    </div>
                </div>
            </div>
        )
    }

    if (systemAvailable === false) {
        return (
            <InitializeView/>
        )
    }

    return (
        <>{props.children}</>
    )
}

export default LoadingView;
