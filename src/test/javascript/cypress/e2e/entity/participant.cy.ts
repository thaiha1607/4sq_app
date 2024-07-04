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

describe('Participant e2e test', () => {
  const participantPageUrl = '/participant';
  const participantPageUrlPattern = new RegExp('/participant(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const participantSample = {};

  let participant;
  // let user;
  // let conversation;

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
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/conversations',
      body: {"title":"towards hoard along"},
    }).then(({ body }) => {
      conversation = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/participants+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/participants').as('postEntityRequest');
    cy.intercept('DELETE', '/api/participants/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

    cy.intercept('GET', '/api/conversations', {
      statusCode: 200,
      body: [conversation],
    });

    cy.intercept('GET', '/api/messages', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (participant) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/participants/${participant.id}`,
      }).then(() => {
        participant = undefined;
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
    if (conversation) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/conversations/${conversation.id}`,
      }).then(() => {
        conversation = undefined;
      });
    }
  });
   */

  it('Participants menu should load Participants page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('participant');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Participant').should('exist');
    cy.url().should('match', participantPageUrlPattern);
  });

  describe('Participant page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(participantPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Participant page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/participant/new$'));
        cy.getEntityCreateUpdateHeading('Participant');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/participants',
          body: {
            ...participantSample,
            user: user,
            conversation: conversation,
          },
        }).then(({ body }) => {
          participant = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/participants+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [participant],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(participantPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(participantPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Participant page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('participant');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantPageUrlPattern);
      });

      it('edit button click should load edit Participant page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Participant');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantPageUrlPattern);
      });

      it('edit button click should load edit Participant page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Participant');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Participant', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('participant').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', participantPageUrlPattern);

        participant = undefined;
      });
    });
  });

  describe('new Participant page', () => {
    beforeEach(() => {
      cy.visit(`${participantPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Participant');
    });

    it.skip('should create an instance of Participant', () => {
      cy.get(`[data-cy="isAdmin"]`).should('not.be.checked');
      cy.get(`[data-cy="isAdmin"]`).click();
      cy.get(`[data-cy="isAdmin"]`).should('be.checked');

      cy.get(`[data-cy="user"]`).select(1);
      cy.get(`[data-cy="conversation"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        participant = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', participantPageUrlPattern);
    });
  });
});
