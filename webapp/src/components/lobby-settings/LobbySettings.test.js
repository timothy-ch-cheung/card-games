import {configure, shallow} from "enzyme";
import Adapter from "enzyme-adapter-react-16";
import React from "react";
import LobbySettings from "./LobbySettings";

configure({adapter: new Adapter()});

describe("TEST SUITE NumberPicker: ", () => {
    let wrapper;

    const matchTwo = {
        minPlayers: 2,
        maxPlayers: 4,
        roundIncrement: "PLAYER_COUNT",
        enabled: true
    }

    const choicePoker = {
        name: "Choice Poker",
        minPlayers: 2,
        maxPlayers: 6,
        roundIncrement: "ONE",
        enabled: false
    }

    test('Matches LobbySettings snapshot', () => {
        wrapper = shallow(<LobbySettings gameMode={matchTwo} numPlayers={2}/>);
        expect(wrapper).toMatchSnapshot();
    });

    test('plus button increases players by player count', () => {
        wrapper = shallow(<LobbySettings gameMode={matchTwo} numPlayers={2}/>);
    });

    test('minus button decreases players by player count', () => {
        wrapper = shallow(<LobbySettings gameMode={matchTwo} numPlayers={2}/>);
    });
});