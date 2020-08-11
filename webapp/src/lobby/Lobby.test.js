import React from 'react';
import {configure, mount} from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
import Lobby from "./Lobby";
import configureStore from "redux-mock-store";
import {Provider} from "react-redux";
import {flushPromises} from "../common/Util";
import API from "../API";

configure({adapter: new Adapter()});

jest.mock('react-router-dom', () => ({
    useHistory: () => ({
        push: jest.fn(),
    }),
}));

describe("TEST SUITE Lobby: ", () => {
    let wrapper;
    const showError = jest.fn();
    const mockStore = configureStore([]);
    let store = mockStore({gameMode: "Match Two", game: 1});
    const MockAdapter = require("axios-mock-adapter");
    let mockAPI;

    beforeAll(() => {
        mockAPI = new MockAdapter(API);
    });

    beforeEach(() => {
        wrapper = mount(<Provider store={store} onShowError={showError}><Lobby/></Provider>);
        mockAPI.onGet("/game/1").reply(200, {data: {id: 1}});
    });

    afterEach(() => {
        store.clearActions();
        mockAPI.resetHistory();
        mockAPI.reset();
    });

    test('Matches Lobby snapshot', () => {
        expect(wrapper).toMatchSnapshot()
    });

    //TODO: Enable test case when migrated to using websockets for updates
    test.skip('Leave game button sends PATCH', async () => {
        mockAPI.onPatch("/leave/1").reply(204, {});
        wrapper.find('button[data-test="leave-game-btn"]').simulate('click');
        await flushPromises();

        expect(mockAPI.history.patch.length).toBe(1);
        expect(store.getActions().length).toBe(2);
    });
});