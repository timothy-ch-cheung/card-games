export const setPlayer = value => {
    return {
        type: 'SET_PLAYER',
        payload: value
    }
}

export const resetPlayer = () => {
    return {
        type: 'RESET_PLAYER'
    }
}

export const setGame = value => {
    return {
        type: 'SET_GAME',
        payload: value
    }
}

export const resetGame = () => {
    return {
        type: 'RESET_GAME'
    }
}