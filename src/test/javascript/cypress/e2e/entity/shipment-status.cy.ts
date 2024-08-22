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

describe('ShipmentStatus e2e test', () => {
  const shipmentStatusPageUrl = '/shipment-status';
  const shipmentStatusPageUrlPattern = new RegExp('/shipment-status(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const shipmentStatusSample = { statusCode: 'drat beneath' };

  let shipmentStatus;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/shipment-statuses+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/shipment-statuses').as('postEntityRequest');
    cy.intercept('DELETE', '/api/shipment-statuses/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (shipmentStatus) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/shipment-statuses/${shipmentStatus.id}`,
      }).then(() => {
        shipmentStatus = undefined;
      });
    }
  });

  it('ShipmentStatuses menu should load ShipmentStatuses page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('shipment-status');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ShipmentStatus').should('exist');
    cy.url().should('match', shipmentStatusPageUrlPattern);
  });

  describe('ShipmentStatus page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(shipmentStatusPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ShipmentStatus page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/shipment-status/new$'));
        cy.getEntityCreateUpdateHeading('ShipmentStatus');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentStatusPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/shipment-statuses',
          body: shipmentStatusSample,
        }).then(({ body }) => {
          shipmentStatus = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/shipment-statuses+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [shipmentStatus],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(shipmentStatusPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ShipmentStatus page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('shipmentStatus');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentStatusPageUrlPattern);
      });

      it('edit button click should load edit ShipmentStatus page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ShipmentStatus');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentStatusPageUrlPattern);
      });

      it.skip('edit button click should load edit ShipmentStatus page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ShipmentStatus');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentStatusPageUrlPattern);
      });

      it('last delete button click should delete instance of ShipmentStatus', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('shipmentStatus').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentStatusPageUrlPattern);

        shipmentStatus = undefined;
      });
    });
  });

  describe('new ShipmentStatus page', () => {
    beforeEach(() => {
      cy.visit(`${shipmentStatusPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ShipmentStatus');
    });

    it('should create an instance of ShipmentStatus', () => {
      cy.get(`[data-cy="statusCode"]`).type('next');
      cy.get(`[data-cy="statusCode"]`).should('have.value', 'next');

      cy.get(`[data-cy="description"]`).type('meh');
      cy.get(`[data-cy="description"]`).should('have.value', 'meh');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        shipmentStatus = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', shipmentStatusPageUrlPattern);
    });
  });
});
