import {fillInDialog, submitCreateGame} from "../support/util";

describe('Update Game ', () => {
    beforeEach(() => {
        cy.visit('/games/public');
    });

    it('Increase and decrease rounds', () => {
        cy.contains('.btn', 'Create Game').click();
        fillInDialog();
        submitCreateGame();
        cy.get('[data-test="round-number-text"]').should('have.value', 2);
        cy.get('[data-test="numRounds-plus-btn"]').click();
        cy.get('[data-test="round-number-text"]').should('have.value', 4);
        cy.get('[data-test="numRounds-minus-btn"]').click();
        cy.get('[data-test="round-number-text"]').should('have.value', 2);
    });

    it('Guest receives new rounds when host updates rounds', () => {
        cy.createGame("rounds-update-lobby").then(data => {
            cy.get('[data-test=refresh-btn]').click();
            let lobbyCardBtn = cy.contains('.card-title', "rounds-update-lobby").parent().children('.btn');
            lobbyCardBtn.click();
            cy.get('input[name="nickname"]').type('Jane');
            cy.contains('[data-test="submit-create-player-modal"]', 'Join Game').click();

            cy.get('[data-test="round-number-text"]').should('have.value', 2);
            cy.updateGame(data.gameId, data.hostPlayerId, data.hostPlayerKey, 4);

            cy.get('[data-test="round-number-text"]').should('have.value', 4);
        });
    });
});