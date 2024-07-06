import {useEffect} from "react";
import {Modal} from "react-bootstrap";

const TableDataManageModal = (props) => {

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
                    <Modal.Title>Table Data Manage(New or Modify)</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    데이터 신규 추가 및 수정
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

export default TableDataManageModal;
