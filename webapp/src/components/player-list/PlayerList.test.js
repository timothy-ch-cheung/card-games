import React from 'react';
import {configure, mount, shallow} from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
import PlayerList from "./PlayerList";
import each from "jest-each";

configure({adapter: new Adapter()});

describe("TEST SUITE PlayerList: ", () => {
    const players = [{name: "John", isHost: true}, {name: "Jane", isHost: false}];

    test('Matches PlayerList snapshot', () => {
        const wrapper = mount(<PlayerList players={players}/>);
        expect(wrapper).toMatchSnapshot();
    });

    test('Player name has crown emoji if they are host', () => {
        const wrapper = shallow(<PlayerList players={[{name: "John", isHost: true}]}/>);
        expect(wrapper.find('[data-test="player0-row"]').dive()
            .find('[data-test="player0-name"]').text()).toEqual('John 👑')
    });

    test('Player name does not have crown emoji if they are guest', () => {
        const wrapper = shallow(<PlayerList players={[{name: "Jane", isHost: false}]}/>);
        expect(wrapper.find('[data-test="player0-row"]').dive()
            .find('[data-test="player0-name"]').text()).toEqual('Jane')
    });

    each([undefined, null, "", "      "]).test('Row is not rendered if player name is blank', (blankValue) => {
        const wrapper = shallow(<PlayerList
            players={[{name: "Jack", isHost: true}, {name: blankValue, isHost: false}]}/>);
        expect(wrapper.find('[data-test="player0-row"]').length).toEqual(1);
        expect(wrapper.find('[data-test="player1-row"]').length).toEqual(0);
    });
});