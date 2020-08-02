export default {
    matchTwo: {
        name: "Match Two",
        minPlayers: 2,
        maxPlayers: 4,
        rounds: {
            initial: "PLAYER_COUNT",
            increment: "PLAYER_COUNT"
        }
    },
    choicePoker: {
        name: "Choice Poker",
        minPlayers: 2,
        maxPlayers: 6,
        rounds: {
            initial: "ONE",
            increment: "ONE"
        }
    }
}