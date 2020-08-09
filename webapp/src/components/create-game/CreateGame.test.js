import React from 'react';
import CreateGame from "./CreateGame";
import {Provider} from "react-redux";
import configureStore from 'redux-mock-store';
import {configure, mount} from "enzyme";
import Adapter from "enzyme-adapter-react-16";
import API from "../../API";

configure({adapter: new Adapter()});

describe("TEST SUITE CreateGame: ", () => {
    const MockAdapter = require("axios-mock-adapter");
    const mockStore = configureStore([]);
    let store;
    let mockAPI;
    beforeAll(() => {
        mockAPI = new MockAdapter(API);
    });

    beforeEach(() => {
        store = mockStore({
            user: null,
            gameMode: "Match Two"
        });
    });

    test('Matches CreateGame snapshot (visible)', () => {
        const wrapper = mount(
            <Provider store={store}>
                <CreateGame show={true}/>
            </Provider>
        );
        expect(wrapper).toMatchSnapshot()
    });

    test('Matches CreateGame snapshot (hidden)', () => {
        const wrapper = mount(
            <Provider store={store}>
                <CreateGame show={false}/>
            </Provider>
        );
        expect(wrapper).toMatchSnapshot()
    });

    test('Calls on hide when close button is clicked', () => {
        const hide = jest.fn();
        const wrapper = mount(
            <Provider store={store}>
                <CreateGame show={true} onClose={hide}/>
            </Provider>
        );
        wrapper.find('button[data-test="close-create-game-btn"]').simulate('click');
        expect(hide).toHaveBeenCalledTimes(1);
    });

    describe('Form submit tests - ', () => {
        const flushPromises = () => new Promise(resolve => setTimeout(resolve))
        let wrapper;

        beforeEach(() => {
            wrapper = mount(
                <Provider store={store}>
                    <CreateGame show={true}/>
                </Provider>
            );
        });

        afterEach(() => {
            store.clearActions();
            mockAPI.resetHistory();
        });

        test('plus/minus buttons change rounds by player count within max/min limit of 2 and 4', () => {
            const clickNumberPickerButton = (choice, times) => {
                for (let i = 0; i < times; i++) {
                    wrapper.find(`button[data-test="numPlayers-${choice}-btn"]`).simulate('click');
                }
            };
            expect(wrapper.find('input[data-test="numPlayers-value"]').props().value).toEqual(2);
            clickNumberPickerButton("plus", 2);
            expect(wrapper.find('input[data-test="numPlayers-value"]').props().value).toEqual(4);
            clickNumberPickerButton("plus", 1);
            expect(wrapper.find('input[data-test="numPlayers-value"]').props().value).toEqual(4);
            clickNumberPickerButton("minus", 2);
            expect(wrapper.find('input[data-test="numPlayers-value"]').props().value).toEqual(2);
            clickNumberPickerButton("minus", 1);
            expect(wrapper.find('input[data-test="numPlayers-value"]').props().value).toEqual(2);
        });

        test('can switch game mode', async () => {
            let eventObj = {target: {value: 'Match Two'}}
            wrapper.find('select[data-test="game-mode-select"]').simulate('change', eventObj);
            await flushPromises();
            let actions = store.getActions();
            expect(actions.length).toBe(1);
            expect(actions[0]).toEqual({"type": "SET_GAME_MODE", "payload": "Match Two"});
        });

        test('POST /player AND /create when userId is null', async () => {
            let close = jest.fn();
            mockAPI.onPost("/player").reply(200, {id: "12345678901234567890123456789012"});
            mockAPI.onPost("/create").reply(200, {id: 1});
            store = mockStore({user: null, gameMode: "Match Two"});
            wrapper = mount(<Provider store={store}><CreateGame show={true} onClose={close}/></Provider>);
            const eventObj = {
                target: {
                    gameMode: {value: 'Match Two'},
                    lobbyName: {value: 'test lobby'},
                    nickname: {value: 'John'}
                }
            };
            let form = wrapper.find('form');
            form.getDOMNode().checkValidity = jest.fn(() => true);
            form.simulate('submit', eventObj);

            await flushPromises()
            expect(close).toHaveBeenCalledTimes(1);
            expect(mockAPI.history.post.length).toBe(2);
            let actions = store.getActions();
            expect(actions.length).toBe(3);
            expect(actions[0]).toEqual({"type": "SET_GAME_MODE", "payload": "Match Two"});
            expect(actions[1]).toEqual({"type": "SET_PLAYER", "payload": "12345678901234567890123456789012"});
            expect(actions[2]).toEqual({"type": "SET_GAME", "payload": 1});
        });

        test('POST only /create when userId is saved', async () => {
            let close = jest.fn();
            mockAPI.onPost("/create").reply(200, {id: 1});
            store = mockStore({user: "12345678901234567890123456789012", gameMode: "Match Two"});
            wrapper = mount(<Provider store={store}><CreateGame show={true} onClose={close}/></Provider>);
            const eventObj = {
                target: {
                    gameMode: {value: 'Match Two'},
                    lobbyName: {value: 'test lobby'}
                }
            };
            let form = wrapper.find('form');
            form.getDOMNode().checkValidity = jest.fn(() => true);
            form.simulate('submit', eventObj);

            await flushPromises()
            expect(close).toHaveBeenCalledTimes(1);
            expect(mockAPI.history.post.length).toBe(1);
            let actions = store.getActions();
            expect(actions.length).toBe(2);
            expect(actions[0]).toEqual({"type": "SET_GAME_MODE", "payload": "Match Two"});
            expect(actions[1]).toEqual({"type": "SET_GAME", "payload": 1});
        });

        test('Invalid form sends no requests and does not close modal', async () => {
            let close = jest.fn();
            store = mockStore({gameMode: "Match Two"});
            wrapper = mount(<Provider store={store}><CreateGame show={true} onClose={close}/></Provider>);
            const eventObj = {
                stopPropagation: jest.fn(),
                target: {
                    gameMode: {value: 'Match Two'}
                }
            };
            let form = wrapper.find('form');
            form.getDOMNode().checkValidity = jest.fn(() => false);
            form.simulate('submit', eventObj);

            await flushPromises()
            expect(close).toHaveBeenCalledTimes(0);
            expect(mockAPI.history.post.length).toBe(0);
            expect(store.getActions().length).toBe(0);
        });
    });
});