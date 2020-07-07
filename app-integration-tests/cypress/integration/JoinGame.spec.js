describe('Join Game ', () => {
    it('user not created', () => {
        let lobbyName = Math.floor(Math.random() * 1000);
        cy.visit('/games')
        cy.createGame(lobbyName);
        cy.wait(10000)
        let lobbyCardBtn = cy.contains('.card-title', lobbyName).parent().children('.btn');
        lobbyCardBtn.click();
        cy.get('input[name="nickname"]').type('Jane');
        cy.contains('.modal-dialog .btn', 'Join Game').click();

        cy.get('.lobby-banner .host').invoke('text').should('eq', 'HOST: John');
        cy.get('.lobby-banner .guest').invoke('text').should('eq', 'GUEST: Jane');
    });
});