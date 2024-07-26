import {Link} from "react-router-dom";

import CassdioSidebar from "../components/layout/cassdio-sidebar";

import ClusterList from "../components/cluster/cluster-list";
import {useEffect} from "react";
import {useCassdioDispatch, useCassdioState} from "../context/cassdioContext";
import CassdioHelper from "../utils/cassdioHelper";
import HomeSidebarLink from "../components/home-sidebar-link";

const CassdioHomePage = () => {

    useEffect(() => {
        //show component

        return () => {
            //hide component
        };
    }, []);


    return (
        <div className="container-fluid h-100 mb-5">
            <div className="row">

                <CassdioSidebar>
                    <HomeSidebarLink/>
                </CassdioSidebar>

                <main className="col-md-9 ms-sm-auto col-lg-10 px-md-4  mb-5">
                    <ClusterList/>
                </main>
            </div>
        </div>
    )
}

export default CassdioHomePage;
