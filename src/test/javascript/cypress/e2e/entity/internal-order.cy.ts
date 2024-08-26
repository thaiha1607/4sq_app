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

describe('InternalOrder e2e test', () => {
  const internalOrderPageUrl = '/internal-order';
  const internalOrderPageUrlPattern = new RegExp('/internal-order(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const internalOrderSample = {"type":"OTHER"};

  let internalOrder;
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
      body: {"statusCode":"reply","description":"refusal gold"},
    }).then(({ body }) => {
      orderStatus = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/orders',
      body: {"type":"RETURN","priority":32,"note":"gladly wherever although","otherInfo":"plus um"},
    }).then(({ body }) => {
      order = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/internal-orders+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/internal-orders').as('postEntityRequest');
    cy.intercept('DELETE', '/api/internal-orders/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/internal-order-histories', {
      statusCode: 200,
      body: [],
    });

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
    if (internalOrder) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/internal-orders/${internalOrder.id}`,
      }).then(() => {
        internalOrder = undefined;
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

  it('InternalOrders menu should load InternalOrders page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('internal-order');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('InternalOrder').should('exist');
    cy.url().should('match', internalOrderPageUrlPattern);
  });

  describe('InternalOrder page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(internalOrderPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create InternalOrder page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/internal-order/new$'));
        cy.getEntityCreateUpdateHeading('InternalOrder');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', internalOrderPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/internal-orders',
          body: {
            ...internalOrderSample,
            status: orderStatus,
            rootOrder: order,
          },
        }).then(({ body }) => {
          internalOrder = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/internal-orders+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [internalOrder],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(internalOrderPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(internalOrderPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details InternalOrder page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('internalOrder');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', internalOrderPageUrlPattern);
      });

      it('edit button click should load edit InternalOrder page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('InternalOrder');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', internalOrderPageUrlPattern);
      });

      it.skip('edit button click should load edit InternalOrder page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('InternalOrder');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', internalOrderPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of InternalOrder', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('internalOrder').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', internalOrderPageUrlPattern);

        internalOrder = undefined;
      });
    });
  });

  describe('new InternalOrder page', () => {
    beforeEach(() => {
      cy.visit(`${internalOrderPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('InternalOrder');
    });

    it.skip('should create an instance of InternalOrder', () => {
      cy.get(`[data-cy="type"]`).select('OTHER');

      cy.get(`[data-cy="note"]`).type('rejig under');
      cy.get(`[data-cy="note"]`).should('have.value', 'rejig under');

      cy.get(`[data-cy="status"]`).select(1);
      cy.get(`[data-cy="rootOrder"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        internalOrder = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', internalOrderPageUrlPattern);
    });
  });
});
