describe('Create Game ', () => {

    const fillInDialog = () => {
        cy.get('.modal-dialog').should('be.visible');
        cy.get('input[name="nickname"]').type('John');
        cy.get('input[name="lobbyName"]').type('John\'s Lobby');
        cy.contains('.modal-dialog .btn','Create').click({force: true});
        cy.get('.modal-dialog').should('not.be.visible');
        cy.get('.lobby-banner .host').invoke('text').should('eq','HOST: John');
        cy.get('.lobby-banner .guest').invoke('text').should('eq','GUEST: ');
    }

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