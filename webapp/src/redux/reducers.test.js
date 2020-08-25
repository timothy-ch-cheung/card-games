import {resetGame, resetGameMode, resetKey, resetPlayer, setGame, setGameMode, setKey, setPlayer} from "./actions";
import {gameModeReducer, gameReducer, keyReducer, userReducer} from "./reducers";
import GameModes from "../GameModes";

describe("TEST SUITE Redux Reducers: ", () => {
    describe("User reducer", () => {
        test("returns payload with SET", () => {
            expect(userReducer(null, setPlayer("4321"))).toEqual("4321");
        });

        test("returns null with RESET", () => {
            expect(userReducer("1234", resetPlayer())).toEqual(null);
        });

        test("returns state when no action matched", () => {
            expect(userReducer("1234", {type: "REVERT_PLAYER"})).toEqual("1234");
        });

        test("initialises state if undefined", () => {
            expect(userReducer(undefined, setPlayer())).toEqual(null);
        });
    });

    describe("Key reducer", () => {
        test("returns payload with SET", () => {
            expect(keyReducer(null, setKey("keykey"))).toEqual("keykey");
        });

        test("returns null with RESET", () => {
            expect(keyReducer("keykey", resetKey())).toEqual(null);
        });

        test("returns state when no action matched", () => {
            expect(keyReducer("keykey", {type: "REVERT_KEY"})).toEqual("keykey");
        });

        test("initialises state if undefined", () => {
            expect(keyReducer(undefined, setGame())).toEqual(null);
        });
    });

    describe("Game reducer", () => {
        test("returns payload with SET", () => {
            expect(gameReducer(null, setGame(2))).toEqual(2);
        });

        test("returns null with RESET", () => {
            expect(gameReducer("1234", resetGame())).toEqual(null);
        });

        test("returns state when no action matched", () => {
            expect(gameReducer("1234", {type: "REVERT_GAME"})).toEqual("1234");
        });

        test("initialises state if undefined", () => {
            expect(gameReducer(undefined, setGame())).toEqual(null);
        });
    });

    describe("Game Mode reducer", () => {
        test("returns payload with SET", () => {
            expect(gameModeReducer(null, setGameMode(GameModes["MATCH_TWO"]))).toEqual(GameModes["MATCH_TWO"]);
        });

        test("returns null with RESET", () => {
            expect(gameModeReducer(GameModes["MATCH_TWO"], resetGameMode())).toEqual(null);
        });

        test("returns state when no action matched", () => {
            expect(gameModeReducer(GameModes["MATCH_TWO"], {type: "REVERT_GAME_MODE"})).toEqual(GameModes["MATCH_TWO"]);
        });

        test("initialises state if undefined", () => {
            expect(gameModeReducer(undefined, setGameMode())).toEqual(null);
        });
    });
});