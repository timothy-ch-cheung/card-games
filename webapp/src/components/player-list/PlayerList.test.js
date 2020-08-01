import React from 'react';
import {configure, mount} from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
import PlayerList from "./PlayerList";

configure({adapter: new Adapter()});

let players = [{name: "John", isHost: true}, {name: "Jane", isHost: false}]

test('Matches PlayerList snapshot', () => {
    const tree = mount(<PlayerList players={players}/>);
    expect(tree).toMatchSnapshot();
});