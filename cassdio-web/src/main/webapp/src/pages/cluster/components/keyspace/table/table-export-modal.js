import {useEffect} from "react";
import {Modal} from "react-bootstrap";

const TableExportModal = (props) => {

    const show = props.show;
    const handleClose = props.handleClose;

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
                    <button className={"btn btn-sm btn-outline-secondary"} onClick={handleClose}>
                        Close
                    </button>
                </Modal.Footer>
            </Modal>
        </>
    )
}

export default TableExportModal;
