import {useEffect, useState} from "react";
import axios from "axios";
import useCadio from "./hooks/useCadio";

const InitializeView = (props) => {
    const {doBootstrap} = useCadio();

    const [clusterInfo, setClusterInfo] = useState(
        {
            contactPoints: "",
            port: 0,
            clusterLocalDataCenter: "",
            clusterAuthCredentials: false,
            username: "",
            password: "",
        }
    );

    const [saveLoading, setSaveLoading] = useState(false);

    const save = () => {
        if (!clusterInfo.contactPoints) {
            alert("contactPoints 를 입력해주세요.");
            return;
        }

        if (!clusterInfo.port || clusterInfo.port === 0) {
            alert("clusterPort 를 입력해주세요.");
            return;
        }

        if (!clusterInfo.clusterLocalDataCenter) {
            alert("clusterLocalDataCenter 를 입력해주세요.");
            return;
        }

        if (clusterInfo.clusterAuthCredentials) {
            if (!clusterInfo.username) {
                alert("username 를 입력해주세요.");
                return;
            }

            if (!clusterInfo.password) {
                alert("password 를 입력해주세요.");
                return;
            }
        }

        setSaveLoading(true);

        axios({
            method: "POST",
            url: "/api/cassandra/cluster",
            data: {
                contactPoints: clusterInfo.contactPoints,
                port: clusterInfo.port,
                localDatacenter: clusterInfo.localDatacenter,
                username: clusterInfo.username,
                password: clusterInfo.password,
            },
        }).then((response) => {
            alert("등록되었습니다.");
            doBootstrap();
        }).catch((error) => {

        }).finally(() => {
            setSaveLoading(false);
        })
    }

    useEffect(() => {
        //show component

        return () => {
            //hide component

        };
    }, []);

    return (
        <div className={"container"}>
            <main>
                <div className="py-5 text-center">
                    <h2>Cadio Initialize View</h2>
                    <p className="lead">Cluster 정보 최소 한개를 등록해주세요.</p>
                </div>

                <div className="row g-5">
                    <div className="col-lg-8 col-xxl-6 my-5 mx-auto">
                        <h4 className="mb-3">Cluster</h4>

                        <div className="col-12 mb-2">
                            <label htmlFor="contactPoints" className="form-label">ContactPoints</label>
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
                                       value={clusterInfo.clusterLocalDataCenter || ''}
                                       onChange={evt => setClusterInfo(t => {
                                           return {...t, clusterLocalDataCenter: evt.target.value}
                                       })}/>
                            </div>
                        </div>

                        <hr className="my-4"/>

                        <div className="form-check mb-3">
                            <input type="checkbox" className="form-check-input" id="clusterAuthCredentials"
                                   checked={clusterInfo.clusterAuthCredentials}
                                   onChange={evt => setClusterInfo(t => {
                                       return {...t, clusterAuthCredentials: !t.clusterAuthCredentials}
                                   })}
                            />
                            <label className="form-check-label" htmlFor="clusterAuthCredentials">
                                Enable
                                AuthCredentials</label>
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
                                               placeholder="cluster Password"
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
                </div>

                <div className={"row"}>
                    <div className="col-lg-6 col-xxl-4  mx-auto">
                        <div className="d-grid gap-2">
                            {/*<button className="btn btn-danger" type="button">Validation</button>*/}
                            <button className="btn btn-primary" type="button" onClick={save}>Save</button>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    )
}

export default InitializeView;
