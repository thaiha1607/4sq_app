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

describe('ShipmentItem e2e test', () => {
  const shipmentItemPageUrl = '/shipment-item';
  const shipmentItemPageUrlPattern = new RegExp('/shipment-item(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const shipmentItemSample = {"qty":14995,"total":18993.73,"rollQty":22743};

  let shipmentItem;
  // let orderItem;
  // let shipment;

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
      url: '/api/shipments',
      body: {"type":"INBOUND","shipmentDate":"2024-08-21T02:26:23.653Z","deliveryDate":"2024-08-21T03:08:42.863Z","note":"applaud nor"},
    }).then(({ body }) => {
      shipment = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/shipment-items+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/shipment-items').as('postEntityRequest');
    cy.intercept('DELETE', '/api/shipment-items/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/order-items', {
      statusCode: 200,
      body: [orderItem],
    });

    cy.intercept('GET', '/api/shipments', {
      statusCode: 200,
      body: [shipment],
    });

  });
   */

  afterEach(() => {
    if (shipmentItem) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/shipment-items/${shipmentItem.id}`,
      }).then(() => {
        shipmentItem = undefined;
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
    if (shipment) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/shipments/${shipment.id}`,
      }).then(() => {
        shipment = undefined;
      });
    }
  });
   */

  it('ShipmentItems menu should load ShipmentItems page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('shipment-item');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ShipmentItem').should('exist');
    cy.url().should('match', shipmentItemPageUrlPattern);
  });

  describe('ShipmentItem page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(shipmentItemPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ShipmentItem page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/shipment-item/new$'));
        cy.getEntityCreateUpdateHeading('ShipmentItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentItemPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/shipment-items',
          body: {
            ...shipmentItemSample,
            orderItem: orderItem,
            shipment: shipment,
          },
        }).then(({ body }) => {
          shipmentItem = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/shipment-items+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [shipmentItem],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(shipmentItemPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(shipmentItemPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ShipmentItem page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('shipmentItem');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentItemPageUrlPattern);
      });

      it('edit button click should load edit ShipmentItem page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ShipmentItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentItemPageUrlPattern);
      });

      it.skip('edit button click should load edit ShipmentItem page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ShipmentItem');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentItemPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of ShipmentItem', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('shipmentItem').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentItemPageUrlPattern);

        shipmentItem = undefined;
      });
    });
  });

  describe('new ShipmentItem page', () => {
    beforeEach(() => {
      cy.visit(`${shipmentItemPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ShipmentItem');
    });

    it.skip('should create an instance of ShipmentItem', () => {
      cy.get(`[data-cy="qty"]`).type('27003');
      cy.get(`[data-cy="qty"]`).should('have.value', '27003');

      cy.get(`[data-cy="total"]`).type('14459.21');
      cy.get(`[data-cy="total"]`).should('have.value', '14459.21');

      cy.get(`[data-cy="rollQty"]`).type('2202');
      cy.get(`[data-cy="rollQty"]`).should('have.value', '2202');

      cy.get(`[data-cy="orderItem"]`).select(1);
      cy.get(`[data-cy="shipment"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        shipmentItem = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', shipmentItemPageUrlPattern);
    });
  });
});
