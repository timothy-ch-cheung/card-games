export const setPlayer = value => {
    return {
        type: 'SET_PLAYER',
        payload: value
    }
}

export const resetPlayer = () => {
    return {
        type: 'RESET_GAME'
    }
}