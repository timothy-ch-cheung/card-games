import React from "react";
import LobbyCard from "../components/LobbyCard";
import styled from "styled-components";
import CreateGame from "../components/CreateGame";

const Divider = styled.div`
border-bottom: 1px solid grey;
margin: 10px;
`;


class Games extends React.Component {
    render() {
        return (
            <div>
                <style type="text/css">
                    {`
                        .left-title {
                            text-align: left;
                        }
                    `}
                </style>
                <CreateGame/>
                <Divider/>
                <h1 className="left-title">Public games</h1>
                <LobbyCard/>
            </div>
        )
    }
}

export default Games;