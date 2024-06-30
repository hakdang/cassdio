import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import "bootstrap"
import 'bootstrap-icons/font/bootstrap-icons.css'

import ConsoleNavbar from "./components/layout/console-navbar";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Home from "./pages/home";
import NotFound from "./pages/not-found";
import WorkspaceHome from "./pages/workspace/workspace-home";

function App() {
    return (
        <div className="App">
            <BrowserRouter>
                <ConsoleNavbar/>

                <div className="container-fluid">
                    <div className="row">

                        <Routes>
                            <Route path="/" element={<Home/>}></Route>
                            <Route path="/workspace/:workspaceId" element={<WorkspaceHome/>}></Route>
                            {/* 상단에 위치하는 라우트들의 규칙을 모두 확인, 일치하는 라우트가 없는경우 처리 */}
                            <Route path="*" element={<NotFound/>}></Route>
                        </Routes>

                    </div>
                </div>
            </BrowserRouter>
        </div>
    );
}

export default App;
