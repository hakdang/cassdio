import {Modal, OverlayTrigger, Tooltip} from "react-bootstrap";
import {useEffect} from "react";
import dayjs from "dayjs";

const QueryTraceViewModal = ({
                                 show,
                                 handleClose,
                                 queryTrace
                             }) => {

    useEffect(() => {
        //show component

        return () => {
            //hide component

        };
    }, [show]);

    return (
        <Modal show={show} size={"xl"} onHide={handleClose}>
            <Modal.Header closeButton>
                <Modal.Title>Query Tracing</Modal.Title>
            </Modal.Header>
            <Modal.Body>

                <table className="table table-sm table-hover">
                    <tbody>
                    <tr>
                        <th className={"text-center"}>Tracing Id</th>
                        <td className={"text-center text-break"}>{queryTrace.tracingId}</td>
                        <th className={"text-center"}>Request Type</th>
                        <td className={"text-center text-break"}>{queryTrace.requestType}</td>
                    </tr>
                    <tr>
                        <th className={"text-center"}>Started At</th>
                        <td className={"text-center text-break"}>
                            <OverlayTrigger placement="bottom" overlay={
                                <Tooltip id="tooltip">
                                    Timestamp : {queryTrace.startedAt}
                                </Tooltip>
                            }>
                               <span> {dayjs(queryTrace.startedAt).format('YYYY-MM-DD HH:mm:ss')}</span>
                            </OverlayTrigger>
                        </td>
                        <th className={"text-center"}>Coordinator Address</th>
                        <td className={"text-center text-break"}>{queryTrace.coordinatorAddress}</td>
                        {/*<td>{JSON.stringify(queryTrace.parameters)}</td>*/}
                    </tr>
                    {Object.keys(queryTrace.parameters).map((paramKey, paramKeyIndex) => {
                        return (
                            <tr key={`param${paramKeyIndex}`}>
                                <th className={"text-center"}>{paramKey}</th>
                                <td className={"text-break"}
                                    colSpan={3}>{queryTrace.parameters[paramKey]}</td>
                            </tr>
                        )
                    })}
                    </tbody>
                </table>

                <h6 className={"h6"}>Events</h6>

                <div className="table-responsive small">
                    <table className="table table-sm table-fixed table-lock-height table-hover">
                        <thead>
                        <tr className={"table-dark"}>
                            <th className={"text-center"} scope="col">Activity</th>
                            <th className={"text-center"} scope="col">Timestamp</th>
                            <th className={"text-center"} scope="col">Source Address</th>
                            <th className={"text-center"} scope="col">Source Elapsed Micros</th>
                            <th className={"text-center"} scope="col">Thread Name</th>
                        </tr>
                        </thead>
                        <tbody>
                        {queryTrace.events.map((info, infoIndex) => {
                            return (
                                <tr key={`events${infoIndex}`}>
                                    <td className={"text-center text-break"}>{info.activity}</td>
                                    <td className={"text-center text-break"}>

                                        <OverlayTrigger placement="bottom" overlay={
                                            <Tooltip id="tooltip">
                                                Timestamp :  {info.timestamp}
                                            </Tooltip>
                                        }>
                                            <span> {dayjs(info.timestamp).format('YYYY-MM-DD HH:mm:ss')}</span>
                                        </OverlayTrigger>
                                    </td>
                                    <td className={"text-center text-break"}>{info.sourceAddress}</td>
                                    <td className={"text-center text-break"}>{info.sourceElapsedMicros}</td>
                                    <td className={"text-center text-break"}>{info.threadName}</td>
                                </tr>
                            )
                        })}
                        </tbody>
                    </table>
                </div>
            </Modal.Body>
            <Modal.Footer>
                <button className={"btn btn-sm btn-outline-secondary"} onClick={handleClose}>
                    Close
                </button>
            </Modal.Footer>
        </Modal>
    )
}

export default QueryTraceViewModal;
