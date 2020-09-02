import axios from 'axios';

const production = 'https://card-games-tcc.herokuapp.com';
const development = 'http://localhost:8080';

export const baseURL = process.env.NODE_ENV === 'development' ? development : production;

const API = axios.create({
    baseURL: baseURL
});

export default API;