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

describe('Product e2e test', () => {
  const productPageUrl = '/product';
  const productPageUrlPattern = new RegExp('/product(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const productSample = { name: 'whoever shadow er', expectedPrice: 29233.89 };

  let product;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/products+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/products').as('postEntityRequest');
    cy.intercept('DELETE', '/api/products/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (product) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/products/${product.id}`,
      }).then(() => {
        product = undefined;
      });
    }
  });

  it('Products menu should load Products page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('product');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Product').should('exist');
    cy.url().should('match', productPageUrlPattern);
  });

  describe('Product page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(productPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Product page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/product/new$'));
        cy.getEntityCreateUpdateHeading('Product');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/products',
          body: productSample,
        }).then(({ body }) => {
          product = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/products+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [product],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(productPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Product page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('product');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productPageUrlPattern);
      });

      it('edit button click should load edit Product page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Product');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productPageUrlPattern);
      });

      it.skip('edit button click should load edit Product page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Product');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productPageUrlPattern);
      });

      it('last delete button click should delete instance of Product', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('product').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productPageUrlPattern);

        product = undefined;
      });
    });
  });

  describe('new Product page', () => {
    beforeEach(() => {
      cy.visit(`${productPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Product');
    });

    it('should create an instance of Product', () => {
      cy.get(`[data-cy="name"]`).type('stark');
      cy.get(`[data-cy="name"]`).should('have.value', 'stark');

      cy.get(`[data-cy="description"]`).type('once');
      cy.get(`[data-cy="description"]`).should('have.value', 'once');

      cy.get(`[data-cy="expectedPrice"]`).type('7416.03');
      cy.get(`[data-cy="expectedPrice"]`).should('have.value', '7416.03');

      cy.get(`[data-cy="provider"]`).type('amidst doubtfully');
      cy.get(`[data-cy="provider"]`).should('have.value', 'amidst doubtfully');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        product = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', productPageUrlPattern);
    });
  });
});
