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

describe('ShipmentAssignment e2e test', () => {
  const shipmentAssignmentPageUrl = '/shipment-assignment';
  const shipmentAssignmentPageUrlPattern = new RegExp('/shipment-assignment(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const shipmentAssignmentSample = {"status":"CANCELLED"};

  let shipmentAssignment;
  // let user;
  // let shipment;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {"login":"5_@3YS\\9Wpel\\!xfXZh","firstName":"Lauriane","lastName":"Stanton","email":"Margaretta.Hauck31@hotmail.com","imageUrl":"certainly excepting"},
    }).then(({ body }) => {
      user = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/shipments',
      body: {"type":"TRANSFER","shipmentDate":"2024-07-03T15:18:44.765Z","note":"define bah applaud"},
    }).then(({ body }) => {
      shipment = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/shipment-assignments+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/shipment-assignments').as('postEntityRequest');
    cy.intercept('DELETE', '/api/shipment-assignments/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

    cy.intercept('GET', '/api/shipments', {
      statusCode: 200,
      body: [shipment],
    });

  });
   */

  afterEach(() => {
    if (shipmentAssignment) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/shipment-assignments/${shipmentAssignment.id}`,
      }).then(() => {
        shipmentAssignment = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (user) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      }).then(() => {
        user = undefined;
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

  it('ShipmentAssignments menu should load ShipmentAssignments page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('shipment-assignment');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ShipmentAssignment').should('exist');
    cy.url().should('match', shipmentAssignmentPageUrlPattern);
  });

  describe('ShipmentAssignment page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(shipmentAssignmentPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ShipmentAssignment page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/shipment-assignment/new$'));
        cy.getEntityCreateUpdateHeading('ShipmentAssignment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentAssignmentPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/shipment-assignments',
          body: {
            ...shipmentAssignmentSample,
            user: user,
            shipment: shipment,
          },
        }).then(({ body }) => {
          shipmentAssignment = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/shipment-assignments+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [shipmentAssignment],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(shipmentAssignmentPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(shipmentAssignmentPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ShipmentAssignment page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('shipmentAssignment');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentAssignmentPageUrlPattern);
      });

      it('edit button click should load edit ShipmentAssignment page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ShipmentAssignment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentAssignmentPageUrlPattern);
      });

      it('edit button click should load edit ShipmentAssignment page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ShipmentAssignment');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentAssignmentPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of ShipmentAssignment', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('shipmentAssignment').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shipmentAssignmentPageUrlPattern);

        shipmentAssignment = undefined;
      });
    });
  });

  describe('new ShipmentAssignment page', () => {
    beforeEach(() => {
      cy.visit(`${shipmentAssignmentPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ShipmentAssignment');
    });

    it.skip('should create an instance of ShipmentAssignment', () => {
      cy.get(`[data-cy="status"]`).select('COMPLETED');

      cy.get(`[data-cy="note"]`).type('which');
      cy.get(`[data-cy="note"]`).should('have.value', 'which');

      cy.get(`[data-cy="user"]`).select(1);
      cy.get(`[data-cy="shipment"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        shipmentAssignment = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', shipmentAssignmentPageUrlPattern);
    });
  });
});
