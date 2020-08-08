import {configure, mount} from "enzyme";
import Adapter from "enzyme-adapter-react-16";
import LobbyCard from "../lobby-card/LobbyCard";
import React from "react";
import configureStore from "redux-mock-store";
import {Provider} from "react-redux";

configure({adapter: new Adapter()});

describe("TEST SUITE LobbyCard: ", () => {
    const mockStore = configureStore([]);
    let store;
    let submit;
    let showModal;

    beforeEach(() => {
        store = mockStore({
            user: null,
        });
        submit = jest.fn();
        showModal = jest.fn();
    })

    test('Matches LobbyCard snapshot', () => {
        const wrapper = mount(<Provider store={store}><LobbyCard/></Provider>);
        expect(wrapper).toMatchSnapshot()
    });

    test('join game opens create player modal when no player is stored', () => {
        const wrapper = mount(<Provider store={store}><LobbyCard onSubmit={submit} showModal={showModal}/></Provider>);
        wrapper.find('button[data-test="join-game-btn"]').simulate('click');
        expect(showModal).toHaveBeenCalledTimes(1);
        expect(submit).toHaveBeenCalledTimes(0);
    });

    test('join game opens lobby when player is stored', () => {
        store = mockStore({
            user: "12345678901234567890123456789012",
        });
        const wrapper = mount(<Provider store={store}><LobbyCard onSubmit={submit} showModal={showModal}/></Provider>);
        wrapper.find('button[data-test="join-game-btn"]').simulate('click');
        expect(submit).toHaveBeenCalledTimes(1);
        expect(showModal).toHaveBeenCalledTimes(0);
    });
});