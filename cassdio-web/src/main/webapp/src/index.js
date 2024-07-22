import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

import "bootstrap"
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css'

import {ClusterProvider} from "pages/cluster/context/clusterContext";
import {CassdioProvider} from "commons/context/cassdioContext";

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <CassdioProvider>
        <ClusterProvider>
            <App/>
        </ClusterProvider>
    </CassdioProvider>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
//reportWebVitals();
