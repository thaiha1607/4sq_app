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

describe('ProductCategory e2e test', () => {
  const productCategoryPageUrl = '/product-category';
  const productCategoryPageUrlPattern = new RegExp('/product-category(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const productCategorySample = {};

  let productCategory;
  let colour;
  let product;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/colours',
      body: { name: 'dunk leap muffled', hexCode: '#Baf889' },
    }).then(({ body }) => {
      colour = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/products',
      body: { name: 'majestic', description: 'overcharge if within', expectedPrice: 26526.95, provider: 'bah since uselessly' },
    }).then(({ body }) => {
      product = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/product-categories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/product-categories').as('postEntityRequest');
    cy.intercept('DELETE', '/api/product-categories/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/colours', {
      statusCode: 200,
      body: [colour],
    });

    cy.intercept('GET', '/api/products', {
      statusCode: 200,
      body: [product],
    });
  });

  afterEach(() => {
    if (productCategory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/product-categories/${productCategory.id}`,
      }).then(() => {
        productCategory = undefined;
      });
    }
  });

  afterEach(() => {
    if (colour) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/colours/${colour.id}`,
      }).then(() => {
        colour = undefined;
      });
    }
    if (product) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/products/${product.id}`,
      }).then(() => {
        product = undefined;
      });
    }
  });

  it('ProductCategories menu should load ProductCategories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('product-category');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProductCategory').should('exist');
    cy.url().should('match', productCategoryPageUrlPattern);
  });

  describe('ProductCategory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(productCategoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ProductCategory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/product-category/new$'));
        cy.getEntityCreateUpdateHeading('ProductCategory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productCategoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/product-categories',
          body: {
            ...productCategorySample,
            colour: colour,
            product: product,
          },
        }).then(({ body }) => {
          productCategory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/product-categories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [productCategory],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(productCategoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ProductCategory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('productCategory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productCategoryPageUrlPattern);
      });

      it('edit button click should load edit ProductCategory page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductCategory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productCategoryPageUrlPattern);
      });

      it.skip('edit button click should load edit ProductCategory page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductCategory');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productCategoryPageUrlPattern);
      });

      it('last delete button click should delete instance of ProductCategory', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('productCategory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productCategoryPageUrlPattern);

        productCategory = undefined;
      });
    });
  });

  describe('new ProductCategory page', () => {
    beforeEach(() => {
      cy.visit(`${productCategoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ProductCategory');
    });

    it('should create an instance of ProductCategory', () => {
      cy.get(`[data-cy="name"]`).type('fooey so');
      cy.get(`[data-cy="name"]`).should('have.value', 'fooey so');

      cy.get(`[data-cy="colour"]`).select(1);
      cy.get(`[data-cy="product"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        productCategory = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', productCategoryPageUrlPattern);
    });
  });
});
