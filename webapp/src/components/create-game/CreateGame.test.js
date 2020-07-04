import React from 'react';
import renderer from "react-test-renderer";
import CreateGame from "./CreateGame";
import {Provider} from "react-redux";
import configureStore from 'redux-mock-store';
import {configure} from "enzyme";
import Adapter from "enzyme-adapter-react-16";

configure({adapter: new Adapter()});

const mockStore = configureStore([]);
let store = mockStore({
    user: null,
});

test('Matches CreateGame snapshot', () => {
    const tree = renderer.create(
        <Provider store={store}>
            <CreateGame/>
        </Provider>
    ).toJSON();

    expect(tree).toMatchSnapshot()
});