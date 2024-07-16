export default function Spinner(props: any) {
    const loading = props.loading || false;
    const mode = props.mode || '';

    if (loading) {
        if (mode === 'justify-content-center') {
            return (
                <div className="d-flex justify-content-center">
                    <div className="spinner-border" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </div>
                </div>
            )
        }

        return (
            <div className="text-center">
                <div className="spinner-border text-danger" role="status">
                    <span className="visually-hidden">Loading...</span>
                </div>
            </div>
        )
    }

    return (
        <>{props.children}</>
    )
}
