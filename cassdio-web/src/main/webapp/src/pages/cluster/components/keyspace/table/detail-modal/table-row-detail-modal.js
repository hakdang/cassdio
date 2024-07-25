import {useEffect} from "react";
import {Modal, OverlayTrigger, Tooltip} from "react-bootstrap";

const TableRowDetailModal = ({show, handleClose, rowDetailView, convertedRowHeader}) => {

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
                    <Modal.Title>Row Detail</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <table className="table table-sm table-hover">
                        <tbody>
                        {
                            convertedRowHeader && convertedRowHeader.map((info, infoIndex) => {
                                return (
                                    <tr>
                                        <th className={"text-center"} key={`resultHeader${infoIndex}`}
                                            scope="col">
                                            <OverlayTrigger placement="bottom" overlay={
                                                <Tooltip id="tooltip">
                                                    {info.column_name} ({info.type})
                                                </Tooltip>
                                            }>
                                                <span style={{cursor: "pointer"}}>
                                                     {
                                                         info.kind === 'partition_key' &&
                                                         <i className="bi bi-p-square me-1"></i>
                                                     }
                                                    {
                                                        info.kind === 'clustering' &&
                                                        <i className="bi bi-c-square me-1"></i>
                                                    }
                                                    {info.column_name}
                                                </span>
                                            </OverlayTrigger>
                                        </th>
                                        <td className={"text-center text-break"} key={`resultItem${infoIndex}`}>
                                            {rowDetailView[info.column_name]}
                                        </td>
                                        <td>
                                            <a className={"btn btn-sm btn-outline-secondary"}>COPY</a>
                                        </td>
                                    </tr>
                                )
                            })
                        }
                        </tbody>
                    </table>
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

export default TableRowDetailModal;
