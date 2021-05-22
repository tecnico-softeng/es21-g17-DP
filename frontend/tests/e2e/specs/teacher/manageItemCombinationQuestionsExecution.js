describe('Manage Item Combination Questions Walk-through', () => {
    function validateQuestion(title, content) {
        cy.get('[data-cy="showQuestionDialog"]')
            .should('be.visible')
            .within($ls => {
                cy.get('.headline').should('contain', title);
                cy.get('span > p').should('contain', content);
            });
    }

    function validateQuestionFull(title, content) {
        cy.log('Validate question with show dialog.');

        cy.get('[data-cy="questionTitleGrid"]')
            .first()
            .click();

        validateQuestion(title, content);

        cy.get('button')
            .contains('close')
            .click();
    }

    before(() => {
        cy.cleanCodeFillInQuestionsByName('Cypress Question Example');
        cy.cleanMultipleChoiceQuestionsByName('Cypress Question Example');
        cy.cleanItemCombinationQuestionsByName('Cypress Question Example');
    });
    after(() => {
        cy.cleanItemCombinationQuestionsByName('Cypress Question Example');
    });

    beforeEach(() => {
        cy.demoTeacherLogin();
        cy.server();
        cy.route('GET', '/courses/*/questions').as('getQuestions');
        cy.route('GET', '/courses/*/topics').as('getTopics');
        cy.get('[data-cy="managementMenuButton"]').click();
        cy.get('[data-cy="questionsTeacherMenuButton"]').click();

        cy.wait('@getQuestions')
            .its('status')
            .should('eq', 200);

        cy.wait('@getTopics')
            .its('status')
            .should('eq', 200);
    });

    afterEach(() => {
        cy.logout();
    });

    it('Creates a new item combination question', function() {
        cy.get('button')
            .contains('New Question')
            .click();

        cy.get('[data-cy="createOrEditQuestionDialog"]')
            .parent()
            .should('be.visible');

        cy.get('span.headline').should('contain', 'New Question');

        cy.get(
            '[data-cy="questionTitleTextArea"]'
        ).type('Cypress Item Combination Question Example - 1', { force: true });
        cy.get(
            '[data-cy="questionQuestionTextArea"]'
        ).type('Cypress Item Combination Question Example - Content - 1', { force: true });

        cy.get('[data-cy="questionTypeInput"]')
            .type('item_combination', { force: true })
            .click({ force: true });

        cy.wait(1000);

        cy.get('[data-cy="questionItemsInput"]')
            .should('have.length', 1)
            .each(($el, index, $list) => {
            cy.get($el).within($ls => {
                cy.get(`[data-cy="Item${index + 1}"]`).type('Item '+ (index+1));
            });
            });

        cy.get(`[data-cy="itemEditor"]`)
            .should('have.length', 2)
            .each(($el, index, $list) => {
                cy.get($el).within($ls => {
                    if (index === 1) {
                        cy.get(`[data-cy="itemOption"]`).check({force: true})
                    }
                });
            });

        cy.route('POST', '/courses/*/questions/').as('postQuestion');

        cy.get('button')
            .contains('Save')
            .click();

        cy.wait('@postQuestion')
            .its('status')
            .should('eq', 200);

        cy.get('[data-cy="questionTitleGrid"]')
            .first()
            .should('contain', 'Cypress Item Combination Question Example - 1');

        validateQuestionFull(
            'Cypress Item Combination Question Example - 1',
            'Cypress Item Combination Question Example - Content - 1'
        );
    });


    it('Can view question (with click)', function() {
        cy.get('[data-cy="questionTitleGrid"]')
            .first()
            .click();

        cy.wait(1000);

        validateQuestion(
            'Cypress Item Combination Question Example - 1',
            'Cypress Item Combination Question Example - Content - 1'
        );

        cy.get('button')
            .contains('close')
            .click();
    });

    it('Can view question (with button)', function() {
        cy.get('tbody tr')
            .first()
            .within($list => {
                cy.get('button')
                    .contains('visibility')
                    .click();
            });

        cy.wait(1000);

        validateQuestion(
            'Cypress Item Combination Question Example - 1',
            'Cypress Item Combination Question Example - Content - 1'
        );

        cy.get('button')
            .contains('close')
            .click();
    });


    it('Can update title (with right-click)', function() {
        cy.route('PUT', '/questions/*').as('updateQuestion');

        cy.get('[data-cy="questionTitleGrid"]')
            .first()
            .rightclick();

        cy.wait(1000); //making sure codemirror loaded

        cy.get('[data-cy="createOrEditQuestionDialog"]')
            .parent()
            .should('be.visible')
            .within($list => {
                cy.get('span.headline').should('contain', 'Edit Question');

                cy.get('[data-cy="questionTitleTextArea"]')
                    .clear({ force: true })
                    .type('Cypress Item Combination Question Example - 1 - Edited', { force: true });

                cy.get('button')
                    .contains('Save')
                    .click();
            });

        cy.wait('@updateQuestion')
            .its('status')
            .should('eq', 200);

        cy.get('[data-cy="questionTitleGrid"]')
            .first()
            .should('contain', 'Cypress Item Combination Question Example - 1 - Edited');

        validateQuestionFull(
            (title = 'Cypress Item Combination Question Example - 1 - Edited'),
            (content = 'Cypress Item Combination Question Example - Content - 1')
        );
    });

    it('Can update content (with button)', function() {
        cy.route('PUT', '/questions/*').as('updateQuestion');

        cy.get('tbody tr')
            .first()
            .within($list => {
                cy.get('button')
                    .contains('edit')
                    .click();
            });

        cy.wait(1000);

        cy.get('[data-cy="createOrEditQuestionDialog"]')
            .parent()
            .should('be.visible')
            .within($list => {
                cy.get('span.headline').should('contain', 'Edit Question');

                cy.get('[data-cy="questionQuestionTextArea"]')
                    .clear({ force: true })
                    .type('Cypress Item Combination Question Example - Content - 1 - Edited', { force: true });

                cy.get('button')
                    .contains('Save')
                    .click();
            });

        cy.wait('@updateQuestion')
            .its('status')
            .should('eq', 200);

        validateQuestionFull(
            (title = 'Cypress Item Combination Question Example - 1 - Edited'),
            (content = 'Cypress Item Combination Question Example - Content - 1 - Edited')
        );
    });

    it('Can duplicate question', function() {
        cy.get('tbody tr')
            .first()
            .within($list => {
                cy.get('button')
                    .contains('cached')
                    .click();
            });

        cy.wait(1000);

        cy.get('[data-cy="createOrEditQuestionDialog"]')
            .parent()
            .should('be.visible');

        cy.get('span.headline').should('contain', 'New Question');

        cy.get('[data-cy="questionTitleTextArea"]')
            .should('have.value', 'Cypress Item Combination Question Example - 1 - Edited')
            .type('{end} - DUP', { force: true });
        cy.get('[data-cy="questionQuestionTextArea"]').should(
            'have.value',
            'Cypress Item Combination Question Example - Content - 1 - Edited'
        );

        cy.route('POST', '/courses/*/questions/').as('postQuestion');

        cy.wait(1000);

        cy.get('button')
            .contains('Save')
            .click();

        cy.wait('@postQuestion')
            .its('status')
            .should('eq', 200);

        cy.get('[data-cy="questionTitleGrid"]')
            .first()
            .should('contain', 'Cypress Item Combination Question Example - 1 - Edited - DUP');

        validateQuestionFull(
            'Cypress Item Combination Question Example - 1 - Edited - DUP',
            'Cypress Item Combination Question Example - Content - 1 - Edited'
        );
    });

    it('Can delete created question', function() {
        cy.route('DELETE', '/questions/*').as('deleteQuestion');
        cy.get('tbody tr')
            .first()
            .within($list => {
                cy.get('button')
                    .contains('delete')
                    .click();
            });

        cy.wait('@deleteQuestion')
            .its('status')
            .should('eq', 200);
    });
});
