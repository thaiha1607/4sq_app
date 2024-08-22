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

describe('OrderHistory e2e test', () => {
  const orderHistoryPageUrl = '/order-history';
  const orderHistoryPageUrlPattern = new RegExp('/order-history(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const orderHistorySample = {};

  let orderHistory;
  // let orderStatus;
  // let order;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/order-statuses',
      body: {"statusCode":"crowd psst","description":"er till"},
    }).then(({ body }) => {
      orderStatus = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/orders',
      body: {"type":"RETURN","priority":32,"isInternal":false,"note":"equally","otherInfo":"urgently"},
    }).then(({ body }) => {
      order = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/order-histories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/order-histories').as('postEntityRequest');
    cy.intercept('DELETE', '/api/order-histories/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/order-statuses', {
      statusCode: 200,
      body: [orderStatus],
    });

    cy.intercept('GET', '/api/orders', {
      statusCode: 200,
      body: [order],
    });

  });
   */

  afterEach(() => {
    if (orderHistory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/order-histories/${orderHistory.id}`,
      }).then(() => {
        orderHistory = undefined;
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
    if (order) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/orders/${order.id}`,
      }).then(() => {
        order = undefined;
      });
    }
  });
   */

  it('OrderHistories menu should load OrderHistories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('order-history');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('OrderHistory').should('exist');
    cy.url().should('match', orderHistoryPageUrlPattern);
  });

  describe('OrderHistory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(orderHistoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create OrderHistory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/order-history/new$'));
        cy.getEntityCreateUpdateHeading('OrderHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderHistoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/order-histories',
          body: {
            ...orderHistorySample,
            status: orderStatus,
            order: order,
          },
        }).then(({ body }) => {
          orderHistory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/order-histories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [orderHistory],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(orderHistoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(orderHistoryPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details OrderHistory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('orderHistory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderHistoryPageUrlPattern);
      });

      it('edit button click should load edit OrderHistory page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('OrderHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderHistoryPageUrlPattern);
      });

      it('edit button click should load edit OrderHistory page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('OrderHistory');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderHistoryPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of OrderHistory', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('orderHistory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderHistoryPageUrlPattern);

        orderHistory = undefined;
      });
    });
  });

  describe('new OrderHistory page', () => {
    beforeEach(() => {
      cy.visit(`${orderHistoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('OrderHistory');
    });

    it.skip('should create an instance of OrderHistory', () => {
      cy.get(`[data-cy="comments"]`).type('mmm idiom');
      cy.get(`[data-cy="comments"]`).should('have.value', 'mmm idiom');

      cy.get(`[data-cy="status"]`).select(1);
      cy.get(`[data-cy="order"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        orderHistory = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', orderHistoryPageUrlPattern);
    });
  });
});
