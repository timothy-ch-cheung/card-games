import React from 'react';
import InputGroup from "react-bootstrap/InputGroup";
import FormControl from "react-bootstrap/FormControl";
import Button from "react-bootstrap/Button";

function NumberPicker(props) {
    const largeFont = {fontSize: "1.5rem", padding: "0rem 0.75rem", width: "40px"}

    return (
        <InputGroup style={{width: "150px"}}>
            <InputGroup.Prepend>
                <Button variant="info" style={largeFont} onClick={props.onDecrease}
                        data-test={props.name + "-minus-btn"}>-</Button>
            </InputGroup.Prepend>
            <FormControl className="number-picker" disabled name={props.name}
                         style={{backgroundColor: "#fff", textAlign: "center"}} value={props.value}
                         data-test={props.name + "-value"}/>
            <InputGroup.Append>
                <Button variant="info" style={largeFont} onClick={props.onIncrease}
                        data-test={props.name + "-plus-btn"}>+</Button>
            </InputGroup.Append>
        </InputGroup>
    );
}

export default NumberPicker;