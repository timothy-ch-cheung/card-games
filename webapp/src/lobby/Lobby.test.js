import React from 'react';
import {configure, shallow} from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
import Lobby from "./Lobby";
import configureStore from "redux-mock-store";
import {Provider} from "react-redux";

configure({adapter: new Adapter()});

const mockStore = configureStore([]);
let store = mockStore({});

test('Matches Lobby snapshot', () => {
    const tree = shallow(<Provider store={store}><Lobby/></Provider>);
    expect(tree).toMatchSnapshot()
});