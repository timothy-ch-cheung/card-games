import React from 'react';
import Navigation from "./Navigation";
import {MemoryRouter} from "react-router-dom";
import {configure, shallow} from "enzyme";
import Adapter from "enzyme-adapter-react-16";

configure({adapter: new Adapter()});

test('Matches Navigation snapshot', () => {
    const tree = shallow(
        <MemoryRouter initialEntries={[]}><
            Navigation/>
        </MemoryRouter>
    );

    expect(tree).toMatchSnapshot()
});