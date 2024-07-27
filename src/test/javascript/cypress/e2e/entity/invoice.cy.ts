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

describe('Invoice e2e test', () => {
  const invoicePageUrl = '/invoice';
  const invoicePageUrlPattern = new RegExp('/invoice(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const invoiceSample = {"totalAmount":14488.73,"type":"DEBIT","paymentMethod":"CREDIT_CARD"};

  let invoice;
  // let invoiceStatus;
  // let order;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/invoice-statuses',
      body: {"description":"concerning aw"},
    }).then(({ body }) => {
      invoiceStatus = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/orders',
      body: {"type":"SALE","priority":35,"isInternal":false,"note":"funk colossal review","otherInfo":"mostly gosh flamboyant"},
    }).then(({ body }) => {
      order = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/invoices+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/invoices').as('postEntityRequest');
    cy.intercept('DELETE', '/api/invoices/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/shipments', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/invoice-statuses', {
      statusCode: 200,
      body: [invoiceStatus],
    });

    cy.intercept('GET', '/api/orders', {
      statusCode: 200,
      body: [order],
    });

  });
   */

  afterEach(() => {
    if (invoice) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/invoices/${invoice.id}`,
      }).then(() => {
        invoice = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (invoiceStatus) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/invoice-statuses/${invoiceStatus.statusCode}`,
      }).then(() => {
        invoiceStatus = undefined;
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

  it('Invoices menu should load Invoices page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('invoice');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Invoice').should('exist');
    cy.url().should('match', invoicePageUrlPattern);
  });

  describe('Invoice page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(invoicePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Invoice page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/invoice/new$'));
        cy.getEntityCreateUpdateHeading('Invoice');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', invoicePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/invoices',
          body: {
            ...invoiceSample,
            status: invoiceStatus,
            order: order,
          },
        }).then(({ body }) => {
          invoice = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/invoices+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [invoice],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(invoicePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(invoicePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Invoice page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('invoice');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', invoicePageUrlPattern);
      });

      it('edit button click should load edit Invoice page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Invoice');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', invoicePageUrlPattern);
      });

      it('edit button click should load edit Invoice page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Invoice');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', invoicePageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Invoice', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('invoice').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', invoicePageUrlPattern);

        invoice = undefined;
      });
    });
  });

  describe('new Invoice page', () => {
    beforeEach(() => {
      cy.visit(`${invoicePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Invoice');
    });

    it.skip('should create an instance of Invoice', () => {
      cy.get(`[data-cy="totalAmount"]`).type('20782.33');
      cy.get(`[data-cy="totalAmount"]`).should('have.value', '20782.33');

      cy.get(`[data-cy="type"]`).select('OTHER');

      cy.get(`[data-cy="paymentMethod"]`).select('EFT');

      cy.get(`[data-cy="note"]`).type('wrongly');
      cy.get(`[data-cy="note"]`).should('have.value', 'wrongly');

      cy.get(`[data-cy="status"]`).select(1);
      cy.get(`[data-cy="order"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        invoice = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', invoicePageUrlPattern);
    });
  });
});
