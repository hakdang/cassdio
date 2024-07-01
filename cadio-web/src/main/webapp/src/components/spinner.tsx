export default function Spinner(props: any) {
    const loading = props.loading || false;

    if (loading) {
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
