import React from 'react';
import {configure, shallow} from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
import Home from "./Home";

configure({ adapter: new Adapter() })
test('matches snapshot', () => {
    const tree = shallow(<Home/>);
    expect(tree).toMatchSnapshot()
});