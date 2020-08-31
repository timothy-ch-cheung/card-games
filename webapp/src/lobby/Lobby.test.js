import React from 'react';
import {configure, mount} from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
import Lobby from "./Lobby";
import configureStore from "redux-mock-store";
import {Provider} from "react-redux";
import {actWait, flushPromises} from "../common/Util";
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
        beforeEach(async () => {
            mockAPI.onGet("/game/1").reply(200, {
                id: 1,
                host: {id: "123", username: "John"},
                guests: [{id: "321", username: "Jane"}],
                gameMode: "MATCH_TWO"
            });
            wrapper = mount(<Provider store={store} onShowError={showError}><Lobby/></Provider>);
            await actWait();
        });

        test('Matches Initial Lobby snapshot', () => {
            expect(wrapper).toMatchSnapshot()
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
            wrapper = mount(<Provider store={store} onShowError={showError}><Lobby/></Provider>);
        });

        test('On component load GET DELETED game resets Game Id', async () => {
            let actions = store.getActions();
            expect(actions.length).toBe(1);
            expect(actions[0]).toEqual({"type": "RESET_GAME"});
        });
    });

});