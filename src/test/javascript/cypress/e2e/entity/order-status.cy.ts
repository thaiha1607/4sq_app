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

describe('OrderStatus e2e test', () => {
  const orderStatusPageUrl = '/order-status';
  const orderStatusPageUrlPattern = new RegExp('/order-status(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const orderStatusSample = { statusCode: 'label provided' };

  let orderStatus;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/order-statuses+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/order-statuses').as('postEntityRequest');
    cy.intercept('DELETE', '/api/order-statuses/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (orderStatus) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/order-statuses/${orderStatus.id}`,
      }).then(() => {
        orderStatus = undefined;
      });
    }
  });

  it('OrderStatuses menu should load OrderStatuses page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('order-status');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('OrderStatus').should('exist');
    cy.url().should('match', orderStatusPageUrlPattern);
  });

  describe('OrderStatus page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(orderStatusPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create OrderStatus page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/order-status/new$'));
        cy.getEntityCreateUpdateHeading('OrderStatus');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderStatusPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/order-statuses',
          body: orderStatusSample,
        }).then(({ body }) => {
          orderStatus = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/order-statuses+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [orderStatus],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(orderStatusPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details OrderStatus page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('orderStatus');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderStatusPageUrlPattern);
      });

      it('edit button click should load edit OrderStatus page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('OrderStatus');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderStatusPageUrlPattern);
      });

      it.skip('edit button click should load edit OrderStatus page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('OrderStatus');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderStatusPageUrlPattern);
      });

      it('last delete button click should delete instance of OrderStatus', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('orderStatus').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderStatusPageUrlPattern);

        orderStatus = undefined;
      });
    });
  });

  describe('new OrderStatus page', () => {
    beforeEach(() => {
      cy.visit(`${orderStatusPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('OrderStatus');
    });

    it('should create an instance of OrderStatus', () => {
      cy.get(`[data-cy="statusCode"]`).type('cow but');
      cy.get(`[data-cy="statusCode"]`).should('have.value', 'cow but');

      cy.get(`[data-cy="description"]`).type('when equally until');
      cy.get(`[data-cy="description"]`).should('have.value', 'when equally until');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        orderStatus = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', orderStatusPageUrlPattern);
    });
  });
});
