import React from 'react';
import {configure, shallow} from 'enzyme';
import Games from "./Games";
import Adapter from 'enzyme-adapter-react-16';
import configureStore from "redux-mock-store";
import {Provider} from "react-redux";

configure({adapter: new Adapter()})

const mockStore = configureStore([]);
let store = mockStore({});

test('Matches Games snapshot', () => {
    const tree = shallow(<Provider store={store}><Games/></Provider>);
    expect(tree).toMatchSnapshot()
});