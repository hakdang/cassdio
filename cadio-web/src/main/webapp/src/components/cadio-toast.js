import {Toast, ToastContainer} from "react-bootstrap";
import {useCadioDispatch, useCadioState} from "../commons/context/cadioContext";
import {useEffect} from "react";
import dayjs from "dayjs";

export default function CadioToast(props) {
    const cadioDispatcher = useCadioDispatch();
    // const {
    //     openToast
    // } = useCadio();
    const {
        toasts,
    } = useCadioState();

    const removeToast = (id) => {
        cadioDispatcher({
            type: "REMOVE_TOAST_ITEM",
            id: id
        })
    }

    useEffect(() => {
        //show component

        return () => {
            //hide component

        };
    }, []);

    return (
        <ToastContainer
            className={"p-3"}
            style={{ zIndex: 99999 }}
            position={"top-end"}
        >
            {
                toasts && toasts.map((toast, toastIndex) => {
                    if (!toast) {
                        return (<></>)
                    }

                    return (
                        <Toast
                            className={"text-bg-primary align-items-center "}
                            key={toastIndex}
                            autohide={true}
                            delay={toast.delay || 3000}
                            onClose={() => {
                                removeToast(toast.id);
                            }}
                        >
                            <div className="d-flex">

                                {/*<Toast.Header>*/}
                                {/*    <strong className="me-auto">Cadio</strong>*/}
                                {/*    <small className="text-muted">test</small>*/}
                                {/*</Toast.Header>*/}
                                <Toast.Body>{toast.message || '내용이 비어 있습니다.'}</Toast.Body>
                                <button type="button" className="btn-close btn-close-white me-2 m-auto"
                                        data-bs-dismiss="toast" aria-label="Close" onClose={() => {
                                    removeToast(toast.id);
                                }}></button>
                            </div>
                        </Toast>
                    )
                })
            }
        </ToastContainer>
    )
}
