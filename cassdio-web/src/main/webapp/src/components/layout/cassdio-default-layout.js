import {Outlet} from "react-router-dom";

import CassdioHeader from "./cassdio-header";
import CassdioToast from "../common/cassdio-toast";

const CassdioDefaultLayout = ({}) => {
    return <div className={"min-vh-100"}>
        <CassdioHeader/>

        <Outlet/>

        <CassdioToast/>
    </div>
}

export default CassdioDefaultLayout;
