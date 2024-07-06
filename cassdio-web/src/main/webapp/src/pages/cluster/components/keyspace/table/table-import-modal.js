import {useEffect} from "react";
import {Modal} from "react-bootstrap";

const TableImportModal = (props) => {

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
                    <Modal.Title>데이터 추가</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    Import 기능
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
