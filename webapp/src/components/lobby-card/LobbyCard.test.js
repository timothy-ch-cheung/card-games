import {configure, shallow} from "enzyme";
import Adapter from "enzyme-adapter-react-16";
import LobbyCard from "../lobby-card/LobbyCard";
import React from "react";
import configureStore from "redux-mock-store";
import {Provider} from "react-redux";

configure({adapter: new Adapter()});

const mockStore = configureStore([]);
let store = mockStore({
    user: null,
});

test('Matches LobbyCard snapshot', () => {
    const tree = shallow(<Provider store={store}><LobbyCard/></Provider>);
    expect(tree).toMatchSnapshot()
});