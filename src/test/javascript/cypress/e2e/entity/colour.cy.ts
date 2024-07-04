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

describe('Colour e2e test', () => {
  const colourPageUrl = '/colour';
  const colourPageUrlPattern = new RegExp('/colour(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const colourSample = { name: 'honest nod quickly', hexCode: '#B6E' };

  let colour;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/colours+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/colours').as('postEntityRequest');
    cy.intercept('DELETE', '/api/colours/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (colour) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/colours/${colour.id}`,
      }).then(() => {
        colour = undefined;
      });
    }
  });

  it('Colours menu should load Colours page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('colour');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Colour').should('exist');
    cy.url().should('match', colourPageUrlPattern);
  });

  describe('Colour page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(colourPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Colour page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/colour/new$'));
        cy.getEntityCreateUpdateHeading('Colour');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', colourPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/colours',
          body: colourSample,
        }).then(({ body }) => {
          colour = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/colours+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [colour],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(colourPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Colour page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('colour');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', colourPageUrlPattern);
      });

      it('edit button click should load edit Colour page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Colour');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', colourPageUrlPattern);
      });

      it('edit button click should load edit Colour page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Colour');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', colourPageUrlPattern);
      });

      it('last delete button click should delete instance of Colour', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('colour').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', colourPageUrlPattern);

        colour = undefined;
      });
    });
  });

  describe('new Colour page', () => {
    beforeEach(() => {
      cy.visit(`${colourPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Colour');
    });

    it('should create an instance of Colour', () => {
      cy.get(`[data-cy="name"]`).type('even');
      cy.get(`[data-cy="name"]`).should('have.value', 'even');

      cy.get(`[data-cy="hexCode"]`).type('#eFB');
      cy.get(`[data-cy="hexCode"]`).should('have.value', '#eFB');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        colour = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', colourPageUrlPattern);
    });
  });
});
