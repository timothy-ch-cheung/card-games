describe('Address bar navigation:', () => {
    it('Root', () => {
        cy.visit('')
        cy.contains('Play against a Bot:')
        cy.contains('Play with others:')
    });

    it('Home', () => {
        cy.visit('/home')
        cy.contains('Play against a Bot:')
        cy.contains('Play with others:')
    });

    it('Games', () => {
        cy.visit('/games')
        cy.contains('Public games')
    });
});

describe('Interaction navigation:', () => {
    it('Clicking title on the nav bar brings user to Home page', () => {
        cy.visit('')
        cy.get('.navbar a[href="/"]').click();
        cy.contains('Play against a Bot:');
        cy.contains('Play with others:');
    });

    it('Clicking Home on the nav bar brings user to Home page', () => {
        cy.visit('')
        cy.get('.navbar a[href="/home"]').click();
        cy.contains('Play against a Bot:');
        cy.contains('Play with others:');
    });

    it('Clicking Games on the nav bar brings user to Games page', () => {
        cy.visit('')
        cy.get('.navbar a[href="/games"]').click();
        cy.contains('Public games')
    });

    it('Clicking the Join Game button brings user to Games page', () => {
        cy.visit('')
        cy.get('a[href="/games"].btn').click();
        cy.contains('Public games')
    });
});