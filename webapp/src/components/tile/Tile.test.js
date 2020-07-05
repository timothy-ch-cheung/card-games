import React from 'react';
import { render, cleanup } from '@testing-library/react';
import Tile from "./Tile";
import renderer from "react-test-renderer";

afterEach(cleanup);

test('renders tile', () => {
  const { getByTestId } = render(<Tile/>);
  const tile = getByTestId('tile')
  expect(tile).toBeInTheDocument();
});

test('Matches Tile snapshot', () => {
  const tree = renderer.create(<Tile/>).toJSON();
  expect(tree).toMatchSnapshot()
});
