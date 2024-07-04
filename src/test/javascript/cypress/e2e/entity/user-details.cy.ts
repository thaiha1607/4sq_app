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

describe('UserDetails e2e test', () => {
  const userDetailsPageUrl = '/user-details';
  const userDetailsPageUrlPattern = new RegExp('/user-details(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const userDetailsSample = {};

  let userDetails;
  // let user;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {"login":"xKBZBu","firstName":"Reta","lastName":"Price","email":"Patricia61@hotmail.com","imageUrl":"gosh derivative"},
    }).then(({ body }) => {
      user = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/user-details+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/user-details').as('postEntityRequest');
    cy.intercept('DELETE', '/api/user-details/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

    cy.intercept('GET', '/api/working-units', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (userDetails) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/user-details/${userDetails.id}`,
      }).then(() => {
        userDetails = undefined;
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
  });
   */

  it('UserDetails menu should load UserDetails page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('user-details');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('UserDetails').should('exist');
    cy.url().should('match', userDetailsPageUrlPattern);
  });

  describe('UserDetails page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(userDetailsPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create UserDetails page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/user-details/new$'));
        cy.getEntityCreateUpdateHeading('UserDetails');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userDetailsPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/user-details',
          body: {
            ...userDetailsSample,
            user: user,
          },
        }).then(({ body }) => {
          userDetails = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/user-details+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [userDetails],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(userDetailsPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(userDetailsPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details UserDetails page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('userDetails');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userDetailsPageUrlPattern);
      });

      it('edit button click should load edit UserDetails page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('UserDetails');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userDetailsPageUrlPattern);
      });

      it('edit button click should load edit UserDetails page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('UserDetails');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userDetailsPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of UserDetails', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('userDetails').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userDetailsPageUrlPattern);

        userDetails = undefined;
      });
    });
  });

  describe('new UserDetails page', () => {
    beforeEach(() => {
      cy.visit(`${userDetailsPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('UserDetails');
    });

    it.skip('should create an instance of UserDetails', () => {
      cy.get(`[data-cy="phone"]`).type('+9941945');
      cy.get(`[data-cy="phone"]`).should('have.value', '+9941945');

      cy.get(`[data-cy="user"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        userDetails = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', userDetailsPageUrlPattern);
    });
  });
});
