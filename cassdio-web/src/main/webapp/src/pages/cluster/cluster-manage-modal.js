import {useEffect, useState} from "react";

import {Modal} from "react-bootstrap";

import {toast} from "react-toastify";

import axios from "axios";
import useCassdio from "commons/hooks/useCassdio";
import Spinner from "components/spinner";

const ClusterManageModal = (props) => {
    const {errorCatch} = useCassdio();

    const show = props.show;
    const handleClose = props.handleClose;
    const getClusterList = props.getClusterList;
    const clusterId = props.clusterId || null;
    const [clusterDetailLoading, setClusterDetailLoading] = useState(false);

    const [clusterInfo, setClusterInfo] = useState(
        {
            clusterId: null,
            contactPoints: "",
            port: 9042,
            localDatacenter: "",
            clusterAuthCredentials: false,
            username: "",
            password: "",
        }
        // {
        //     contactPoints: "",
        //     port: 0,
        //     localDatacenter: "",
        //     clusterAuthCredentials: false,
        //     username: "",
        //     password: "",
        // }
    );

    const [saveLoading, setSaveLoading] = useState(false);
    const save = () => {
        if (!clusterInfo.contactPoints) {
            toast.warn("contactPoints 를 입력해주세요.");
            return;
        }

        if (!clusterInfo.port || clusterInfo.port === 0) {
            toast.warn("clusterPort 를 입력해주세요.");
            return;
        }

        if (!clusterInfo.localDatacenter) {
            toast.warn("localDatacenter 를 입력해주세요.");
            return;
        }

        if (clusterInfo.clusterAuthCredentials) {
            if (!clusterInfo.username) {
                toast.warn("username 를 입력해주세요.");
                return;
            }

            if (!clusterInfo.password) {
                toast.warn("password 를 입력해주세요.");
                return;
            }
        }

        setSaveLoading(true);

        let method = "POST"
        let url = "/api/cassandra/cluster";
        if (clusterId) {
            method = "PUT";
            url = `/api/cassandra/cluster/${clusterId}`;
        }

        axios({
            method: method,
            url: url,
            data: {
                contactPoints: clusterInfo.contactPoints,
                port: clusterInfo.port,
                localDatacenter: clusterInfo.localDatacenter,
                username: clusterInfo.username,
                password: clusterInfo.password,
            },
        }).then((response) => {
            toast.info("등록 완료.");
            getClusterList();
            handleClose();
        }).catch((error) => {
            errorCatch(error);
        }).finally(() => {
            setSaveLoading(false);
        })
    }

    const getCluster = () => {
        setClusterDetailLoading(true)

        axios({
            method: "GET",
            url: `/api/cassandra/cluster/${clusterId}`,
            params: {}
        }).then((response) => {
            setClusterInfo(response.data.result.cluster)
        }).catch((error) => {
            errorCatch(error)
        }).finally(() => {
            setClusterDetailLoading(false)
        });
    }

    useEffect(() => {
        //show component

        if (clusterId) {
            getCluster();
        }

        return () => {
            //hide component
        };
    }, []);

    return (
        <Modal show={show} size={"xl"} onHide={handleClose}>
            <Modal.Header closeButton>
                <Modal.Title>Cluster Manage</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Spinner loading={clusterDetailLoading}>
                    <div className={"row"}>
                        <div className="col-12 mb-2">
                            <label htmlFor="contactPoints" className="form-label">Contact Points</label>
                            <div className="input-group">
                                <input type="text" className="form-control" id="username" placeholder="contactPoints"
                                       value={clusterInfo.contactPoints || ''}
                                       onChange={evt => setClusterInfo(t => {
                                           return {...t, contactPoints: evt.target.value}
                                       })}/>
                            </div>
                        </div>
                        <div className="col-12 mb-2">
                            <label htmlFor="clusterPort" className="form-label">Cluster Port</label>
                            <div className="input-group">
                                <input type="number" className="form-control" id="clusterPort" placeholder=""
                                       value={clusterInfo.port || 0}
                                       onChange={evt => setClusterInfo(t => {
                                           return {...t, port: evt.target.value}
                                       })}/>
                            </div>
                        </div>
                        <div className="col-12 mb-2">
                            <label htmlFor="clusterLocalDataCenter" className="form-label">Local Datacenter</label>
                            <div className="input-group">
                                <input type="text" className="form-control" id="clusterLocalDataCenter"
                                       placeholder="Local Datacenter"
                                       value={clusterInfo.localDatacenter || ''}
                                       onChange={evt => setClusterInfo(t => {
                                           return {...t, localDatacenter: evt.target.value}
                                       })}/>
                            </div>
                        </div>

                        <hr className="my-4"/>

                        <div className={"col-12 mb-2"}>
                            <div className="form-check mb-3">
                                <input type="checkbox" className="form-check-input" id="clusterAuthCredentials"
                                       checked={clusterInfo.clusterAuthCredentials}
                                       onChange={evt => setClusterInfo(t => {
                                           return {...t, clusterAuthCredentials: !t.clusterAuthCredentials}
                                       })}
                                />
                                <label className="form-check-label" htmlFor="clusterAuthCredentials">
                                    Enable AuthCredentials
                                </label>
                            </div>
                        </div>

                        {
                            clusterInfo.clusterAuthCredentials &&
                            <>
                                <div className="col-12 mb-2">
                                    <label htmlFor="clusterUsername" className="form-label">
                                        Username
                                    </label>
                                    <div className="input-group">
                                        <input type="text" className="form-control" id="clusterUsername"
                                               placeholder="UserName"
                                               value={clusterInfo.username || ''}
                                               onChange={evt => setClusterInfo(t => {
                                                   return {...t, username: evt.target.value}
                                               })}
                                        />
                                    </div>
                                </div>
                                <div className="col-12 mb-2">
                                    <label htmlFor="clusterPassword" className="form-label">
                                        Password
                                    </label>
                                    <div className="input-group">
                                        <input type="password" className="form-control" id="clusterPassword"
                                               placeholder="Cluster Password"
                                               value={clusterInfo.password || ''}
                                               onChange={evt => setClusterInfo(t => {
                                                   return {...t, password: evt.target.value}
                                               })}
                                        />
                                    </div>
                                </div>

                            </>
                        }
                    </div>
                </Spinner>

            </Modal.Body>
            <Modal.Footer>
                <button className={"btn btn-sm btn-outline-primary"} onClick={e => {
                    save()
                }}>
                    Save
                    {
                        saveLoading &&
                        <div className="ms-2 spinner-border spinner-border-sm" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </div>
                    }
                </button>
                <button className={"btn btn-sm btn-outline-secondary"} onClick={handleClose}>
                    Close
                </button>
            </Modal.Footer>
        </Modal>
    )
}

export default ClusterManageModal;
