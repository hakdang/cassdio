import {useEffect, useState} from "react";
import axios from "axios";
import {axiosCatch} from "../../../../../utils/axiosUtils";
import {Modal} from "react-bootstrap";

const TableDetailModal = (props) => {

    const show = props.show;
    const handleClose = props.handleClose;

    const clusterId = props.clusterId;
    const keyspaceName = props.keyspaceName;
    const tableName = props.tableName;

    const [tableLoading, setTableLoading] = useState(false);
    const [tableInfo, setTableInfo] = useState({
        table: {},
        describe: {},
        columns: [],
    })

    const renderData = (data) => {
        if (typeof data === "object") {
            if (Array.isArray(data)) {
                return data.join(',');
            }

            return JSON.stringify(data);
        }

        return data;
    }

    useEffect(() => {
        //show component

        if (!tableName) {
            return;
        }

        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${clusterId}/keyspace/${keyspaceName}/table/${tableName}`,
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
            axiosCatch(error)
        }).finally(() => {
            setTableLoading(false)
        });
        return () => {
            //hide component

        };
    }, [tableName]);

    return (
        <Modal show={show} size={"xl"} onHide={handleClose}>
            <Modal.Header closeButton>
                <Modal.Title>Table Detail</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {
                    tableLoading ? <>
                        <div className="d-flex justify-content-center">
                            <div className="spinner-border" role="status">
                                <span className="visually-hidden">Loading...</span>
                            </div>
                        </div>
                    </> : <>
                        <div className={"row mb-3"}>
                            <div className={"col"}>
                                <div className="card border-0">
                                    <div className={"card-body"}>
                                        {
                                            Object.keys(tableInfo.table).map((info, infoIndex) => {
                                                return (
                                                    <p key={infoIndex}>
                                                        <b>{info}</b>
                                                        :
                                                        {
                                                            renderData(tableInfo.table[info])
                                                        }

                                                    </p>
                                                )
                                            })
                                        }

                                        {/*{JSON.stringify(tableInfo.table)};*/}
                                    </div>
                                    {/*<div className="card-body">*/}
                                    {/*    <h5 className="card-title"><b>TableName : </b> {tableInfo.table.tableName}</h5>*/}
                                    {/*    <h6 className="card-subtitle mb-2 text-body-secondary">*/}
                                    {/*        ID : {tableInfo.table.id}*/}
                                    {/*    </h6>*/}
                                    {/*    <p className="card-text">*/}
                                    {/*        <b>Comment : </b> {tableInfo.table.comment}*/}
                                    {/*    </p>*/}
                                    {/*</div>*/}

                                    {/*<h6 className="text-body-secondary">*/}
                                    {/*    Options*/}
                                    {/*</h6>*/}

                                    <div className={"row"}>
                                        {/*{*/}
                                        {/*    splitTableOptionList().map((keyGroups, keyGroupsIndex) => {*/}
                                        {/*        return (*/}
                                        {/*            <div className={"col-lg-6 col-sm-12"} key={keyGroupsIndex}>*/}
                                        {/*                <ul className="list-group list-group-flush">*/}
                                        {/*                    {*/}
                                        {/*                        keyGroups.map((key, keyIndex) => {*/}
                                        {/*                            return (*/}
                                        {/*                                <li className="list-group-item">*/}
                                        {/*                                    <b>{key}</b> {JSON.stringify(tableInfo.table.options[key])}*/}
                                        {/*                                </li>*/}
                                        {/*                            )*/}
                                        {/*                        })*/}
                                        {/*                    }*/}
                                        {/*                </ul>*/}
                                        {/*            </div>*/}
                                        {/*        )*/}
                                        {/*    })*/}
                                        {/*}*/}
                                    </div>

                                </div>
                            </div>

                        </div>
                        <div className={"row mb-3"}>
                            <div className={"col"}>
                                <h5 className={"h5"}> Describe</h5>
                                <code className={"w-100 text-break"} style={{whiteSpace: "pre"}}>
                                    {tableInfo.describe.create_statement}
                                </code>
                            </div>
                        </div>
                        <div className={"row mb-3"}>
                            <div className={"col"}>
                                <h5 className={"h5"}> Columns</h5>

                                <div className="table-responsive small">
                                    <table className="table table-sm">
                                        <thead>
                                        <tr>
                                            <th className={"text-center"} scope="col">Column Name</th>
                                            <th className={"text-center"} scope="col">Kind</th>
                                            <th className={"text-center"} scope="col">Clustering Order</th>
                                            <th className={"text-center"} scope="col">DataType</th>
                                        </tr>
                                        </thead>
                                        <tbody className="table-group-divider">
                                        {/*{*/}
                                        {/*    tableInfo.columns.map((info, infoIndex) => {*/}
                                        {/*        return (*/}
                                        {/*            <tr className={`*/}
                                        {/*            ${info.kind === 'PARTITION_KEY' && 'table-danger '}*/}
                                        {/*            ${info.kind === 'CLUSTERING' && 'table-warning '}*/}
                                        {/*            `}*/}
                                        {/*                key={infoIndex}>*/}
                                        {/*                <th className={"text-center"}>*/}
                                        {/*                    {info.name}*/}
                                        {/*                </th>*/}
                                        {/*                <td className={"text-center"}>*/}
                                        {/*                    {info.kind}*/}
                                        {/*                </td>*/}
                                        {/*                <td className={"text-center"}>*/}
                                        {/*                    {info.clusteringOrder}*/}
                                        {/*                </td>*/}
                                        {/*                <td className={"text-center"}>*/}
                                        {/*                    {info.dataType}*/}
                                        {/*                </td>*/}
                                        {/*            </tr>*/}
                                        {/*        )*/}
                                        {/*    })*/}
                                        {/*}*/}
                                        </tbody>
                                    </table>
                                </div>

                            </div>
                        </div>


                    </>
                }
            </Modal.Body>
            <Modal.Footer>
                <button className={"btn btn-sm btn-outline-secondary"} onClick={handleClose}>

                </button>
            </Modal.Footer>
        </Modal>
    )
}

export default TableDetailModal;
