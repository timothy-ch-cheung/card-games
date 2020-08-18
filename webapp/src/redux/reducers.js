export const userReducer = (state, action) => {
    if (typeof state === 'undefined') {
        return null
    }
    switch (action.type) {
        case 'SET_PLAYER':
            return action.payload;
        case 'RESET_PLAYER':
            return null;
        default:
            return state;
    }
}

export const keyReducer = (state, action) => {
    if (typeof state === 'undefined') {
        return null
    }
    switch (action.type) {
        case 'SET_KEY':
            return action.payload;
        case 'RESET_KEY':
            return null;
        default:
            return state;
    }
}

export const gameReducer = (state, action) => {
    if (typeof state === 'undefined') {
        return null
    }
    switch (action.type) {
        case 'SET_GAME':
            return action.payload;
        case 'RESET_GAME':
            return null;
        default:
            return state;
    }
}

export const gameModeReducer = (state, action) => {
    if (typeof state === 'undefined') {
        return null
    }
    switch (action.type) {
        case 'SET_GAME_MODE':
            return action.payload;
        case 'RESET_GAME_MODE':
            return null;
        default:
            return state;
    }
}