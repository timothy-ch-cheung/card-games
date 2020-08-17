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

export const setKey = value => {
    return {
        type: 'SET_KEY',
        payload: value
    }
}

export const resetKey = () => {
    return {
        type: 'RESET_KEY'
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

export const setGameMode = value => {
    return {
        type: 'SET_GAME_MODE',
        payload: value
    }
}

export const resetGameMode = () => {
    return {
        type: 'RESET_GAME_MODE'
    }
}

