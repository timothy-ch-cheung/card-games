describe('Lobby ', () => {
    describe('as guest, ', () => {
        it('host leaves the game while guest is still in lobby', () => {
            cy.visit('/games/public');
            cy.createGame("Host will leave", "John", 2, false).then(data => {
                cy.get('[data-test=refresh-btn]').click();
                cy.get(`[data-test="join-game-btn-${data.gameId}"]`).click();
                cy.get('input[name="nickname"]').type('Jane');
                cy.contains('[data-test="submit-create-player-modal"]', 'Join Game').click();

                cy.wait(100);
                cy.leaveGame(data.gameId, data.hostPlayerId, data.hostPlayerKey);
                cy.contains('Public games');
            });
        });

        it('another guest joining game updates player list', () => {
            cy.visit('/games/public');
            cy.createGame("Another will join", "John", 3).then(data => {
                cy.get('[data-test=refresh-btn]').click();
                let lobbyCardBtn = cy.contains('.card-title', "Another will join").parent().children('.btn');
                lobbyCardBtn.click();
                cy.get('input[name="nickname"]').type('Jane');
                cy.contains('.modal-dialog .btn', 'Join Game').click();

                cy.get('[data-test^="player-name"]').should('have.length', 2);
                cy.joinGame(data.gameId);
                cy.get('[data-test^="player-name"]').should('have.length', 3);
            });
        });
    });
});