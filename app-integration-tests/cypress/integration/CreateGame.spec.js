const fillInDialog = (nickname) => {
    cy.get('.modal-dialog').should('be.visible');
    cy.get('input[name="nickname"]').type(nickname ? nickname : 'John');
    cy.get('input[name="lobbyName"]').type('John\'s Lobby');
    cy.get('select[data-test="game-mode-select"]').select('Match Two');
}

const submitCreateGame = (nickname) => {
    cy.contains('.modal-dialog .btn', 'Create').click({force: true});
    cy.get('.modal-dialog').should('not.be.visible');
    cy.get('p[data-test="player-name-0"]').invoke('text').should('eq', `${nickname ? nickname : 'John'} 👑`);
}

describe('Create Game ', () => {
    it('from Home screen', () => {
        cy.visit('/home');
        cy.contains('.btn', 'Create Game').click();
        fillInDialog();
        submitCreateGame();
    });

    it('from Games screen', () => {
        cy.visit('/games/public');
        cy.contains('.btn', 'Create Game').click();
        fillInDialog();
        submitCreateGame();
    });

    it('change number of players', () => {
        cy.visit('/home');
        cy.contains('.btn', 'Create Game').click();
        fillInDialog();
        cy.get('[data-test="numPlayers-plus-btn"]').click();
        cy.get('[data-test="numPlayers-minus-btn"]').click();
        cy.get('[data-test="numPlayers-plus-btn"]').click();
        cy.get('[data-test="numPlayers-value"]').should('have.value', "3");
        submitCreateGame();
    });
});

describe('When nickname is stored from creating a game: ', () => {
    beforeEach(() => {
        cy.visit('/games/public');
        cy.contains('.btn', 'Create Game').click();
        fillInDialog('Jane');
        submitCreateGame('Jane');
        cy.get('[data-test="leave-game-btn"]').click();
    });

    it('user does not need to enter nickname to create game', () => {
        cy.contains('.btn', 'Create Game').click();
        cy.get('input[name="lobbyName"]').type('Jane\'s Lobby');
        cy.get('select[data-test="game-mode-select"]').select('Match Two');
        cy.contains('.modal-dialog .btn', 'Create').click({force: true});
        cy.get('p[data-test="player-name-0"]').invoke('text').should('eq', 'Jane 👑');
    });

    it('user does not need to enter nickname to join game', () => {
        let lobbyName = Math.floor(Math.random() * 1000);
        cy.createGame(lobbyName);
        cy.get('[data-test=refresh-btn]').click();
        let lobbyCardBtn = cy.contains('.card-title', lobbyName).parent().children('.btn');
        lobbyCardBtn.click();
        cy.get('p[data-test="player-name-1"]').invoke('text').should('eq', 'Jane');
    });
});