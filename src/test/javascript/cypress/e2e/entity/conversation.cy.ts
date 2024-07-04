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

describe('Conversation e2e test', () => {
  const conversationPageUrl = '/conversation';
  const conversationPageUrlPattern = new RegExp('/conversation(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const conversationSample = {"title":"save unimpressively astride"};

  let conversation;
  // let participant;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/participants',
      body: {"isAdmin":false},
    }).then(({ body }) => {
      participant = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/conversations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/conversations').as('postEntityRequest');
    cy.intercept('DELETE', '/api/conversations/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/participants', {
      statusCode: 200,
      body: [participant],
    });

  });
   */

  afterEach(() => {
    if (conversation) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/conversations/${conversation.id}`,
      }).then(() => {
        conversation = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
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
   */

  it('Conversations menu should load Conversations page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('conversation');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Conversation').should('exist');
    cy.url().should('match', conversationPageUrlPattern);
  });

  describe('Conversation page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(conversationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Conversation page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/conversation/new$'));
        cy.getEntityCreateUpdateHeading('Conversation');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', conversationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/conversations',
          body: {
            ...conversationSample,
            participant: participant,
          },
        }).then(({ body }) => {
          conversation = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/conversations+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [conversation],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(conversationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(conversationPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Conversation page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('conversation');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', conversationPageUrlPattern);
      });

      it('edit button click should load edit Conversation page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Conversation');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', conversationPageUrlPattern);
      });

      it('edit button click should load edit Conversation page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Conversation');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', conversationPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Conversation', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('conversation').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', conversationPageUrlPattern);

        conversation = undefined;
      });
    });
  });

  describe('new Conversation page', () => {
    beforeEach(() => {
      cy.visit(`${conversationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Conversation');
    });

    it.skip('should create an instance of Conversation', () => {
      cy.get(`[data-cy="title"]`).type('simplification');
      cy.get(`[data-cy="title"]`).should('have.value', 'simplification');

      cy.get(`[data-cy="participant"]`).select([0]);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        conversation = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', conversationPageUrlPattern);
    });
  });
});
