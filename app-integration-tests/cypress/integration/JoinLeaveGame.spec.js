import {fillInDialog} from "../support/util";

describe('Join Game ', () => {
    let lobbyName;

    beforeEach(() => {
        lobbyName = Math.floor(Math.random() * 1000);
        cy.visit('/games/public');
    });

    it('user not created', () => {
        cy.createGame(lobbyName);
        cy.get('[data-test=refresh-btn]').click();
        let lobbyCardBtn = cy.contains('.card-title', lobbyName).parent().children('.btn');
        lobbyCardBtn.click();
        cy.get('input[name="nickname"]').type('Jane');
        cy.contains('[data-test="submit-create-player-modal"]', 'Join Game').click();

        cy.get('p[data-test="player-name-0"]').invoke('text').should('eq', 'John 👑');
        cy.get('p[data-test="player-name-1"]').invoke('text').should('eq', 'Jane');
    });

    it('with 4 players max, 3 players already in lobby', () => {
        cy.createGame(lobbyName, "John", 4).joinGame().joinGame().then(data => {
            cy.get('[data-test=refresh-btn]').click();
            cy.get(`[data-test="join-game-btn-${data.gameId}"]`).click();
            cy.get('input[name="nickname"]').type('Jane');

            cy.contains('[data-test="submit-create-player-modal"]', 'Join Game').click();
            cy.get('p[data-test^="player-name"]').contains('Jane');
        });
    });
});

describe('Leave Game ', () => {
    it('as Host', () => {
        cy.visit('/games/public');
        cy.contains('.btn', 'Create Game').click();
        fillInDialog();
        cy.contains('.modal-dialog .btn', 'Create').click({force: true});
        cy.get('p[data-test="player-name-0"]').invoke('text').should('eq', 'John 👑');
        cy.get('[data-test="leave-game-btn"]').click();
        cy.contains('Public games');
    });

    it('as Guest', () => {
        let lobbyName = Math.floor(Math.random() * 1000);
        cy.visit('/games/public');
        cy.createGame(lobbyName);
        cy.get('[data-test=refresh-btn]').click();
        let lobbyCardBtn = cy.contains('.card-title', lobbyName).parent().children('.btn');
        lobbyCardBtn.click();
        cy.get('input[name="nickname"]').type('Jane');
        cy.contains('.modal-dialog .btn', 'Join Game').click();
        cy.get('[data-test="leave-game-btn"]').click();
        cy.contains('Public games');

    });
});

describe('When nickname is stored from joining a game: ', () => {
    before(() => {
        cy.visit('/games/public');
        cy.createGame("Nickname Store Lobby");
        cy.get('[data-test=refresh-btn]').click();
    });

    beforeEach(() => {
        cy.visit('/games/public');
        let lobbyCardBtn = cy.contains('.card-title', "Nickname Store Lobby").parent().children('.btn');
        lobbyCardBtn.click();
        cy.get('input[name="nickname"]').type('Jane');
        cy.contains('.modal-dialog .btn', 'Join Game').click();
        cy.get('[data-test="leave-game-btn"]').click();
    })

    it('user does not need to enter nickname to create game', () => {
        cy.contains('.btn', 'Create Game').click();
        cy.get('input[name="lobbyName"]').type('Jane\'s Lobby');
        cy.get('select[data-test="game-mode-select"]').select('Match Two');
        cy.contains('.modal-dialog .btn', 'Create').click({force: true});
        cy.get('p[data-test="player-name-0"]').invoke('text').should('eq', 'Jane 👑');
    });

    it('user does not need to enter nickname to join game', () => {
        let lobbyCardBtn = cy.contains('.card-title', "Nickname Store Lobby").parent().children('.btn');
        lobbyCardBtn.click();
        cy.get('p[data-test="player-name-1"]').invoke('text').should('eq', 'Jane');
    });
});