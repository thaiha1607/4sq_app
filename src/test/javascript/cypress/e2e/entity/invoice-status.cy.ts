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

describe('InvoiceStatus e2e test', () => {
  const invoiceStatusPageUrl = '/invoice-status';
  const invoiceStatusPageUrlPattern = new RegExp('/invoice-status(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const invoiceStatusSample = { description: 'aha' };

  let invoiceStatus;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/invoice-statuses+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/invoice-statuses').as('postEntityRequest');
    cy.intercept('DELETE', '/api/invoice-statuses/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (invoiceStatus) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/invoice-statuses/${invoiceStatus.statusCode}`,
      }).then(() => {
        invoiceStatus = undefined;
      });
    }
  });

  it('InvoiceStatuses menu should load InvoiceStatuses page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('invoice-status');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('InvoiceStatus').should('exist');
    cy.url().should('match', invoiceStatusPageUrlPattern);
  });

  describe('InvoiceStatus page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(invoiceStatusPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create InvoiceStatus page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/invoice-status/new$'));
        cy.getEntityCreateUpdateHeading('InvoiceStatus');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', invoiceStatusPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/invoice-statuses',
          body: invoiceStatusSample,
        }).then(({ body }) => {
          invoiceStatus = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/invoice-statuses+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [invoiceStatus],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(invoiceStatusPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details InvoiceStatus page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('invoiceStatus');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', invoiceStatusPageUrlPattern);
      });

      it('edit button click should load edit InvoiceStatus page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('InvoiceStatus');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', invoiceStatusPageUrlPattern);
      });

      it('edit button click should load edit InvoiceStatus page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('InvoiceStatus');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', invoiceStatusPageUrlPattern);
      });

      it('last delete button click should delete instance of InvoiceStatus', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('invoiceStatus').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', invoiceStatusPageUrlPattern);

        invoiceStatus = undefined;
      });
    });
  });

  describe('new InvoiceStatus page', () => {
    beforeEach(() => {
      cy.visit(`${invoiceStatusPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('InvoiceStatus');
    });

    it('should create an instance of InvoiceStatus', () => {
      cy.get(`[data-cy="description"]`).type('scrawny');
      cy.get(`[data-cy="description"]`).should('have.value', 'scrawny');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        invoiceStatus = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', invoiceStatusPageUrlPattern);
    });
  });
});
