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

describe('StaffInfo e2e test', () => {
  const staffInfoPageUrl = '/staff-info';
  const staffInfoPageUrlPattern = new RegExp('/staff-info(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const staffInfoSample = {"status":"INACTIVE"};

  let staffInfo;
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
      body: {"login":"5","firstName":"Dimitri","lastName":"Lockman","email":"Buddy.Kihn76@hotmail.com","imageUrl":"phooey"},
    }).then(({ body }) => {
      user = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/staff-infos+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/staff-infos').as('postEntityRequest');
    cy.intercept('DELETE', '/api/staff-infos/*').as('deleteEntityRequest');
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
    if (staffInfo) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/staff-infos/${staffInfo.id}`,
      }).then(() => {
        staffInfo = undefined;
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

  it('StaffInfos menu should load StaffInfos page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('staff-info');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('StaffInfo').should('exist');
    cy.url().should('match', staffInfoPageUrlPattern);
  });

  describe('StaffInfo page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(staffInfoPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create StaffInfo page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/staff-info/new$'));
        cy.getEntityCreateUpdateHeading('StaffInfo');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', staffInfoPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/staff-infos',
          body: {
            ...staffInfoSample,
            user: user,
          },
        }).then(({ body }) => {
          staffInfo = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/staff-infos+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [staffInfo],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(staffInfoPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(staffInfoPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details StaffInfo page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('staffInfo');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', staffInfoPageUrlPattern);
      });

      it('edit button click should load edit StaffInfo page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StaffInfo');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', staffInfoPageUrlPattern);
      });

      it.skip('edit button click should load edit StaffInfo page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StaffInfo');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', staffInfoPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of StaffInfo', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('staffInfo').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', staffInfoPageUrlPattern);

        staffInfo = undefined;
      });
    });
  });

  describe('new StaffInfo page', () => {
    beforeEach(() => {
      cy.visit(`${staffInfoPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('StaffInfo');
    });

    it.skip('should create an instance of StaffInfo', () => {
      cy.get(`[data-cy="status"]`).select('INACTIVE');

      cy.get(`[data-cy="user"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        staffInfo = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', staffInfoPageUrlPattern);
    });
  });
});
