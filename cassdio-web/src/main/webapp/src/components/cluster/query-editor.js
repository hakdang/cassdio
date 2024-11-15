import {useRef, useState} from "react";

import AceEditor from "react-ace";
import "ace-builds/src-noconflict/ext-language_tools";
import "ace-builds/src-noconflict/mode-sql";
import "ace-builds/src-noconflict/theme-sqlserver"

import {OverlayTrigger, Tooltip} from "react-bootstrap";
import {toast} from "react-toastify";

import {useCassdioState} from "context/cassdioContext";

const QueryEditor = ({queryOptions, setQueryOptions, queryExecute}) => {

    const [queryLoading, setQueryLoading] = useState(false)
    const [query, setQuery] = useState("")

    const [selectedQuery, setSelectedQuery] = useState('');

    const {
        consistencyLevels,
        defaultConsistencyLevel,
    } = useCassdioState();

    const editorRef = useRef();

    const editorQueryExecute = (cursor) => {
        if (selectedQuery) {
            queryExecute(selectedQuery, cursor, setQueryLoading);
        } else {
            queryExecute(query, cursor, setQueryLoading);
        }
    }

    const commands = [{
        name: 'saveFile',
        bindKey: {win: 'Ctrl-S', mac: 'Command-S'},
        exec: (editor) => {
            toast.info('File saved');
            // Implement save logic here
        }
    }, {
        name: 'commandExecute',
        bindKey: {win: 'Ctrl-Enter', mac: 'Command-Enter'},
        exec: (editor) => {
            const tempQuery = editor.getSelectedText();
            if (tempQuery) {
                queryExecute(tempQuery, null, setQueryLoading);
            } else {
                queryExecute(editor.getValue(), null, setQueryLoading);
            }
        }
    }];

    function onSelectionChange(value, event) {
        // use content
        setSelectedQuery(editorRef.current.editor.getSelectedText());
    }

    function onLoad(newValue) {
        //console.log("load", newValue);
    }

    function onChange(newValue) {
        //console.log("change", newValue);
        setQuery(newValue)
    }

    return (
        <>
            <div className="btn-toolbar pb-1" role="toolbar" aria-label="Toolbar with button groups">
                <div className="btn-group btn-group-sm me-1 " role="group" aria-label="Third group">
                    <button type="button" className="btn btm-sm btn-danger" onClick={() => editorQueryExecute(null)}>
                        {
                            queryLoading ?
                                <div className="spinner-border spinner-border-sm" role="status">
                                    <span className="visually-hidden">Loading...</span>
                                </div> : <i className="bi bi-play-fill"></i>
                        }
                    </button>
                </div>
                <div className="btn-group btn-group-sm me-1" role="group" aria-label="First group">
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

                {/*<div className="btn-group btn-group-sm me-1" role="group" aria-label="First group">*/}
                {/*    <OverlayTrigger placement="top" overlay={*/}
                {/*        <Tooltip id="tooltip">*/}
                {/*            Query Save*/}
                {/*        </Tooltip>*/}
                {/*    }>*/}
                {/*        <button className="btn btn-sm btn-outline-primary" type="button"*/}
                {/*                onClick={e => alert("쿼리 저장하기 옵션 구현 필요.")}>*/}
                {/*            <i className="bi bi-floppy2-fill"></i>*/}
                {/*        </button>*/}
                {/*    </OverlayTrigger>*/}
                {/*    <OverlayTrigger placement="top" overlay={*/}
                {/*        <Tooltip id="tooltip">*/}
                {/*            Query Auto Save <br/>(per 30 sec)*/}
                {/*        </Tooltip>*/}
                {/*    }>*/}
                {/*        <button className="btn btn-sm btn-outline-primary" type="button"*/}
                {/*                onClick={e => alert("자동저장 옵션 구현 필요(query option 에 대한 localstorage 필요)")}>*/}
                {/*            <i className="bi bi-clock"></i>*/}
                {/*        </button>*/}
                {/*    </OverlayTrigger>*/}
                {/*    <OverlayTrigger placement="top" overlay={*/}
                {/*        <Tooltip id="tooltip">*/}
                {/*            Query Editor Clean*/}
                {/*        </Tooltip>*/}
                {/*    }>*/}
                {/*        <button className="btn btn-sm btn-outline-primary" type="button"*/}
                {/*                onClick={e => alert("에디터 비우기")}>*/}
                {/*            <i className="bi bi-eraser-fill"></i>*/}
                {/*        </button>*/}
                {/*    </OverlayTrigger>*/}
                {/*</div>*/}

                <div className="btn-group btn-group-sm me-2" role="group" aria-label="First group">
                    <OverlayTrigger placement="top" overlay={
                        <Tooltip id="tooltip">
                            Query Command Options
                        </Tooltip>
                    }>
                        <button className="btn btn-sm btn-outline-secondary" type="button" data-bs-toggle="collapse"
                                data-bs-target="#queryOptionCollapse" aria-expanded="false"
                                aria-controls="queryOptionCollapse">
                            <i className="bi bi-gear-wide-connected"></i>
                        </button>
                    </OverlayTrigger>
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
                                <select className="form-select form-select-sm" id="queryLimitSelect"
                                        onChange={e => {
                                            setQueryOptions(t => {
                                                return {...t, limit: parseInt(e.target.value)}
                                            })
                                        }
                                        } value={queryOptions.limit || "10"}>
                                    <option value="10">10</option>
                                    <option value="50">50</option>
                                    <option value="100">100</option>
                                    <option value="500">500</option>
                                </select>
                                <label htmlFor="queryLimitSelect">Limit</label>
                            </div>
                        </div>
                        <div className="col">
                            <div className="form-floating">
                                <select className="form-select form-select-sm"
                                        id="consistencyLevelSelect"
                                        value={queryOptions.consistencyLevelProtocolCode || defaultConsistencyLevel.protocolCode || 10} //LOCAL_ONE
                                        onChange={e => {
                                            setQueryOptions(t => {
                                                return {...t, consistencyLevelProtocolCode: parseInt(e.target.value)}
                                            })
                                        }}
                                >
                                    {
                                        consistencyLevels.map((info, infoIndex) => {
                                            return (
                                                <option key={`consistencyLevel${infoIndex}`} value={info.protocolCode}>{info.name}</option>
                                            )
                                        })
                                    }
                                </select>
                                <label htmlFor="consistencyLevelSelect">ConsistencyLevel</label>
                            </div>
                        </div>
                        <div className="col">
                            <div className="form-floating mb-3">
                                <input type="number" className="form-control form-control-sm" id="queryTimeoutSec"
                                       placeholder="Query Timeout Sec"
                                       value={queryOptions.timeoutSeconds || 0}
                                       onChange={
                                           e => {
                                               setQueryOptions(t => {
                                                   return {...t, timeoutSeconds: e.target.value}
                                               })
                                           }
                                       }
                                />
                                <label htmlFor="queryTimeoutSec">Query Timeout</label>
                            </div>
                        </div>
                    </div>

                    <div className="row g-3 mt-1">
                        <div className="col">
                            <div className="form-check form-switch">
                                <input className="form-check-input" type="checkbox" role="switch"
                                       id="tracingOnSwitch"
                                       checked={queryOptions.trace}
                                       onChange={
                                           e => {
                                               setQueryOptions(t => {
                                                   return {...t, trace: !queryOptions.trace}
                                               })
                                           }
                                       }
                                />
                                <label className="form-check-label" htmlFor="tracingOnSwitch">
                                    Tracing On
                                </label>
                            </div>
                        </div>
                        <div className="col">

                        </div>
                    </div>

                </div>
            </div>

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
                commands={commands}
            />
        </>
    )
}

export default QueryEditor;
