import axios from 'axios';

const production  = 'https://card-games-tcc.herokuapp.com';
const development = 'http://localhost:8080';

const API = axios.create({
    baseURL: process.env.NODE_ENV === 'development' ? development : production
});

export default API;