import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import '../node_modules/bootstrap/dist/css/bootstrap.min.css';
import {combineReducers, createStore} from 'redux';
import {Provider} from 'react-redux';
import axios from "axios";

delete axios.defaults.headers.common["Authorization"];
axios.defaults.headers.common = {};
axios.defaults.headers.common.accept = 'application/json';

const userReducer = (state = null, action) => {
    switch (action.type) {
        case 'SET':
            return action.payload;
        case 'RESET':
            return null;
        default:
            return state;
    }
}

const allReducers = combineReducers({
    user: userReducer
})

const store = createStore(allReducers);

require('./home/Home.css')
ReactDOM.render(
    <React.StrictMode>
        <Provider store={store}>
            <App/>
        </Provider>
    </React.StrictMode>,
    document.getElementById('root')
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
