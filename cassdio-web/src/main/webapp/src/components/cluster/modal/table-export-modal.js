import {useEffect} from "react";
import {Modal} from "react-bootstrap";
import axios from "axios";

const TableExportModal = ({show, handleClose, clusterId, keyspaceName, tableName}) => {

    const exporterDownload = async () => {
        const config = {
            method: "POST",
            url: `/api/cassandra/cluster/${clusterId}/keyspace/${keyspaceName}/table/${tableName}/row/export`,
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
            <Modal show={show} size={"xl"} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Data Exporter</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    Export
                </Modal.Body>
                <Modal.Footer>
                    <button className={"btn btn-sm btn-outline-danger"} onClick={exporterDownload}>
                        Exporter
                    </button>
                    <button className={"btn btn-sm btn-outline-secondary"} onClick={handleClose}>
                        Close
                    </button>
                </Modal.Footer>
            </Modal>
        </>
    )
}

export default TableExportModal;
