import {useEffect} from "react";
import {Modal, OverlayTrigger, Tooltip} from "react-bootstrap";
import {toast} from "react-toastify";

const TableRowDetailModal = ({show, handleClose, rowDetailView, convertedRowHeader}) => {

    const handleCopyClipBoard = async (data) => {
        try {
            await navigator.clipboard.writeText(data);
            await toast.info("Copied")
        } catch (e) {
            toast.error('Fail Copy');
        }
    };

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
                                    <tr key={`row${infoIndex}`}>
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
                                            <button
                                                className={"btn btn-sm btn-outline-secondary"}
                                                onClick={e => handleCopyClipBoard(rowDetailView[info.column_name])}>
                                                <i className="bi bi-clipboard"></i>
                                            </button>
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
