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

describe('Order e2e test', () => {
  const orderPageUrl = '/order';
  const orderPageUrlPattern = new RegExp('/order(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const orderSample = {"type":"RETURN"};

  let order;
  // let orderItem;
  // let user;
  // let orderStatus;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/order-items',
      body: {"orderedQty":14815,"receivedQty":26641,"unitPrice":24745.94,"note":"amid weird precipitation"},
    }).then(({ body }) => {
      orderItem = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {"login":"Z@KtRWuX\\PY0Cxl\\`sp70\\<TqgIPs\\`con3\\G6xpW","firstName":"Earline","lastName":"Bailey","email":"Easton.Bogan80@gmail.com","imageUrl":"geez misfile"},
    }).then(({ body }) => {
      user = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/order-statuses',
      body: {"description":"however"},
    }).then(({ body }) => {
      orderStatus = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/orders+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/orders').as('postEntityRequest');
    cy.intercept('DELETE', '/api/orders/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/invoices', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/order-items', {
      statusCode: 200,
      body: [orderItem],
    });

    cy.intercept('GET', '/api/orders', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/shipments', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

    cy.intercept('GET', '/api/order-statuses', {
      statusCode: 200,
      body: [orderStatus],
    });

    cy.intercept('GET', '/api/addresses', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (order) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/orders/${order.id}`,
      }).then(() => {
        order = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (orderItem) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/order-items/${orderItem.id}`,
      }).then(() => {
        orderItem = undefined;
      });
    }
    if (user) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      }).then(() => {
        user = undefined;
      });
    }
    if (orderStatus) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/order-statuses/${orderStatus.statusCode}`,
      }).then(() => {
        orderStatus = undefined;
      });
    }
  });
   */

  it('Orders menu should load Orders page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('order');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Order').should('exist');
    cy.url().should('match', orderPageUrlPattern);
  });

  describe('Order page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(orderPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Order page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/order/new$'));
        cy.getEntityCreateUpdateHeading('Order');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/orders',
          body: {
            ...orderSample,
            orderItem: orderItem,
            customer: user,
            status: orderStatus,
          },
        }).then(({ body }) => {
          order = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/orders+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [order],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(orderPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(orderPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Order page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('order');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderPageUrlPattern);
      });

      it('edit button click should load edit Order page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Order');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderPageUrlPattern);
      });

      it('edit button click should load edit Order page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Order');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Order', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('order').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderPageUrlPattern);

        order = undefined;
      });
    });
  });

  describe('new Order page', () => {
    beforeEach(() => {
      cy.visit(`${orderPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Order');
    });

    it.skip('should create an instance of Order', () => {
      cy.get(`[data-cy="type"]`).select('SALE');

      cy.get(`[data-cy="priority"]`).type('30');
      cy.get(`[data-cy="priority"]`).should('have.value', '30');

      cy.get(`[data-cy="isInternal"]`).should('not.be.checked');
      cy.get(`[data-cy="isInternal"]`).click();
      cy.get(`[data-cy="isInternal"]`).should('be.checked');

      cy.get(`[data-cy="note"]`).type('colorfully famously');
      cy.get(`[data-cy="note"]`).should('have.value', 'colorfully famously');

      cy.get(`[data-cy="otherInfo"]`).type('aw mouth');
      cy.get(`[data-cy="otherInfo"]`).should('have.value', 'aw mouth');

      cy.get(`[data-cy="orderItem"]`).select([0]);
      cy.get(`[data-cy="customer"]`).select(1);
      cy.get(`[data-cy="status"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        order = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', orderPageUrlPattern);
    });
  });
});
