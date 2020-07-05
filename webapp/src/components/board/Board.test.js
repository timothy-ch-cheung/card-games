import React from 'react';
import Board from "./Board";
import {configure, shallow} from "enzyme";
import Adapter from "enzyme-adapter-react-16";

configure({adapter: new Adapter()})

test('Matches Board snapshot', () => {
    const tree = shallow(<Board/>);
    expect(tree).toMatchSnapshot()
});