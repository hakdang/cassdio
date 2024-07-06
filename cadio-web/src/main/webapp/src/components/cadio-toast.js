import {Toast, ToastContainer} from "react-bootstrap";
import useCadio from "../commons/hooks/useCadio";
import {useCadioDispatch, useCadioState} from "../commons/context/cadioContext";
import {useEffect} from "react";

export default function CadioToast(props) {
    const cadioDispatcher = useCadioDispatch();
    const {
        openToast
    } = useCadio();
    const {
        toasts,
    } = useCadioState();

    const setToast = (toastIndex) => {
        cadioDispatcher({
            type: "REMOVE_TOAST_ITEM",
            toastIndex : toastIndex
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
            position={"top-end"}
        >
            {
                toasts && toasts.map((toast, toastIndex) => {
                    if (!toast) {
                        return (<></>)
                    }

                    return (
                        <Toast
                            key={toastIndex}
                            autohide={true}
                            delay={toast.delay || 3000}
                            onClose={() => {
                                setToast();
                            }}
                        >
                            <Toast.Header>
                                <strong className="me-auto">Bootstrap</strong>
                                <small className="text-muted">just now</small>
                            </Toast.Header>
                            <Toast.Body>{toast.message || 'tempty'}</Toast.Body>
                        </Toast>
                    )
                })
            }
        </ToastContainer>
    )
}
