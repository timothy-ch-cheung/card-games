import {configure, mount, shallow} from "enzyme";
import Adapter from "enzyme-adapter-react-16";
import React from "react";
import LobbySettings from "./LobbySettings";
import API from "../../API";
import {flushPromises} from "../../common/Util";

configure({adapter: new Adapter()});

describe("TEST SUITE LobbySettings: ", () => {
    let wrapper;
    const MockAdapter = require("axios-mock-adapter");
    let mockAPI;

    beforeAll(() => {
        mockAPI = new MockAdapter(API);
    });

    test('Matches LobbySettings snapshot [Match Two]', () => {
        wrapper = shallow(<LobbySettings numPlayers={2} gameModes="MATCH_TWO"/>);
        expect(wrapper).toMatchSnapshot();
    });

    test('Matches LobbySettings snapshot [Choice Poker]', () => {
        wrapper = shallow(<LobbySettings numPlayers={2} gameModes="CHOICE_POKER"/>);
        expect(wrapper).toMatchSnapshot();
    });

    describe('Number of rounds - ', () => {
        beforeEach(() => {
            mockAPI.onPatch("/update/1").reply(204, {});
        });

        afterEach(() => {
            mockAPI.resetHistory();
            mockAPI.reset();
        });


        test('plus/minus buttons change rounds by player count', async () => {
            wrapper = mount(<LobbySettings gameMode={"MATCH_TWO"} gameId={1} numPlayers={2} isHost={true}/>);
            expect(wrapper.find('input[data-test="round-number-text"]').props().value).toEqual(2);
            wrapper.find('button[data-test="numRounds-plus-btn"]').simulate('click');
            await flushPromises();
            expect(mockAPI.history.patch.length).toBe(1);
            expect(wrapper.find('input[data-test="round-number-text"]').props().value).toEqual(4);
            wrapper.find('button[data-test="numRounds-minus-btn"]').simulate('click');
            await flushPromises();
            expect(wrapper.find('input[data-test="round-number-text"]').props().value).toEqual(2);
        });

        test('plus/minus buttons change rounds by one', async () => {
            wrapper = mount(<LobbySettings gameMode={"CHOICE_POKER"} gameId={1} numPlayers={2} isHost={true}/>);
            expect(wrapper.find('input[data-test="round-number-text"]').props().value).toEqual(1);
            wrapper.find('button[data-test="numRounds-plus-btn"]').simulate('click');
            await flushPromises();
            expect(wrapper.find('input[data-test="round-number-text"]').props().value).toEqual(2);
            wrapper.find('button[data-test="numRounds-minus-btn"]').simulate('click');
            await flushPromises();
            expect(wrapper.find('input[data-test="round-number-text"]').props().value).toEqual(1);
        });

        test('onSubmit function is set and called on submit event', () => {
            let submit = jest.fn();
            wrapper = mount(<LobbySettings gameMode={"MATCH_TWO"} numPlayers={2} onSubmit={submit}/>);
            const formEventMocked = {preventDefault: jest.fn(), target: {numRounds: {value: 4}}};
            wrapper.find('form').simulate('submit', formEventMocked);
            expect(wrapper.props().onSubmit).toEqual(submit);
            expect(submit).toHaveBeenCalledTimes(1);
        });

        test('onSubmit function is not called when rounds invalid', () => {
            let submit = jest.fn();
            wrapper = mount(<LobbySettings gameMode={"MATCH_TWO"} numPlayers={2} onSubmit={submit}/>);
            const formEventMocked = {preventDefault: jest.fn(), target: {numRounds: {value: undefined}}};
            wrapper.find('form').simulate('submit', formEventMocked);
            expect(submit).toHaveBeenCalledTimes(0);
        });

        test('plus/minus are disabled when not host', () => {
            wrapper = mount(<LobbySettings gameMode={"CHOICE_POKER"} numPlayers={2} isHost={false}/>);
            expect(wrapper.find('button[data-test="numRounds-plus-btn"]').props().disabled).toBe(true);
            expect(wrapper.find('button[data-test="numRounds-minus-btn"]').props().disabled).toBe(true);
        });
    });
});