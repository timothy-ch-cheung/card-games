describe('Lobby ', () => {
    describe('as guest, ', () => {
        it('host leaves the game while guest is still in lobby', () => {
            cy.visit('/games/public');
            cy.createGame("Host will leave", "John", 2, false).then(data => {
                cy.get('[data-test=refresh-btn]').click();
                cy.get(`[data-test="join-game-btn-${data.gameId}"]`).click();
                cy.get('input[name="nickname"]').type('Jane');
                cy.contains('[data-test="submit-create-player-modal"]', 'Join Game').click();

                cy.leaveGame(data.gameId, data.hostPlayerId, data.hostPlayerKey);
                cy.contains('Public games')
            });
        });
    });
});