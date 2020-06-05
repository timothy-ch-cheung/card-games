import React from 'react';
import renderer from "react-test-renderer";
import Navigation from "./Navigation";
import {MemoryRouter} from "react-router-dom";

test('matches snapshot', () => {
    const tree = renderer.create(
        <MemoryRouter initialEntries={['/']}><
            Navigation/>
        </MemoryRouter>
    ).toJSON();

    expect(tree).toMatchSnapshot()
});