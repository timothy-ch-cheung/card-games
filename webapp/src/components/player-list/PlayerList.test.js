import React from 'react';
import {configure, mount, shallow} from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
import PlayerList from "./PlayerList";
import each from "jest-each";

configure({adapter: new Adapter()});

describe("TEST SUITE PlayerList: ", () => {
    const players = [{username: "John", isHost: true}, {username: "Jane", isHost: false}];

    test('Matches PlayerList snapshot', () => {
        const wrapper = mount(<PlayerList players={players}/>);
        expect(wrapper).toMatchSnapshot();
    });

    test('Player name has crown emoji if they are host', () => {
        const wrapper = shallow(<PlayerList players={[{username: "John", isHost: true}]}/>);
        expect(wrapper.find('[data-test="player0-row"]').dive()
            .find('[data-test="player0-name"]').text()).toEqual('John 👑')
    });

    test('Player name does not have crown emoji if they are guest', () => {
        const wrapper = shallow(<PlayerList players={[{username: "Jane", isHost: false}]}/>);
        expect(wrapper.find('[data-test="player0-row"]').dive()
            .find('[data-test="player0-name"]').text()).toEqual('Jane')
    });

    each([undefined, null, "", "      "]).test('Row is not rendered if player name is blank', (blankValue) => {
        const wrapper = shallow(<PlayerList
            players={[{username: "Jack", isHost: true}, {username: blankValue, isHost: false}]}/>);
        expect(wrapper.find('[data-test="player0-row"]').length).toEqual(1);
        expect(wrapper.find('[data-test="player1-row"]').length).toEqual(0);
    });

    test('number of players in lobby is shown', () => {
        const wrapper = shallow(<PlayerList maxPlayers={6}
            players={[{username: "Jack", isHost: true}, {username: "Jane", isHost: false}]}/>);
        expect(wrapper.find('p[data-test="player-count"]').text()).toEqual("2/6")
    });
});