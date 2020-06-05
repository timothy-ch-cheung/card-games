import React from 'react';
import renderer from "react-test-renderer";
import CreateGame from "./CreateGame";
import {Provider} from "react-redux";
import configureStore from 'redux-mock-store';

test('matches snapshot', () => {
    const mockStore = configureStore([]);
    let store = mockStore({
        user: null,
    });

    const tree = renderer.create(
        <Provider store={store}>
            <CreateGame/>
        </Provider>
    ).toJSON();

    expect(tree).toMatchSnapshot()
});