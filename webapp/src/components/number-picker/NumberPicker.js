import React from 'react';
import InputGroup from "react-bootstrap/InputGroup";
import FormControl from "react-bootstrap/FormControl";
import Button from "react-bootstrap/Button";

function NumberPicker(props) {
    const largeFont = {fontSize: "1.5rem", padding: "0rem 0.75rem", width: "40px"}
    props.setValue(props.minValue);


    const decrease = () => {
        //props.setValue(props.value - props.step)
    }

    const increase = () => {
        //props.setValue(props.value + props.step)
    }

    return (
        <InputGroup style={{width: "150px"}}>
            <InputGroup.Prepend>
                <Button variant="info" style={largeFont} onClick={decrease}>-</Button>
            </InputGroup.Prepend>
            <FormControl className="number-picker" disabled={true}
                         style={{backgroundColor: "#fff", textAlign: "center"}} value={props.value}/>
            <InputGroup.Append>
                <Button variant="info" style={largeFont} onClick={increase}>+</Button>
            </InputGroup.Append>
        </InputGroup>
    );
}

export default NumberPicker;