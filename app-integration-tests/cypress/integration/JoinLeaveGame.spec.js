const fillInDialog = () => {
    cy.get('input[name="nickname"]').type('John');
    cy.get('input[name="lobbyName"]').type('John\'s Lobby');
    cy.contains('.modal-dialog .btn', 'Create').click({force: true});
}

describe('Join Game ', () => {
    it('user not created', () => {
        let lobbyName = Math.floor(Math.random() * 1000);
        cy.visit('/games');
        cy.createGame(lobbyName);
        cy.wait(10000);
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
        cy.visit('/games');
        cy.contains('.btn', 'Create Game').click();
        fillInDialog();
        cy.get('.lobby-banner .host').invoke('text').should('eq', 'HOST: John');
        cy.contains('.btn', 'Leave Lobby').click();
        cy.contains('Public games');
    });

    it('as Guest', () => {
        let lobbyName = Math.floor(Math.random() * 1000);
        cy.visit('/games');
        cy.createGame(lobbyName);
        cy.wait(10000);
        let lobbyCardBtn = cy.contains('.card-title', lobbyName).parent().children('.btn');
        lobbyCardBtn.click();
        cy.get('input[name="nickname"]').type('Jane');
        cy.contains('.modal-dialog .btn', 'Join Game').click();
        cy.contains('.btn', 'Leave Lobby').click();
        cy.contains('Public games');

    });
});