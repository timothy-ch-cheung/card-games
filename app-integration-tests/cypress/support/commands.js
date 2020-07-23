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
Cypress.Commands.add("createGame", (lobbyName, nickname) => {
    cy.fixture('createPlayer.json').then((createPlayer) => {
        if (nickname !== undefined) {
            createPlayer.username = nickname;
        }

        cy.request({
            url: `${Cypress.env('baseUrl')}/player`,
            method: 'POST',
            body: createPlayer,
        })
            .its('body')
            .then((body) => {

                cy.fixture('createGame.json').then((createGame) => {
                        if (lobbyName !== undefined) {
                            createGame.lobbyName = lobbyName;
                        }
                        createGame.host.id = body.id;

                        cy.request({
                            url: `${Cypress.env('baseUrl')}/create`,
                            method: 'POST',
                            body: createGame
                        })
                    }
                );
            })
    })
});