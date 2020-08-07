import {configure, mount, shallow} from "enzyme";
import Adapter from "enzyme-adapter-react-16";
import React from "react";
import LobbySettings from "./LobbySettings";

configure({adapter: new Adapter()});

describe("TEST SUITE LobbySettings: ", () => {
    let wrapper;

    test('Matches LobbySettings snapshot', () => {
        wrapper = shallow(<LobbySettings numPlayers={2} gameModes="Match Two"/>);
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

        test('plus/minus buttons change rounds by player count', () => {
            wrapper = mount(<LobbySettings gameMode={"Choice Poker"} numPlayers={2}/>);
            expect(wrapper.find('input[data-test="round-number-text"]').props().value).toEqual(1);
            wrapper.find('button[data-test="numRounds-plus-btn"]').simulate('click');
            expect(wrapper.find('input[data-test="round-number-text"]').props().value).toEqual(2);
            wrapper.find('button[data-test="numRounds-minus-btn"]').simulate('click');
            expect(wrapper.find('input[data-test="round-number-text"]').props().value).toEqual(1);
        });

        test('onSubmit function is set', () => {
            let submit = jest.fn();
            wrapper = mount(<LobbySettings gameMode={"Match Two"} numPlayers={2} onSubmit={submit}/>);
            expect(wrapper.props().onSubmit).toEqual(submit);
        });
    });
});