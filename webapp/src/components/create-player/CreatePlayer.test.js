import React from 'react';
import configureStore from "redux-mock-store";
import {mount} from "enzyme";
import {Provider} from "react-redux";
import CreatePlayer from "./CreatePlayer";
import API from "../../API";
import {flushPromises} from "../../common/Util";

describe("TEST SUITE Create Player: ", () => {
    const mockStore = configureStore([]);
    let store;
    let wrapper;
    const MockAdapter = require("axios-mock-adapter");
    let mockAPI;

    let close;
    let submit;

    beforeAll(() => {
        mockAPI = new MockAdapter(API);
    });

    beforeEach(() => {
        close = jest.fn();
        submit = jest.fn();
        store = mockStore({gameMode: "MATCH_TWO", game: 1});
        wrapper = mount(<Provider store={store}><CreatePlayer onClose={close} show={true}
                                                              onSubmit={submit}/></Provider>);
        mockAPI.onPost("/player").reply(200, {
            id: "12345678901234567890123456789023",
            key: "keykeykeykeykeykeykeykeykeykeyke"
        });
    });

    afterEach(() => {
        store.clearActions();
        mockAPI.resetHistory();
        mockAPI.reset();
    });

    test("Matches snapshot", () => {
        expect(wrapper).toMatchSnapshot()
    });

    test("Calls onClose when close button is clicked", () => {
        wrapper.find('button[data-test="close-create-player-modal"]').simulate('click');
        expect(close).toHaveBeenCalledTimes(1);
    });

    test("Should make POST request and save response when from submitted", async () => {
        const eventObj = {
            preventDefault: jest.fn(),
            target: {
                nickname: {value: 'John'}
            }
        };
        let form = wrapper.find('form');
        form.getDOMNode().checkValidity = jest.fn(() => true);
        form.simulate('submit', eventObj);
        await flushPromises();

        expect(submit).toHaveBeenCalledTimes(1);
        expect(mockAPI.history.post.length).toBe(1);
        let actions = store.getActions();
        expect(actions.length).toBe(2);
        expect(actions[0]).toEqual({type: "SET_PLAYER", payload: "12345678901234567890123456789023"});
        expect(actions[1]).toEqual({type: "SET_KEY", payload: "keykeykeykeykeykeykeykeykeykeyke"});
    });

    test("Should not make POST request when form validation", async () => {
        let form = wrapper.find('form');
        form.getDOMNode().checkValidity = jest.fn(() => false);
        form.simulate('submit', {preventDefault: jest.fn()});
        await flushPromises();

        expect(submit).toHaveBeenCalledTimes(0);
        expect(mockAPI.history.post.length).toBe(0);
        expect(store.getActions().length).toBe(0);
    });
});