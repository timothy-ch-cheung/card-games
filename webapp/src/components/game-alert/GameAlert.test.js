import React from 'react';
import {shallow} from "enzyme";
import GameAlert from "./GameAlert";

describe("TEST SUITE Game Alert: ", () => {
    test('Matches snapshot', () => {
        let wrapper = shallow(<GameAlert show={true} errorText="Error: Game not found"/>)
        expect(wrapper).toMatchSnapshot()
    });
});