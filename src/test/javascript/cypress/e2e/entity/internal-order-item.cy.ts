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

describe('InternalOrderItem e2e test', () => {
  const internalOrderItemPageUrl = '/internal-order-item';
  const internalOrderItemPageUrlPattern = new RegExp('/internal-order-item(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const internalOrderItemSample = {"qty":31350};

  let internalOrderItem;
  // let orderItem;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/order-items',
      body: {"orderedQty":21229,"receivedQty":9596,"unitPrice":17001.92,"note":"incredible honorable"},
    }).then(({ body }) => {
      orderItem = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/internal-order-items+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/internal-order-items').as('postEntityRequest');
    cy.intercept('DELETE', '/api/internal-order-items/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/order-items', {
      statusCode: 200,
      body: [orderItem],
    });

  });
   */

  afterEach(() => {
    if (internalOrderItem) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/internal-order-items/${internalOrderItem.id}`,
      }).then(() => {
        internalOrderItem = undefined;
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
  });
   */

  it('InternalOrderItems menu should load InternalOrderItems page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('internal-order-item');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('InternalOrderItem').should('exist');
    cy.url().should('match', internalOrderItemPageUrlPattern);
  });

  describe('InternalOrderItem page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(internalOrderItemPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create InternalOrderItem page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/internal-order-item/new$'));
        cy.getEntityCreateUpdateHeading('InternalOrderItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', internalOrderItemPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/internal-order-items',
          body: {
            ...internalOrderItemSample,
            orderItem: orderItem,
          },
        }).then(({ body }) => {
          internalOrderItem = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/internal-order-items+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [internalOrderItem],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(internalOrderItemPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(internalOrderItemPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details InternalOrderItem page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('internalOrderItem');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', internalOrderItemPageUrlPattern);
      });

      it('edit button click should load edit InternalOrderItem page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('InternalOrderItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', internalOrderItemPageUrlPattern);
      });

      it.skip('edit button click should load edit InternalOrderItem page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('InternalOrderItem');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', internalOrderItemPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of InternalOrderItem', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('internalOrderItem').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', internalOrderItemPageUrlPattern);

        internalOrderItem = undefined;
      });
    });
  });

  describe('new InternalOrderItem page', () => {
    beforeEach(() => {
      cy.visit(`${internalOrderItemPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('InternalOrderItem');
    });

    it.skip('should create an instance of InternalOrderItem', () => {
      cy.get(`[data-cy="qty"]`).type('28145');
      cy.get(`[data-cy="qty"]`).should('have.value', '28145');

      cy.get(`[data-cy="note"]`).type('rewarding whenever');
      cy.get(`[data-cy="note"]`).should('have.value', 'rewarding whenever');

      cy.get(`[data-cy="orderItem"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        internalOrderItem = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', internalOrderItemPageUrlPattern);
    });
  });
});
