import {useEffect, useState} from "react";
import {Modal} from "react-bootstrap";
import axios from "axios";
import {toast} from "react-toastify";

const TableImportModal = (props) => {

    const show = props.show;
    const handleClose = props.handleClose;

    const importSampleDownload = async () => {
        const config = {
            method: "POST",
            url: `/api/cassandra/cluster/${props.clusterId}/keyspace/${props.keyspaceName}/table/${props.tableName}/row/import/sample`,
            responseType: "blob",
        };
        const response = await axios(config);
        const name = response.headers["content-disposition"]
            .split("filename=")[1]
            .replace(/"/g, "");
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute("download", name);
        link.style.cssText = "display:none";
        document.body.appendChild(link);
        link.click();
        link.remove();
    }

    const [files, setFiles] = useState([]);

    const handleFilesChange = (e) => {
        setFiles(Array.from(e.target.files));
    }

    const importFileUpload = (evt) => {
        evt.preventDefault();

        if (!files || files.length <= 0) {
            toast.warn('File Empty');
            return;
        }

        const formData = new FormData();

        formData.append('file', files[0])

        axios({
            method: 'post',
            url: `/api/cassandra/cluster/${props.clusterId}/keyspace/${props.keyspaceName}/table/${props.tableName}/row/import`,
            data: formData,
        })
            .then((result) => {
                console.log('요청성공')
                console.log(result)

            })
            .catch((error) => {
                console.log('요청실패')
                console.log(error)
            })

    }

    useEffect(() => {
        //show component

        return () => {
            //hide component

        };
    }, []);

    return (
        <>
            <Modal show={show} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>
                        Data Importer
                        <span className={"ms-3"}>
                            <button className={"btn btn-sm btn-outline-info"}
                                    onClick={importSampleDownload}>Sample
                            </button>
                        </span>
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <>
                        <div className={"row mb-3"}>
                            <div className={"col"}>
                                {/*<label htmlFor="formFileSm" className="form-label">Small file input*/}
                                {/*    example</label>*/}
                                <input className="form-control form-control-sm" type="file"
                                       onChange={handleFilesChange}/>
                            </div>
                        </div>

                        <div className={"row"}>
                            <div className={"col-2"}>
                                Option
                            </div>
                            <div className={"col-10"}>
                                <div className={"row mb-3"}>
                                    <div className={"col"}>
                                        <div className="form-floating">
                                            <select className="form-select form-select-sm" id="queryLimitSelect"
                                                // onChange={e => {
                                                //     setQueryOptions(t => {
                                                //         return {...t, limit: parseInt(e.target.value)}
                                                //     })
                                                // }
                                                // } value={queryOptions.limit || "10"}>
                                            >
                                                <option value="10">10</option>
                                                <option value="50">50</option>
                                                <option value="100">100</option>
                                            </select>
                                            <label htmlFor="queryLimitSelect">Per Commit Size</label>
                                        </div>
                                    </div>
                                    <div className={"col"}>
                                        <div className="form-floating">
                                            <select className="form-select form-select-sm" id="queryLimitSelect"
                                                // onChange={e => {
                                                //     setQueryOptions(t => {
                                                //         return {...t, limit: parseInt(e.target.value)}
                                                //     })
                                                // }
                                                // } value={queryOptions.limit || "10"}>
                                            >
                                                <option value="LOGGED">LOGGED</option>
                                                <option value="UNLOGGED">UNLOGGED</option>
                                                <option value="COUNTER">COUNTER</option>
                                            </select>
                                            <label htmlFor="queryLimitSelect">Batch Type</label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </>
                </Modal.Body>
                <Modal.Footer>
                    <button className={"btn btn-sm btn-outline-primary"} onClick={importFileUpload}>
                        Upload
                    </button>
                    <button className={"btn btn-sm btn-outline-secondary"} onClick={handleClose}>
                        Close
                    </button>
                </Modal.Footer>
            </Modal>
        </>
    )
}

export default TableImportModal;
