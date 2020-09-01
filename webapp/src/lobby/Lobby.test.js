import React from 'react';
import {configure, mount} from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
import Lobby from "./Lobby";
import configureStore from "redux-mock-store";
import {Provider} from "react-redux";
import API from "../API";
import {flushPromises} from "../common/Util";

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
    let store = mockStore({gameMode: "MATCH_TWO", game: 1});
    const MockAdapter = require("axios-mock-adapter");
    let mockAPI;

    beforeAll(() => {
        mockAPI = new MockAdapter(API);
    });

    afterEach(() => {
        store.clearActions();
        mockAPI.resetHistory();
        mockAPI.reset();
    });

    describe("When lobby is active, ", () => {
        beforeEach(() => {
            mockAPI.onGet("/game/1").reply(200, {
                id: 1,
                host: {id: "123", username: "John"},
                guests: [{id: "321", username: "Jane"}],
                gameMode: "MATCH_TWO",
                gameStatus: "OPEN"
            });
            wrapper = mount(<Provider store={store}><Lobby onShowError={showError}/></Provider>);
        });

        test('Matches Initial Lobby snapshot', () => {
            expect(wrapper).toMatchSnapshot();
            wrapper.unmount();
        });

        test('Leave game button sends PATCH', async () => {
            mockAPI.onPatch("/leave/1").reply(204, {});
            wrapper.find('button[data-test="leave-game-btn"]').simulate('click');
            await flushPromises();

            expect(mockAPI.history.patch.length).toBe(1);
            let actions = store.getActions();
            expect(actions.length).toBe(3);
            expect(actions[0]).toEqual({"type": "SET_GAME_MODE", "payload": "MATCH_TWO"});
            expect(actions[1]).toEqual({"type": "RESET_GAME"});
            expect(actions[2]).toEqual({"type": "RESET_GAME_MODE"});
            wrapper.unmount();
        });
    });

    describe("When lobby is DELETED", () => {
        beforeEach(() => {
            mockAPI.onGet("/game/1").reply(200, {
                id: 1,
                host: {id: "123", username: "John"},
                guests: [{id: "321", username: "Jane"}],
                gameMode: "MATCH_TWO",
                gameStatus: "DELETED"
            });
            wrapper = mount(<Provider store={store}><Lobby onShowError={showError}/></Provider>);
        });

        test('On component load GET DELETED game resets Game Id', async () => {
            await flushPromises();
            let actions = store.getActions();
            expect(actions.length).toBe(1);
            expect(actions[0]).toEqual({"type": "RESET_GAME"});
        });
    });

    describe("When lobby is not returned ", () => {

        beforeEach(() => {
            mockAPI.onGet("/game/1").reply(400, {
                message: "Server inactive"
            });
            wrapper = mount(<Provider store={store}><Lobby onShowError={showError}/></Provider>);
        });

        test('from API call after component mount', async () => {
            await flushPromises();
            let actions = store.getActions();
            expect(actions.length).toBe(1);
            expect(actions[0]).toEqual({"type": "RESET_GAME"});
        });
    });
});