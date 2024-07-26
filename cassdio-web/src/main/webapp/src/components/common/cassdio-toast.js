import {useEffect} from "react";
import {Bounce, ToastContainer} from "react-toastify";

import 'react-toastify/dist/ReactToastify.css';

export default function CassdioToast(props) {

    useEffect(() => {
        //show component

        return () => {
            //hide component

        };
    }, []);

    return (
        <ToastContainer
            position="top-right"
            autoClose={3000}
            hideProgressBar={false}
            newestOnTop={false}
            closeOnClick
            rtl={false}
            pauseOnFocusLoss
            draggable
            pauseOnHover
            theme="colored"
            transition={Bounce}
        />
    )
}
