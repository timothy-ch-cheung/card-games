import React from 'react';
import {configure, shallow} from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
import Lobby from "./Lobby";
import configureStore from "redux-mock-store";
import {Provider} from "react-redux";

const mockStore = configureStore([]);
let store = mockStore({});
configure({adapter: new Adapter()})

test('matches snapshot', () => {
    const tree = shallow(<Provider store={store}><Lobby/></Provider>);
    expect(tree).toMatchSnapshot()
});