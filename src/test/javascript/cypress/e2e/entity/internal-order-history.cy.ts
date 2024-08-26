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

describe('InternalOrderHistory e2e test', () => {
  const internalOrderHistoryPageUrl = '/internal-order-history';
  const internalOrderHistoryPageUrlPattern = new RegExp('/internal-order-history(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const internalOrderHistorySample = {};

  let internalOrderHistory;
  // let orderStatus;
  // let internalOrder;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/order-statuses',
      body: {"statusCode":"apropos bouncy","description":"mid serialise woot"},
    }).then(({ body }) => {
      orderStatus = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/internal-orders',
      body: {"type":"RETURN","note":"scurry torte where"},
    }).then(({ body }) => {
      internalOrder = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/internal-order-histories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/internal-order-histories').as('postEntityRequest');
    cy.intercept('DELETE', '/api/internal-order-histories/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/order-statuses', {
      statusCode: 200,
      body: [orderStatus],
    });

    cy.intercept('GET', '/api/internal-orders', {
      statusCode: 200,
      body: [internalOrder],
    });

  });
   */

  afterEach(() => {
    if (internalOrderHistory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/internal-order-histories/${internalOrderHistory.id}`,
      }).then(() => {
        internalOrderHistory = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (orderStatus) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/order-statuses/${orderStatus.id}`,
      }).then(() => {
        orderStatus = undefined;
      });
    }
    if (internalOrder) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/internal-orders/${internalOrder.id}`,
      }).then(() => {
        internalOrder = undefined;
      });
    }
  });
   */

  it('InternalOrderHistories menu should load InternalOrderHistories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('internal-order-history');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('InternalOrderHistory').should('exist');
    cy.url().should('match', internalOrderHistoryPageUrlPattern);
  });

  describe('InternalOrderHistory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(internalOrderHistoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create InternalOrderHistory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/internal-order-history/new$'));
        cy.getEntityCreateUpdateHeading('InternalOrderHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', internalOrderHistoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/internal-order-histories',
          body: {
            ...internalOrderHistorySample,
            status: orderStatus,
            order: internalOrder,
          },
        }).then(({ body }) => {
          internalOrderHistory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/internal-order-histories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [internalOrderHistory],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(internalOrderHistoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(internalOrderHistoryPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details InternalOrderHistory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('internalOrderHistory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', internalOrderHistoryPageUrlPattern);
      });

      it('edit button click should load edit InternalOrderHistory page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('InternalOrderHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', internalOrderHistoryPageUrlPattern);
      });

      it.skip('edit button click should load edit InternalOrderHistory page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('InternalOrderHistory');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', internalOrderHistoryPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of InternalOrderHistory', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('internalOrderHistory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', internalOrderHistoryPageUrlPattern);

        internalOrderHistory = undefined;
      });
    });
  });

  describe('new InternalOrderHistory page', () => {
    beforeEach(() => {
      cy.visit(`${internalOrderHistoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('InternalOrderHistory');
    });

    it.skip('should create an instance of InternalOrderHistory', () => {
      cy.get(`[data-cy="note"]`).type('loincloth');
      cy.get(`[data-cy="note"]`).should('have.value', 'loincloth');

      cy.get(`[data-cy="status"]`).select(1);
      cy.get(`[data-cy="order"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        internalOrderHistory = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', internalOrderHistoryPageUrlPattern);
    });
  });
});
