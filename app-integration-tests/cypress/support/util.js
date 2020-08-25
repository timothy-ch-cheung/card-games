export const fillInDialog = (nickname) => {
    cy.get('.modal-dialog').should('be.visible');
    cy.get('input[name="nickname"]').type(nickname ? nickname : 'John');
    cy.get('input[name="lobbyName"]').type('John\'s Lobby');
    cy.get('select[data-test="game-mode-select"]').select('Match Two');
}

export const submitCreateGame = (nickname) => {
    cy.contains('.modal-dialog .btn', 'Create').click({force: true});
    cy.get('.modal-dialog').should('not.be.visible');
    cy.get('p[data-test="player-name-0"]').invoke('text').should('eq', `${nickname ? nickname : 'John'} 👑`);
}