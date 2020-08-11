import {configure, mount, shallow} from "enzyme";
import Adapter from "enzyme-adapter-react-16";
import React from "react";
import LobbySettings from "./LobbySettings";

configure({adapter: new Adapter()});

describe("TEST SUITE LobbySettings: ", () => {
    let wrapper;

    test('Matches LobbySettings snapshot [Match Two]', () => {
        wrapper = shallow(<LobbySettings numPlayers={2} gameModes="Match Two"/>);
        expect(wrapper).toMatchSnapshot();
    });

    test('Matches LobbySettings snapshot [Choice Poker]', () => {
        wrapper = shallow(<LobbySettings numPlayers={2} gameModes="Choice Poker"/>);
        expect(wrapper).toMatchSnapshot();
    });

    describe('Number of rounds - ', () => {
        test('plus/minus buttons change rounds by player count', () => {
            wrapper = mount(<LobbySettings gameMode={"Match Two"} numPlayers={2}/>);
            expect(wrapper.find('input[data-test="round-number-text"]').props().value).toEqual(2);
            wrapper.find('button[data-test="numRounds-plus-btn"]').simulate('click');
            expect(wrapper.find('input[data-test="round-number-text"]').props().value).toEqual(4);
            wrapper.find('button[data-test="numRounds-minus-btn"]').simulate('click');
            expect(wrapper.find('input[data-test="round-number-text"]').props().value).toEqual(2);
        });

        test('plus/minus buttons change rounds by one', () => {
            wrapper = mount(<LobbySettings gameMode={"Choice Poker"} numPlayers={2}/>);
            expect(wrapper.find('input[data-test="round-number-text"]').props().value).toEqual(1);
            wrapper.find('button[data-test="numRounds-plus-btn"]').simulate('click');
            expect(wrapper.find('input[data-test="round-number-text"]').props().value).toEqual(2);
            wrapper.find('button[data-test="numRounds-minus-btn"]').simulate('click');
            expect(wrapper.find('input[data-test="round-number-text"]').props().value).toEqual(1);
        });

        test('onSubmit function is set and called on submit event', () => {
            let submit = jest.fn();
            wrapper = mount(<LobbySettings gameMode={"Match Two"} numPlayers={2} onSubmit={submit}/>);
            const formEventMocked = {preventDefault: jest.fn(), target: {numRounds: {value: 4}}};
            wrapper.find('form').simulate('submit', formEventMocked);
            expect(wrapper.props().onSubmit).toEqual(submit);
            expect(submit).toHaveBeenCalledTimes(1);
        });

        test('onSubmit function is not called when rounds invalid', () => {
            let submit = jest.fn();
            wrapper = mount(<LobbySettings gameMode={"Match Two"} numPlayers={2} onSubmit={submit}/>);
            const formEventMocked = {preventDefault: jest.fn(), target: {numRounds: {value: undefined}}};
            wrapper.find('form').simulate('submit', formEventMocked);
            expect(submit).toHaveBeenCalledTimes(0);
        });
    });
});