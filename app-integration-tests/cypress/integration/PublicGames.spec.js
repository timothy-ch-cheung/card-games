describe('Games Page:', () => {
    it('refresh button should load new public games', () => {
        cy.visit('/games/public');
        cy.createGame('new game');
        cy.get('[data-test=refresh-btn]').click();
        cy.get('[data-test=lobby-cards-container]').find('.lobby-card')
            .then(lobbyCard => {
                const numCards = lobbyCard.length;
                cy.createGame('new game');
                cy.get('[data-test=refresh-btn]').click();
                cy.get('.container').find('.lobby-card').should('have.length', numCards + 1);
            });
    });

    it('page should automatically load new public games', () => {
        cy.visit('/games/public');
        cy.createGame('new game');
        cy.get('[data-test=refresh-btn]').click();
        cy.get('[data-test=lobby-cards-container]').find('.lobby-card')
            .then(lobbyCard => {
                const numCards = lobbyCard.length;
                cy.createGame('new game');
                cy.wait(11000);
                cy.get('.container').find('.lobby-card').should('have.length', numCards + 1);
            });
    });
});