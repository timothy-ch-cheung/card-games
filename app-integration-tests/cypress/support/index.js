// ***********************************************************
// This example support/index.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
import './commands'

// Alternatively you can use CommonJS syntax:
// require('./commands')

before(() => {
    cy.writeFile('games_cleanup.txt', '');
    if (Cypress.env('TEST_ENV') === 'PRODUCTION') {
        Cypress.env('serverUrl', Cypress.config().baseUrl);
    } else {
        Cypress.env('serverUrl', 'http://localhost:8080');
    }
});

afterEach(() => {
    cy.url().then((url) => {
        if (url.endsWith('/current-game')) {
            cy.get('[data-test="leave-game-btn"]').click();
        }
    });
});

after(() => {
    cy.readFile('games_cleanup.txt').then((str) => {
        if (str != null) {
            let games = str.split('\n');
            let lastItem = games.slice(-1)[0];
            if (!/^\d+\s[A-Z0-9]+$/.test(lastItem)) {
                games.pop();
            }

            let game;
            for (game of games) {
                game = game.split(" ");
                let gameId = game[0];
                let playerId = game[1];
                let playerKey = game[2]

                cy.leaveGame(gameId, playerId, playerKey);
            }
        }
    });
});
