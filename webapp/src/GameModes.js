export default {
    "Match Two": {
        minPlayers: 2,
        maxPlayers: 4,
        rounds: {
            initial: "PLAYER_COUNT",
            increment: "PLAYER_COUNT"
        },
        enabled: true
    },
    "Choice Poker": {
        name: "Choice Poker",
        minPlayers: 2,
        maxPlayers: 6,
        rounds: {
            initial: "ONE",
            increment: "ONE"
        },
        enabled: false
    }
}