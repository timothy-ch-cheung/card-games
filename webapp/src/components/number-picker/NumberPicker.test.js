import React from "react";
import NumberPicker from "./NumberPicker";
import {configure, shallow} from "enzyme";
import Adapter from "enzyme-adapter-react-16";

configure({adapter: new Adapter()});

describe("TEST SUITE NumberPicker: ", () => {
    let wrapper;
    let increase;
    let decrease;

    beforeEach(() => {
        increase = jest.fn();
        decrease = jest.fn();
        wrapper = shallow(<NumberPicker value={123} name={"number"} onIncrease={increase} onDecrease={decrease}/>);
    });

    test('Matches PlayerList snapshot', () => {
        expect(wrapper).toMatchSnapshot();
    });

    test('onDecrease is called when minus button is clicked', () => {
        wrapper.find('[data-test="number-minus-btn"]').simulate("click");
        expect(decrease).toHaveBeenCalledTimes(1);
        expect(increase).toHaveBeenCalledTimes(0);
    });

    test('onIncrease is called when plus button is clicked', () => {
        wrapper.find('[data-test="number-plus-btn"]').simulate("click");
        expect(increase).toHaveBeenCalledTimes(1);
        expect(decrease).toHaveBeenCalledTimes(0);
    });
});