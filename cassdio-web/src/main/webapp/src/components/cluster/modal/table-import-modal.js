import {useEffect} from "react";
import {Modal} from "react-bootstrap";
import axios from "axios";

const TableImportModal = (props) => {

    const show = props.show;
    const handleClose = props.handleClose;

    const importSampleDownload = async () => {
        const config = {
            method: "POST",
            url: `/api/cassandra/cluster/${props.clusterId}/keyspace/${props.keyspaceName}/table/${props.tableName}/import/sample`,
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
                    <Modal.Title>Data Importer</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <>
                        <div className={"row"}>
                            <div className={"col-4"}>
                                Sample
                            </div>
                            <div className={"col-8"}>
                                <button className={"btn btn-sm btn-outline-info"}
                                        onClick={importSampleDownload}>Download
                                </button>
                            </div>
                        </div>
                        <hr/>
                        <div className={"row"}>
                            <div className={"col-4"}>
                                Import
                            </div>
                            <div className={"col-8"}>
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

                                </div>

                                <div className={"row"}>
                                    <div className={"col"}>
                                        <button className={"btn btn-sm btn-outline-primary"}
                                                onClick={importSampleDownload}>Upload
                                        </button>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </>
                </Modal.Body>
                <Modal.Footer>
                    <button className={"btn btn-sm btn-outline-secondary"} onClick={handleClose}>
                        Close
                    </button>
                </Modal.Footer>
            </Modal>
        </>
    )
}

export default TableImportModal;
