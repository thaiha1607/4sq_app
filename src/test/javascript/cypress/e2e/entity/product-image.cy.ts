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

describe('ProductImage e2e test', () => {
  const productImagePageUrl = '/product-image';
  const productImagePageUrlPattern = new RegExp('/product-image(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const productImageSample = {"imageUri":"bawl phew finally"};

  let productImage;
  // let product;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/products',
      body: {"name":"even","description":"above bonfire","provider":"meanwhile wiggly ugh"},
    }).then(({ body }) => {
      product = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/product-images+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/product-images').as('postEntityRequest');
    cy.intercept('DELETE', '/api/product-images/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/products', {
      statusCode: 200,
      body: [product],
    });

  });
   */

  afterEach(() => {
    if (productImage) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/product-images/${productImage.id}`,
      }).then(() => {
        productImage = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
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
   */

  it('ProductImages menu should load ProductImages page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('product-image');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProductImage').should('exist');
    cy.url().should('match', productImagePageUrlPattern);
  });

  describe('ProductImage page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(productImagePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ProductImage page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/product-image/new$'));
        cy.getEntityCreateUpdateHeading('ProductImage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productImagePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/product-images',
          body: {
            ...productImageSample,
            product: product,
          },
        }).then(({ body }) => {
          productImage = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/product-images+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [productImage],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(productImagePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(productImagePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ProductImage page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('productImage');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productImagePageUrlPattern);
      });

      it('edit button click should load edit ProductImage page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductImage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productImagePageUrlPattern);
      });

      it('edit button click should load edit ProductImage page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductImage');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productImagePageUrlPattern);
      });

      it.skip('last delete button click should delete instance of ProductImage', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('productImage').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productImagePageUrlPattern);

        productImage = undefined;
      });
    });
  });

  describe('new ProductImage page', () => {
    beforeEach(() => {
      cy.visit(`${productImagePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ProductImage');
    });

    it.skip('should create an instance of ProductImage', () => {
      cy.get(`[data-cy="imageUri"]`).type('bah from whereas');
      cy.get(`[data-cy="imageUri"]`).should('have.value', 'bah from whereas');

      cy.get(`[data-cy="altText"]`).type('huzzah indeed');
      cy.get(`[data-cy="altText"]`).should('have.value', 'huzzah indeed');

      cy.get(`[data-cy="product"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        productImage = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', productImagePageUrlPattern);
    });
  });
});
