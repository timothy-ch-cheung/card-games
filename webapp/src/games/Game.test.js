import React from 'react';
import {configure, shallow} from 'enzyme';
import Games from "./Games";
import Adapter from 'enzyme-adapter-react-16';

configure({ adapter: new Adapter() })

test('Matches Games snapshot', () => {
    const tree = shallow(<Games/>);
    expect(tree).toMatchSnapshot()
});