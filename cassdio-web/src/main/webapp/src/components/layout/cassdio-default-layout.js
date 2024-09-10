import {Outlet, useLoaderData} from "react-router-dom";

import CassdioHeader from "./cassdio-header";
import CassdioToast from "components/common/cassdio-toast";
import {useEffect} from "react";
import {useCassdioDispatch} from "../../context/cassdioContext";

const CassdioDefaultLayout = () => {
    const dispatch = useCassdioDispatch();
    // const {
    //     consistencyLevels,
    //     defaultConsistencyLevel,
    // } = useCassdioState();
    const {bootstrap} = useLoaderData();

    useEffect(() => {

        if (bootstrap) {
            dispatch({
                type: "SET_BOOTSTRAP",
                consistencyLevels: bootstrap.consistencyLevels,
                defaultConsistencyLevel: bootstrap.defaultConsistencyLevel,
            });
        }


        return () => {
        };
    }, [bootstrap, dispatch]);

    return <div className={"min-vh-100"}>
        <CassdioHeader/>

        <Outlet/>

        <CassdioToast/>
    </div>
}

export default CassdioDefaultLayout;
