const fillInDialog = (nickname) => {
    cy.get('.modal-dialog').should('be.visible');
    cy.get('input[name="nickname"]').type(nickname? nickname : 'John');
    cy.get('input[name="lobbyName"]').type('John\'s Lobby');
    cy.contains('.modal-dialog .btn','Create').click({force: true});
    cy.get('.modal-dialog').should('not.be.visible');
    cy.get('.lobby-banner .host').invoke('text').should('eq',`HOST: ${nickname? nickname : 'John'}`);
    cy.get('.lobby-banner .guest').invoke('text').should('eq','GUEST: ');
}

describe('Create Game ', () => {
    it('from Home screen', () => {
        cy.visit('/home')
        cy.contains('.btn','Create Game').click();
        fillInDialog();
    });

    it('from Games screen', () => {
        cy.visit('/games')
        cy.contains('.btn','Create Game').click();
        fillInDialog()
    });
});

describe('When nickname is stored from creating a game: ', () => {
    beforeEach(() => {
        cy.visit('/games');
        cy.contains('.btn','Create Game').click();
        fillInDialog('Jane');
        cy.contains('.btn', 'Leave Lobby').click();
    });

    //TODO: Fix primary key constraint bug when leaving game as Host
    // it('user does not need to enter nickname to create game', () => {
    //     cy.contains('.btn','Create Game').click();
    //     cy.get('input[name="lobbyName"]').type('Jane\'s Lobby');
    //     cy.contains('.modal-dialog .btn', 'Create').click({force: true});
    //     cy.get('.lobby-banner .host').invoke('text').should('eq','HOST: Jane');
    // });

    it('user does not need to enter nickname to join game', () => {
        let lobbyName = Math.floor(Math.random() * 1000);
        cy.createGame(lobbyName);
        cy.wait(10000);
        let lobbyCardBtn = cy.contains('.card-title', lobbyName).parent().children('.btn');
        lobbyCardBtn.click();
        cy.get('.lobby-banner .guest').invoke('text').should('eq','GUEST: Jane');
    });
});