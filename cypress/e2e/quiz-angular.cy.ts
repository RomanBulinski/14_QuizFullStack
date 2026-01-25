describe('Angular Quiz E2E Test', () => {
  beforeEach(() => {
    cy.visit('/');
  });

  it('should complete a full Angular quiz with 30 questions', () => {
    // Select Angular
    cy.contains('Angular').parent().click();

    // Select 30 questions
    cy.contains('30 Questions').click();

    // Start quiz
    cy.contains('Start Quiz').click();

    // Verify we're on quiz page with correct count
    cy.url().should('include', '#/quiz');
    cy.contains('Question 1 of 30').should('be.visible');

    // Answer all 30 questions
    for (let i = 1; i <= 30; i++) {
      cy.contains(`Question ${i} of 30`, { timeout: 10000 }).should('be.visible');

      // Select first option
      cy.get('mat-radio-button').first().click();

      // Click Next or Finish
      if (i < 30) {
        cy.contains('Next').click();
      } else {
        cy.contains('Finish').click();
      }

      cy.wait(200);
    }

    // Verify summary page
    cy.url().should('include', '#/summary');
    cy.contains('Angular Quiz Results').should('be.visible');
    cy.contains(/\d+ \/ 30/).should('be.visible');
    cy.get('.question-item').should('have.length', 30);
  });
});
