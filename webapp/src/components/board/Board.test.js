import React from 'react';
import renderer from "react-test-renderer";
import Board from "./Board";

test('matches snapshot', () => {
    const tree = renderer.create(<Board/>).toJSON();
    expect(tree).toMatchSnapshot()
});