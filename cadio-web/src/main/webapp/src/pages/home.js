import {Link} from "react-router-dom";

const Home = () => {

    return (
        <>
            <main className="col-12 px-md-4">
                <div
                    className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h2 className="h2">Cadio Dashboard</h2>
                    {/*<div className="btn-toolbar mb-2 mb-md-0">*/}
                    {/*    <div className="btn-group me-2">*/}
                    {/*        <Link className={"btn btn-sm btn-outline-secondary"}*/}
                    {/*              to={"/system"}>*/}
                    {/*            System*/}
                    {/*        </Link>*/}
                    {/*        /!*<button type="button" className="btn btn-sm btn-outline-secondary">Export</button>*!/*/}
                    {/*    </div>*/}
                    {/*    /!*<button type="button"*!/*/}
                    {/*    /!*        className="btn btn-sm btn-outline-secondary dropdown-toggle d-flex align-items-center gap-1">*!/*/}
                    {/*    /!*    This week*!/*/}
                    {/*    /!*</button>*!/*/}
                    {/*</div>*/}
                </div>

                <div className="table-responsive small">
                    <table className="table table-striped table-sm">
                        <thead>
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">Header</th>
                            <th scope="col">Header</th>
                            <th scope="col">Header</th>
                            <th scope="col">Header</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td>1,001</td>
                            <td>random</td>
                            <td>data</td>
                            <td>placeholder</td>
                            <td>
                                <Link className={"btn btn-sm btn-primary"}
                                      to={"/cluster/1"}>
                                    바로가기
                                </Link>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>


                <Link className={"btn btn-sm btn-outline-secondary"}
                      to={"/system"}>
                    System
                </Link>

            </main>
        </>
    )
}

export default Home;
