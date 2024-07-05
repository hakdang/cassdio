import {useRef,useState} from "react";

import AceEditor from "react-ace";
import "ace-builds/src-noconflict/ext-language_tools";
import "ace-builds/src-noconflict/mode-sql";
import "ace-builds/src-noconflict/theme-sqlserver"

const QueryEditor = (props) => {

    const [queryLoading, setQueryLoading] = useState(false)
    const [query, setQuery] = useState("SELECT * FROM testdb.test_table_1;")

    const editorRef = useRef();

    const queryExecute = (cursor) => {
        props.queryExecute(query, cursor, setQueryLoading);
    }

    function onSelectionChange(value, event) {
        const content = editorRef.current.editor.getSelectedText();
        console.log("content : ", content)
        // use content
    }
    function onLoad(newValue) {
        console.log("load", newValue);
    }

    function onChange(newValue) {
        console.log("change", newValue);
        setQuery(newValue)
    }

    return (
        <>
            <div className="btn-toolbar pb-1" role="toolbar" aria-label="Toolbar with button groups">
                <div className="btn-group btn-group-sm me-2 " role="group" aria-label="Third group">
                    <button type="button" className="btn btm-sm btn-danger" onClick={() => queryExecute(null)}>
                        {
                            queryLoading ?
                                <div className="spinner-border spinner-border-sm" role="status">
                                    <span className="visually-hidden">Loading...</span>
                                </div> : <i className="bi bi-play-fill"></i>
                        }
                    </button>
                </div>
                <div className="btn-group btn-group-sm me-2" role="group" aria-label="First group">
                    <button className="btn btn-sm btn-outline-secondary" type="button"
                            onClick={e => {
                                editorRef.current.editor.undo()
                            }}>
                        <i className="bi bi-arrow-left"></i>
                    </button>
                    <button className="btn btn-sm btn-outline-secondary" type="button"
                            onClick={e => {
                                editorRef.current.editor.redo()
                            }}>
                        <i className="bi bi-arrow-right"></i>
                    </button>
                </div>
                <div className="btn-group btn-group-sm me-2" role="group" aria-label="First group">
                    <button className="btn btn-sm btn-primary" type="button" data-bs-toggle="collapse"
                            data-bs-target="#queryOptionCollapse" aria-expanded="false"
                            aria-controls="queryOptionCollapse">
                        <i className="bi bi-gear-wide-connected"></i>
                    </button>
                </div>
                {/*<div className="btn-group btn-group-sm me-2" role="group" aria-label="First group">*/}
                {/*    <button type="button" className="btn btm-sm btn-primary">*/}
                {/*        <i className="bi bi-play-fill"></i>*/}
                {/*    </button>*/}
                {/*    <button type="button" className="btn btm-sm btn-primary">2</button>*/}
                {/*    <button type="button" className="btn btm-sm btn-primary">3</button>*/}
                {/*    <button type="button" className="btn btm-sm btn-primary">4</button>*/}
                {/*</div>*/}
                {/*<div className="btn-group btn-group-sm" role="group" aria-label="Second group">*/}
                {/*    <button type="button" className="btn btm-sm btn-secondary">5</button>*/}
                {/*    <button type="button" className="btn btm-sm btn-secondary">6</button>*/}
                {/*    <button type="button" className="btn btm-sm btn-secondary">7</button>*/}
                {/*</div>*/}
            </div>

            <div className="collapse" id="queryOptionCollapse">
                <div className="card card-body mt-1 mb-3">
                    <div className="row">
                        <div className="col">
                            <div className="form-floating">
                                <select className="form-select form-select-sm" id="floatingSelect">
                                    <option value="1">One</option>
                                    <option value="2">Two</option>
                                    <option value="3">Three</option>
                                </select>
                                <label htmlFor="floatingSelect">Limit</label>
                            </div>
                        </div>
                        <div className="col">
                            <div className="form-floating">
                                <select className="form-select form-select-sm" id="floatingSelect">
                                    <option value="1">One</option>
                                    <option value="2">Two</option>
                                    <option value="3">Three</option>
                                </select>
                                <label htmlFor="floatingSelect">ConsistencyLevel</label>
                            </div>
                        </div>
                        <div className="col">
                            <div className="form-floating mb-3">
                                <input type="email" className="form-control form-control-sm" id="floatingInput"
                                       placeholder="name@example.com"/>
                                <label htmlFor="floatingInput">Query Timeout</label>
                            </div>
                        </div>
                    </div>

                    <div className="row g-3">
                        <div className="col">
                            <div className="form-check form-switch">
                                <input className="form-check-input" type="checkbox" role="switch"
                                       id="flexSwitchCheckDefault"/>
                                <label className="form-check-label" htmlFor="flexSwitchCheckDefault">
                                    Tracing On
                                </label>
                            </div>
                        </div>
                        <div className="col">

                        </div>
                    </div>

                </div>
            </div>

            {/*<div className="form-floating">*/}
            {/*    /!*현재는 단일 쿼리만 실행 가능하도록*!/*/}
            {/*    <textarea className="form-control" placeholder="Query" id="queryEditor"*/}
            {/*              style={{"height": "300px"}}*/}
            {/*              value={query || ''}*/}
            {/*              rows={10}*/}
            {/*              onChange={evt => setQuery(evt.target.value)}*/}
            {/*    ></textarea>*/}
            {/*    <label htmlFor="queryEditor">Query</label>*/}
            {/*</div>*/}

            <AceEditor
                ref={editorRef}
                mode={"sql"}
                theme={"sqlserver"}
                onLoad={onLoad}
                onChange={onChange}
                editorProps={{$blockScrolling: true}}
                onSelectionChange={onSelectionChange}
                setOptions={{
                    enableBasicAutocompletion: true,
                    enableLiveAutocompletion: true,
                    enableSnippets: false,
                    showLineNumbers: true,
                    tabSize: 2,
                }}
                fontSize={12}
                lineHeight={19}
                showPrintMargin={true}
                showGutter={true}
                highlightActiveLine={true}
                value={query || ''}
                width={'100%'}
            />
        </>
    )
}

export default QueryEditor;
