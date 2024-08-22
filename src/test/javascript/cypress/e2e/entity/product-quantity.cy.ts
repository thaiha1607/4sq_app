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

describe('ProductQuantity e2e test', () => {
  const productQuantityPageUrl = '/product-quantity';
  const productQuantityPageUrlPattern = new RegExp('/product-quantity(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const productQuantitySample = {"qty":30337};

  let productQuantity;
  // let workingUnit;
  // let productCategory;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/working-units',
      body: {"name":"throughout settlement aha","type":"WAREHOUSE","imageUri":"whether whereas unimportant"},
    }).then(({ body }) => {
      workingUnit = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/product-categories',
      body: {"name":"emancipate trophy"},
    }).then(({ body }) => {
      productCategory = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/product-quantities+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/product-quantities').as('postEntityRequest');
    cy.intercept('DELETE', '/api/product-quantities/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/working-units', {
      statusCode: 200,
      body: [workingUnit],
    });

    cy.intercept('GET', '/api/product-categories', {
      statusCode: 200,
      body: [productCategory],
    });

  });
   */

  afterEach(() => {
    if (productQuantity) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/product-quantities/${productQuantity.id}`,
      }).then(() => {
        productQuantity = undefined;
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
    if (productCategory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/product-categories/${productCategory.id}`,
      }).then(() => {
        productCategory = undefined;
      });
    }
  });
   */

  it('ProductQuantities menu should load ProductQuantities page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('product-quantity');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProductQuantity').should('exist');
    cy.url().should('match', productQuantityPageUrlPattern);
  });

  describe('ProductQuantity page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(productQuantityPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ProductQuantity page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/product-quantity/new$'));
        cy.getEntityCreateUpdateHeading('ProductQuantity');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productQuantityPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/product-quantities',
          body: {
            ...productQuantitySample,
            workingUnit: workingUnit,
            productCategory: productCategory,
          },
        }).then(({ body }) => {
          productQuantity = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/product-quantities+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [productQuantity],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(productQuantityPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(productQuantityPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ProductQuantity page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('productQuantity');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productQuantityPageUrlPattern);
      });

      it('edit button click should load edit ProductQuantity page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductQuantity');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productQuantityPageUrlPattern);
      });

      it.skip('edit button click should load edit ProductQuantity page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductQuantity');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productQuantityPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of ProductQuantity', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('productQuantity').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productQuantityPageUrlPattern);

        productQuantity = undefined;
      });
    });
  });

  describe('new ProductQuantity page', () => {
    beforeEach(() => {
      cy.visit(`${productQuantityPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ProductQuantity');
    });

    it.skip('should create an instance of ProductQuantity', () => {
      cy.get(`[data-cy="qty"]`).type('912');
      cy.get(`[data-cy="qty"]`).should('have.value', '912');

      cy.get(`[data-cy="workingUnit"]`).select(1);
      cy.get(`[data-cy="productCategory"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        productQuantity = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', productQuantityPageUrlPattern);
    });
  });
});
