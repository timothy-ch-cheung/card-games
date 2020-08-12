// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
const queueCleanup = (gameId, playerId, playerKey) => {
    cy.writeFile('games_cleanup.txt', `${gameId} ${playerId} ${playerKey}\n`, { flag: 'a+' })
};

Cypress.Commands.add("createGame", (lobbyName, nickname) => {
    cy.fixture('createPlayer.json').then((createPlayer) => {
        if (nickname !== undefined) {
            createPlayer.username = nickname;
        }

        cy.request({
            url: `${Cypress.env('serverUrl')}/player`,
            method: 'POST',
            body: createPlayer,
        })
            .its('body')
            .then((body) => {

                cy.fixture('createGame.json').then((createGame) => {
                        if (lobbyName !== undefined) {
                            createGame.lobbyName = lobbyName;
                        }
                        let playerId = body.id;
                        let playerKey = body.key;
                        createGame.host.id = playerId;
                        createGame.host.key = playerKey;

                        cy.request({
                            url: `${Cypress.env('serverUrl')}/create`,
                            method: 'POST',
                            body: createGame
                        })
                            .its('body')
                            .then(
                            (body) => {
                                let gameId = body.id;
                                queueCleanup(gameId, playerId, playerKey)
                            }
                        );
                    }
                );
            })
    })
});

