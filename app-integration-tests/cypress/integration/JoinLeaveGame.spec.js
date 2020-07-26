const fillInDialog = (nickname) => {
    cy.get('input[name="nickname"]').type(nickname? nickname : 'John');
    cy.get('input[name="lobbyName"]').type('John\'s Lobby');
    cy.contains('.modal-dialog .btn', 'Create').click({force: true});
}

describe('Join Game ', () => {
    it('user not created', () => {
        let lobbyName = Math.floor(Math.random() * 1000);
        cy.visit('/games/public');
        cy.createGame(lobbyName);
        cy.get('[data-test=refresh-btn]').click();
        let lobbyCardBtn = cy.contains('.card-title', lobbyName).parent().children('.btn');
        lobbyCardBtn.click();
        cy.get('input[name="nickname"]').type('Jane');
        cy.contains('.modal-dialog .btn', 'Join Game').click();

        cy.get('.lobby-banner .host').invoke('text').should('eq', 'HOST: John');
        cy.get('.lobby-banner .guest').invoke('text').should('eq', 'GUEST: Jane');
    });
});

describe('Leave Game ', () => {
    it('as Host', () => {
        cy.visit('/games/public');
        cy.contains('.btn', 'Create Game').click();
        fillInDialog();
        cy.get('.lobby-banner .host').invoke('text').should('eq', 'HOST: John');
        cy.contains('.btn', 'Leave Lobby').click();
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
        cy.contains('.btn', 'Leave Lobby').click();
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
        cy.contains('.btn', 'Leave Lobby').click();
    })

    it('user does not need to enter nickname to create game', () => {
        cy.contains('.btn','Create Game').click();
        cy.get('input[name="lobbyName"]').type('Jane\'s Lobby');
        cy.contains('.modal-dialog .btn', 'Create').click({force: true});
        cy.get('.lobby-banner .host').invoke('text').should('eq','HOST: Jane');
    });

    it('user does not need to enter nickname to join game', () => {
        let lobbyCardBtn = cy.contains('.card-title', "Nickname Store Lobby").parent().children('.btn');
        lobbyCardBtn.click();
        cy.get('.lobby-banner .guest').invoke('text').should('eq','GUEST: Jane');
    });
});