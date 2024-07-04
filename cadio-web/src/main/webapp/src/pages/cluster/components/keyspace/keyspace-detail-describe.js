const KeyspaceDetailDescribe = ({describe}) => {

    if(!describe){
        return (<></>)
    }

    return (
        <>
            <div
                className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 ">
                <h4 className="h4">Describe</h4>
                {/*<div className="btn-toolbar mb-2 mb-md-0">*/}
                {/*    <div className="btn-group me-2">*/}
                {/*        <button type="button" className="btn btn-sm btn-outline-secondary">Share</button>*/}
                {/*        <button type="button" className="btn btn-sm btn-outline-secondary">Export</button>*/}
                {/*    </div>*/}
                {/*    <button type="button"*/}
                {/*            className="btn btn-sm btn-outline-secondary dropdown-toggle d-flex align-items-center gap-1">*/}
                {/*        This week*/}
                {/*    </button>*/}
                {/*</div>*/}
            </div>

            <code style={{whiteSpace: "pre"}}>
                {describe}
            </code>
        </>
    )
}

export default KeyspaceDetailDescribe;
