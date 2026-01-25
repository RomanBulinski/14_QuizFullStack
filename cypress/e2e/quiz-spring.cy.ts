describe('Spring Quiz E2E Test', () => {
  beforeEach(() => {
    cy.visit('/');
  });

  it('should complete a full Spring quiz with 10 questions', () => {
    // 1. Verify we're on the selection page
    cy.url().should('include', '#/selection');
    cy.contains('Select Your Quiz').should('be.visible');

    // 2. Click Spring tile
    cy.contains('Spring Framework').parent().click();
    cy.contains('Spring Framework').parent().should('have.class', 'selected');

    // 3. Verify 10 questions is selected by default
    cy.contains('10 Questions').should('have.class', 'mat-accent');

    // 4. Click "Start Quiz" button
    cy.contains('Start Quiz').should('not.be.disabled').click();

    // 5. Verify we're on the quiz page
    cy.url().should('include', '#/quiz');
    cy.contains('Question 1 of 10').should('be.visible');

    // 6. Answer all 10 questions
    for (let i = 1; i <= 10; i++) {
      cy.contains(`Question ${i} of 10`).should('be.visible');

      // Select the first option for each question
      cy.get('mat-radio-button').first().click();

      // Click Next or Finish button
      if (i < 10) {
        cy.contains('Next').click();
      } else {
        cy.contains('Finish').click();
      }

      // Wait a bit for transition
      cy.wait(300);
    }

    // 7. Verify we're on the summary page
    cy.url().should('include', '#/summary');
    cy.contains('Quiz Completed!').should('be.visible');
    cy.contains('Spring Quiz Results').should('be.visible');

    // 8. Verify score is displayed
    cy.contains('Score').should('be.visible');
    cy.contains(/\d+%/).should('be.visible');
    cy.contains(/\d+ \/ 10/).should('be.visible');
    cy.contains('Correct Answers').should('be.visible');

    // 9. Verify suggestion message is present
    cy.get('.suggestion').should('be.visible');

    // 10. Verify question review is displayed
    cy.contains('Question Review').should('be.visible');
    cy.get('.question-item').should('have.length', 10);

    // 11. Click "Try Again" button
    cy.contains('Try Again').click();

    // 12. Verify we're back at selection page
    cy.url().should('include', '#/selection');
  });

  it('should allow selecting Angular and 20 questions', () => {
    cy.visit('/');

    // Select Angular
    cy.contains('Angular').parent().click();
    cy.contains('Angular').parent().should('have.class', 'selected');

    // Select 20 questions
    cy.contains('20 Questions').click();
    cy.contains('20 Questions').should('have.class', 'mat-accent');

    // Start quiz
    cy.contains('Start Quiz').click();

    // Verify correct question count
    cy.url().should('include', '#/quiz');
    cy.contains('Question 1 of 20').should('be.visible');
  });

  it('should not allow starting quiz without selecting technology', () => {
    cy.visit('/');

    // Try to click Start Quiz without selecting technology
    cy.contains('Start Quiz').should('be.disabled');
  });

  it('should allow navigating back to previous questions', () => {
    cy.visit('/');

    // Start a Spring quiz
    cy.contains('Spring Framework').parent().click();
    cy.contains('Start Quiz').click();

    // Answer first question
    cy.get('mat-radio-button').first().click();
    cy.contains('Next').click();

    // Verify we're on question 2
    cy.contains('Question 2 of 10').should('be.visible');

    // Click Previous button
    cy.contains('Previous').click();

    // Verify we're back on question 1
    cy.contains('Question 1 of 10').should('be.visible');

    // Verify Previous is disabled on first question
    cy.contains('Previous').should('be.disabled');
  });

  it('should display progress bar correctly', () => {
    cy.visit('/');

    // Start a quiz
    cy.contains('Spring Framework').parent().click();
    cy.contains('Start Quiz').click();

    // Check initial progress
    cy.contains('Progress: 1 / 10').should('be.visible');

    // Answer and move to next question
    cy.get('mat-radio-button').first().click();
    cy.contains('Next').click();

    // Check updated progress
    cy.contains('Progress: 2 / 10').should('be.visible');
  });
});
