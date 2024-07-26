import React, {useEffect} from "react";
import {RouterProvider} from "react-router-dom";
import {CassdioProvider} from "./context/cassdioContext";
import router from "./routers/router";
import './App.css'

function App() {

    useEffect(() => {
        //show component

        // return () => {
        //     //hide component
        //
        // };
    }, []);

    return (
        <CassdioProvider>
            <RouterProvider router={router}/>
        </CassdioProvider>
    )
}

export default App;
