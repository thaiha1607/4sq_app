import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('WorkingUnit e2e test', () => {
  const workingUnitPageUrl = '/working-unit';
  const workingUnitPageUrlPattern = new RegExp('/working-unit(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const workingUnitSample = { name: 'per biopsy', type: 'WAREHOUSE' };

  let workingUnit;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/working-units+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/working-units').as('postEntityRequest');
    cy.intercept('DELETE', '/api/working-units/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (workingUnit) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/working-units/${workingUnit.id}`,
      }).then(() => {
        workingUnit = undefined;
      });
    }
  });

  it('WorkingUnits menu should load WorkingUnits page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('working-unit');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('WorkingUnit').should('exist');
    cy.url().should('match', workingUnitPageUrlPattern);
  });

  describe('WorkingUnit page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(workingUnitPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create WorkingUnit page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/working-unit/new$'));
        cy.getEntityCreateUpdateHeading('WorkingUnit');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', workingUnitPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/working-units',
          body: workingUnitSample,
        }).then(({ body }) => {
          workingUnit = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/working-units+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [workingUnit],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(workingUnitPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details WorkingUnit page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('workingUnit');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', workingUnitPageUrlPattern);
      });

      it('edit button click should load edit WorkingUnit page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('WorkingUnit');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', workingUnitPageUrlPattern);
      });

      it.skip('edit button click should load edit WorkingUnit page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('WorkingUnit');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', workingUnitPageUrlPattern);
      });

      it('last delete button click should delete instance of WorkingUnit', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('workingUnit').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', workingUnitPageUrlPattern);

        workingUnit = undefined;
      });
    });
  });

  describe('new WorkingUnit page', () => {
    beforeEach(() => {
      cy.visit(`${workingUnitPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('WorkingUnit');
    });

    it('should create an instance of WorkingUnit', () => {
      cy.get(`[data-cy="name"]`).type('bottom er undercut');
      cy.get(`[data-cy="name"]`).should('have.value', 'bottom er undercut');

      cy.get(`[data-cy="type"]`).select('WAREHOUSE');

      cy.get(`[data-cy="imageUri"]`).type('aged privilege');
      cy.get(`[data-cy="imageUri"]`).should('have.value', 'aged privilege');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        workingUnit = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', workingUnitPageUrlPattern);
    });
  });
});
