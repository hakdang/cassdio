import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import axios from "axios";
import {axiosCatch} from "../../../../../utils/axiosUtils";

const TableInformation = () => {

    const routeParams = useParams();

    const [tableLoading, setTableLoading] = useState(false);

    const [tableInfo, setTableInfo] = useState({
        table: {},
        describe: {},
        columns: [],
    })

    const splitTableOptionList = (options) => {
        if (!options || options.length <= 0) {
            return [];
        }
        const allOptionKeys = Object.keys(options);
        const half = Math.ceil(allOptionKeys.length / 2);
        const result = [];
        result.push(allOptionKeys.splice(0, half))
        result.push(allOptionKeys.splice(-half));

        return result;
    }


    useEffect(() => {
        //show component

        console.log("routeParams ", routeParams.clusterId)
        console.log("routeParams ", routeParams.keyspaceName)
        console.log("routeParams ", routeParams.tableName)
        setTableLoading(true);

        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${routeParams.clusterId}/keyspace/${routeParams.keyspaceName}/table/${routeParams.tableName}`,
            params: {
                withTableDescribe: true,
            }
        }).then((response) => {

            setTableInfo({
                table: response.data.result.table,
                describe: response.data.result.describe,
                columns: response.data.result.columns,
            })

        }).catch((error) => {
            console.log(error)
            //TODO : error catch
            axiosCatch(error)
        }).finally(() => {
            setTableLoading(false)
        });

        return () => {
            //hide component

        };
    }, []);

    return (
        <div className={"row mt-3"}>
            <div className={"col"}>



            </div>
        </div>
    )
}

export default TableInformation;
