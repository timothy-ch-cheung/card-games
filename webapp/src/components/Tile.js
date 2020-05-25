import React from "react";
import styled from 'styled-components';

const Button = styled.button`
font-size: 2.5rem;
width: 100px;
height: 100px;
background-color: #f8f9fa;
border: 0;
flex: 0 0 32%;       
box-shadow: none;
border-radius: 0px;
padding: 0;
:focus {
    outline: 0;
}
:hover {
    background: #eeeeee;
}
`;

class Tile extends React.Component {

    render() {
        return (
            <Button className="btn-tile"></Button>
        );
    }

}

export default Tile;