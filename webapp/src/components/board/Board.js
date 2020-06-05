import React from "react";
import Tile from "../tile/Tile";

class Board extends React.Component {

    render() {
        return (
            <div className="container">
                <style type="text/css">
                    {`
                        .board {
                            display: grid;
                            grid-gap: 1px;
                            grid-template-columns: repeat(3, 1fr);
                            width: 300px;
                            height: 300px;
                            grid-gap: 0;
                            margin: 20px auto;
                        }    
                    `}
                </style>
                <div className="board">
                    <Tile/><Tile/><Tile/>
                    <Tile/><Tile/><Tile/>
                    <Tile/><Tile/><Tile/>
                </div>
            </div>
        )
    }
}

export default Board;