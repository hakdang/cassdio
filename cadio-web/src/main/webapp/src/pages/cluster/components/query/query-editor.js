const QueryEditor = () => {

    return (
        <>
            <div className="form-floating">
                <textarea className="form-control" placeholder="Query" id="queryEditor"
                          style={{"height": "300px"}}></textarea>
                <label htmlFor="queryEditor">Query</label>
            </div>
        </>
    )
}

export default QueryEditor;
