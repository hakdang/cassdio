import {useEffect, useState} from "react";
import {OverlayTrigger, Tooltip} from "react-bootstrap";
import {toast} from "react-toastify";
import {Link} from "react-router-dom";

export default function DataRowItem(props) {
    const data = props.data;

    const [renderData, setRenderData] = useState();

    // function isJson(item) {
    //     let value = typeof item !== "string" ? JSON.stringify(item) : item;
    //     try {
    //         value = JSON.parse(value);
    //     } catch (e) {
    //         return false;
    //     }
    //
    //     return typeof value === "object" && value !== null;
    // }

    const handleCopyClipBoard = async (data) => {
        try {
            await navigator.clipboard.writeText(data);
            await toast.info("Copied")
        } catch (e) {
            toast.error('Fail Copy');
        }
    };

    useEffect(() => {
        //show component

        // if (isJson(data)) {
        //     console.log("JSON Viewer : ", typeof data, data)
        // } else {
        //     console.log("Default Viewer : ", typeof data, data)
        // }

        if (data && typeof data === 'string' && data.length >= 25) {
            setRenderData(data.substring(0, 24) + "...")
        } else {
            setRenderData(data)
        }

        return () => {
            //hide component

        };
    }, [data]);

    if (data === 'NULL') {
        return (
            ""
        )
    }

    return (
        <OverlayTrigger placement="top" overlay={
            <Tooltip id="tooltip">
                {data}
            </Tooltip>
        }>
            <Link to="{() => false}" role={"button"}
                  onClick={e => handleCopyClipBoard(data)}>
                {renderData}
            </Link>
        </OverlayTrigger>
    )
}
