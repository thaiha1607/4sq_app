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

describe('WarehouseAssignment e2e test', () => {
  const warehouseAssignmentPageUrl = '/warehouse-assignment';
  const warehouseAssignmentPageUrlPattern = new RegExp('/warehouse-assignment(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const warehouseAssignmentSample = {"status":"FAILED"};

  let warehouseAssignment;
  // let workingUnit;
  // let internalOrder;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/working-units',
      body: {"name":"criminalise ultimately lest","type":"OTHER","imageUri":"melt fooey impish"},
    }).then(({ body }) => {
      workingUnit = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/internal-orders',
      body: {"type":"TRANSFER","note":"amidst cavort frail"},
    }).then(({ body }) => {
      internalOrder = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/warehouse-assignments+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/warehouse-assignments').as('postEntityRequest');
    cy.intercept('DELETE', '/api/warehouse-assignments/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/working-units', {
      statusCode: 200,
      body: [workingUnit],
    });

    cy.intercept('GET', '/api/internal-orders', {
      statusCode: 200,
      body: [internalOrder],
    });

  });
   */

  afterEach(() => {
    if (warehouseAssignment) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/warehouse-assignments/${warehouseAssignment.id}`,
      }).then(() => {
        warehouseAssignment = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (workingUnit) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/working-units/${workingUnit.id}`,
      }).then(() => {
        workingUnit = undefined;
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

  it('WarehouseAssignments menu should load WarehouseAssignments page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('warehouse-assignment');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('WarehouseAssignment').should('exist');
    cy.url().should('match', warehouseAssignmentPageUrlPattern);
  });

  describe('WarehouseAssignment page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(warehouseAssignmentPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create WarehouseAssignment page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/warehouse-assignment/new$'));
        cy.getEntityCreateUpdateHeading('WarehouseAssignment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', warehouseAssignmentPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/warehouse-assignments',
          body: {
            ...warehouseAssignmentSample,
            sourceWorkingUnit: workingUnit,
            internalOrder: internalOrder,
          },
        }).then(({ body }) => {
          warehouseAssignment = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/warehouse-assignments+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [warehouseAssignment],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(warehouseAssignmentPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(warehouseAssignmentPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details WarehouseAssignment page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('warehouseAssignment');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', warehouseAssignmentPageUrlPattern);
      });

      it('edit button click should load edit WarehouseAssignment page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('WarehouseAssignment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', warehouseAssignmentPageUrlPattern);
      });

      it.skip('edit button click should load edit WarehouseAssignment page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('WarehouseAssignment');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', warehouseAssignmentPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of WarehouseAssignment', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('warehouseAssignment').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', warehouseAssignmentPageUrlPattern);

        warehouseAssignment = undefined;
      });
    });
  });

  describe('new WarehouseAssignment page', () => {
    beforeEach(() => {
      cy.visit(`${warehouseAssignmentPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('WarehouseAssignment');
    });

    it.skip('should create an instance of WarehouseAssignment', () => {
      cy.get(`[data-cy="status"]`).select('PENDING');

      cy.get(`[data-cy="note"]`).type('diesel lower since');
      cy.get(`[data-cy="note"]`).should('have.value', 'diesel lower since');

      cy.get(`[data-cy="otherInfo"]`).type('equalize rusty extremely');
      cy.get(`[data-cy="otherInfo"]`).should('have.value', 'equalize rusty extremely');

      cy.get(`[data-cy="sourceWorkingUnit"]`).select(1);
      cy.get(`[data-cy="internalOrder"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        warehouseAssignment = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', warehouseAssignmentPageUrlPattern);
    });
  });
});
