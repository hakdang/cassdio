import {useEffect} from "react";

import {Modal} from "react-bootstrap";
import Spinner from "components/spinner";
import useCluster from "./hooks/useCluster";

const ClusterManageModal = (props) => {
    const {
        doGetClusterList,
        removeClusterId,
        doSaveCluster,
        doGetCluster,
        clusters,
        clusterDetailLoading,
        clusterInfo,
        setClusterInfo,
        saveLoading,
    } = useCluster();

    const show = props.show;
    const handleClose = props.handleClose;
    const clusterId = props.clusterId || null;

    useEffect(() => {
        //show component

        if (clusterId) {
            doGetCluster(clusterId);
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
                    doSaveCluster(clusterId, handleClose)
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
