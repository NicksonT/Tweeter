import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";
import Home from "./Home";
import Login from "./Login";
import React from "react";
import Cookies from 'universal-cookie';

export default function App() {
    const [loggedIn, setLoggedIn] = React.useState(localStorage.getItem("loggedIn") == null ? "false" : localStorage.getItem("loggedIn"));
    const [loggedInUserName, setLoggedInUserName] = React.useState(localStorage.getItem("loggedInUserName") == null ? "" : localStorage.getItem("loggedInUserName"));
    const cookies = new Cookies();

    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={loggedIn === "true" ?
                    <Home cookies={cookies} setLoggedIn={setLoggedIn} loggedInUserName={loggedInUserName}/> :
                    <Navigate replace to="/login"/>}/>
                <Route path="/login" element={loggedIn === "false" ?
                    <Login cookies={cookies} setLoggedIn={setLoggedIn} setLoggedInUserName={setLoggedInUserName}/> :
                    <Navigate replace to="/"/>}/>
            </Routes>
        </BrowserRouter>
    );
}