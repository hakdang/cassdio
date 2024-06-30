import {useParams} from "react-router-dom";
import {useEffect} from "react";

const WorkspaceHome = () => {
    const routeParams = useParams();

    useEffect(() => {
        //show component

        console.log("routeParams ", routeParams.workspaceId)

        return () => {
            //hide component


        };
    }, []);

    return (
        <>
            TEST
        </>
    )
}

export default WorkspaceHome;