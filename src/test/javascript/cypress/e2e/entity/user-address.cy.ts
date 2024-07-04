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

describe('UserAddress e2e test', () => {
  const userAddressPageUrl = '/user-address';
  const userAddressPageUrlPattern = new RegExp('/user-address(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const userAddressSample = {"type":"OTHER"};

  let userAddress;
  // let user;
  // let address;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {"login":"KLDi-","firstName":"Jaleel","lastName":"Kohler","email":"Stacy.Bailey@yahoo.com","imageUrl":"dressing"},
    }).then(({ body }) => {
      user = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/addresses',
      body: {"line1":"unless umpire defenseless","line2":"sculpt","city":"Franeckiborough","state":"trash","country":"Djibouti","zipOrPostalCode":"amongst"},
    }).then(({ body }) => {
      address = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/user-addresses+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/user-addresses').as('postEntityRequest');
    cy.intercept('DELETE', '/api/user-addresses/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

    cy.intercept('GET', '/api/addresses', {
      statusCode: 200,
      body: [address],
    });

  });
   */

  afterEach(() => {
    if (userAddress) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/user-addresses/${userAddress.id}`,
      }).then(() => {
        userAddress = undefined;
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
    if (address) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/addresses/${address.id}`,
      }).then(() => {
        address = undefined;
      });
    }
  });
   */

  it('UserAddresses menu should load UserAddresses page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('user-address');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('UserAddress').should('exist');
    cy.url().should('match', userAddressPageUrlPattern);
  });

  describe('UserAddress page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(userAddressPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create UserAddress page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/user-address/new$'));
        cy.getEntityCreateUpdateHeading('UserAddress');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userAddressPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/user-addresses',
          body: {
            ...userAddressSample,
            user: user,
            address: address,
          },
        }).then(({ body }) => {
          userAddress = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/user-addresses+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [userAddress],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(userAddressPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(userAddressPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details UserAddress page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('userAddress');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userAddressPageUrlPattern);
      });

      it('edit button click should load edit UserAddress page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('UserAddress');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userAddressPageUrlPattern);
      });

      it('edit button click should load edit UserAddress page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('UserAddress');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userAddressPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of UserAddress', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('userAddress').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userAddressPageUrlPattern);

        userAddress = undefined;
      });
    });
  });

  describe('new UserAddress page', () => {
    beforeEach(() => {
      cy.visit(`${userAddressPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('UserAddress');
    });

    it.skip('should create an instance of UserAddress', () => {
      cy.get(`[data-cy="type"]`).select('BILLING');

      cy.get(`[data-cy="friendlyName"]`).type('kimono lychee');
      cy.get(`[data-cy="friendlyName"]`).should('have.value', 'kimono lychee');

      cy.get(`[data-cy="isDefault"]`).should('not.be.checked');
      cy.get(`[data-cy="isDefault"]`).click();
      cy.get(`[data-cy="isDefault"]`).should('be.checked');

      cy.get(`[data-cy="user"]`).select(1);
      cy.get(`[data-cy="address"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        userAddress = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', userAddressPageUrlPattern);
    });
  });
});
