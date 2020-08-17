import React from 'react';
import {configure, mount} from 'enzyme';
import Games from "./Games";
import Adapter from 'enzyme-adapter-react-16';
import configureStore from "redux-mock-store";
import {Provider} from "react-redux";
import API from "../API";
import {flushPromises} from "../common/Util";

configure({adapter: new Adapter()})

describe("TEST SUITE Game: ", () => {
    const MockAdapter = require("axios-mock-adapter");
    let mockAPI;
    const mockStore = configureStore([]);
    let store;
    let mockGames;
    let wrapper;

    beforeAll(() => {
        mockAPI = new MockAdapter(API);
    });

    beforeEach(() => {
        store = mockStore({
            gameMode: "Match Two"
        });
        mockGames = {
            numGames: 2,
            games: [
                {id: 1, lobbyName: "test1", host: {username: "John"}},
                {id: 2, lobbyName: "test2", host: {username: "Jane"}}
            ]
        }
        mockAPI.onGet("/games").reply(200, mockGames);

        wrapper = mount(<Provider store={store}><Games/></Provider>);
    });

    afterEach(() => {
        store.clearActions();
        mockAPI.resetHistory();
        mockAPI.reset();
    });

    test('Matches snapshot', () => {
        expect(wrapper).toMatchSnapshot();
    });

    test('Clicking create game button opens modal', () => {
        expect(wrapper.find('div[data-test="create-game-modal"]').length).toBe(0);
        wrapper.find('button[data-test="create-game-btn"]').simulate('click');
        expect(wrapper.find('div[data-test="create-game-modal"]').length).toBe(1);
        wrapper.find('button[data-test="close-create-game-btn"]').simulate('click');
        expect(wrapper.find('div[data-test="create-game-modal"]').length).toBe(0);
    });

    test('Clicking refresh button makes GET request', async () => {
        await flushPromises();
        expect(mockAPI.history.get.length).toBe(1);
        wrapper.find('button[data-test="refresh-btn"]').simulate('click');
        await flushPromises();
        expect(mockAPI.history.get.length).toBe(2);
    });
});