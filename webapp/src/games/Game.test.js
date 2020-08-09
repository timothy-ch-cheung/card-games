import React from 'react';
import {configure, mount} from 'enzyme';
import Games from "./Games";
import Adapter from 'enzyme-adapter-react-16';
import configureStore from "redux-mock-store";
import {Provider} from "react-redux";

configure({adapter: new Adapter()})

const mockStore = configureStore([]);
let store = mockStore({
    gameMode: "Match Two"
});

test('Matches Games snapshot', () => {
    const tree = mount(<Provider store={store}><Games/></Provider>);
    expect(tree).toMatchSnapshot();
});