package com.foursquare.server.web.rest;

import static com.foursquare.server.domain.InvoiceAsserts.*;
import static com.foursquare.server.web.rest.TestUtil.createUpdateProxyForBean;
import static com.foursquare.server.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursquare.server.IntegrationTest;
import com.foursquare.server.domain.Invoice;
import com.foursquare.server.domain.Invoice;
import com.foursquare.server.domain.InvoiceStatus;
import com.foursquare.server.domain.Order;
import com.foursquare.server.domain.enumeration.InvoiceType;
import com.foursquare.server.domain.enumeration.PaymentMethod;
import com.foursquare.server.repository.InvoiceRepository;
import com.foursquare.server.service.InvoiceService;
import com.foursquare.server.service.dto.InvoiceDTO;
import com.foursquare.server.service.mapper.InvoiceMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link InvoiceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InvoiceResourceIT {

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_TOTAL_AMOUNT = new BigDecimal(0 - 1);

    private static final InvoiceType DEFAULT_TYPE = InvoiceType.PRO_FORMA;
    private static final InvoiceType UPDATED_TYPE = InvoiceType.REGULAR;

    private static final PaymentMethod DEFAULT_PAYMENT_METHOD = PaymentMethod.CASH;
    private static final PaymentMethod UPDATED_PAYMENT_METHOD = PaymentMethod.EFT;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/invoices";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceRepository invoiceRepositoryMock;

    @Autowired
    private InvoiceMapper invoiceMapper;

    @Mock
    private InvoiceService invoiceServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInvoiceMockMvc;

    private Invoice invoice;

    private Invoice insertedInvoice;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Invoice createEntity(EntityManager em) {
        Invoice invoice = new Invoice()
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .type(DEFAULT_TYPE)
            .paymentMethod(DEFAULT_PAYMENT_METHOD)
            .note(DEFAULT_NOTE);
        // Add required entity
        InvoiceStatus invoiceStatus;
        if (TestUtil.findAll(em, InvoiceStatus.class).isEmpty()) {
            invoiceStatus = InvoiceStatusResourceIT.createEntity(em);
            em.persist(invoiceStatus);
            em.flush();
        } else {
            invoiceStatus = TestUtil.findAll(em, InvoiceStatus.class).get(0);
        }
        invoice.setStatus(invoiceStatus);
        // Add required entity
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            order = OrderResourceIT.createEntity(em);
            em.persist(order);
            em.flush();
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        invoice.setOrder(order);
        return invoice;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Invoice createUpdatedEntity(EntityManager em) {
        Invoice invoice = new Invoice()
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .type(UPDATED_TYPE)
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .note(UPDATED_NOTE);
        // Add required entity
        InvoiceStatus invoiceStatus;
        if (TestUtil.findAll(em, InvoiceStatus.class).isEmpty()) {
            invoiceStatus = InvoiceStatusResourceIT.createUpdatedEntity(em);
            em.persist(invoiceStatus);
            em.flush();
        } else {
            invoiceStatus = TestUtil.findAll(em, InvoiceStatus.class).get(0);
        }
        invoice.setStatus(invoiceStatus);
        // Add required entity
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            order = OrderResourceIT.createUpdatedEntity(em);
            em.persist(order);
            em.flush();
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        invoice.setOrder(order);
        return invoice;
    }

    @BeforeEach
    public void initTest() {
        invoice = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedInvoice != null) {
            invoiceRepository.delete(insertedInvoice);
            insertedInvoice = null;
        }
    }

    @Test
    @Transactional
    void createInvoice() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);
        var returnedInvoiceDTO = om.readValue(
            restInvoiceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InvoiceDTO.class
        );

        // Validate the Invoice in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInvoice = invoiceMapper.toEntity(returnedInvoiceDTO);
        assertInvoiceUpdatableFieldsEquals(returnedInvoice, getPersistedInvoice(returnedInvoice));

        insertedInvoice = returnedInvoice;
    }

    @Test
    @Transactional
    void createInvoiceWithExistingId() throws Exception {
        // Create the Invoice with an existing ID
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInvoiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTotalAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoice.setTotalAmount(null);

        // Create the Invoice, which fails.
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        restInvoiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoice.setType(null);

        // Create the Invoice, which fails.
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        restInvoiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPaymentMethodIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        invoice.setPaymentMethod(null);

        // Create the Invoice, which fails.
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        restInvoiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInvoices() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList
        restInvoiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invoice.getId().toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].paymentMethod").value(hasItem(DEFAULT_PAYMENT_METHOD.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInvoicesWithEagerRelationshipsIsEnabled() throws Exception {
        when(invoiceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInvoiceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(invoiceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInvoicesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(invoiceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInvoiceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(invoiceRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInvoice() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get the invoice
        restInvoiceMockMvc
            .perform(get(ENTITY_API_URL_ID, invoice.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(invoice.getId().toString()))
            .andExpect(jsonPath("$.totalAmount").value(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.paymentMethod").value(DEFAULT_PAYMENT_METHOD.toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getInvoicesByIdFiltering() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        UUID id = invoice.getId();

        defaultInvoiceFiltering("id.equals=" + id, "id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllInvoicesByTotalAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where totalAmount equals to
        defaultInvoiceFiltering("totalAmount.equals=" + DEFAULT_TOTAL_AMOUNT, "totalAmount.equals=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesByTotalAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where totalAmount in
        defaultInvoiceFiltering(
            "totalAmount.in=" + DEFAULT_TOTAL_AMOUNT + "," + UPDATED_TOTAL_AMOUNT,
            "totalAmount.in=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInvoicesByTotalAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where totalAmount is not null
        defaultInvoiceFiltering("totalAmount.specified=true", "totalAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByTotalAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where totalAmount is greater than or equal to
        defaultInvoiceFiltering(
            "totalAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.greaterThanOrEqual=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInvoicesByTotalAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where totalAmount is less than or equal to
        defaultInvoiceFiltering(
            "totalAmount.lessThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.lessThanOrEqual=" + SMALLER_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInvoicesByTotalAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where totalAmount is less than
        defaultInvoiceFiltering("totalAmount.lessThan=" + UPDATED_TOTAL_AMOUNT, "totalAmount.lessThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesByTotalAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where totalAmount is greater than
        defaultInvoiceFiltering("totalAmount.greaterThan=" + SMALLER_TOTAL_AMOUNT, "totalAmount.greaterThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInvoicesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where type equals to
        defaultInvoiceFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllInvoicesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where type in
        defaultInvoiceFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllInvoicesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where type is not null
        defaultInvoiceFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByPaymentMethodIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paymentMethod equals to
        defaultInvoiceFiltering("paymentMethod.equals=" + DEFAULT_PAYMENT_METHOD, "paymentMethod.equals=" + UPDATED_PAYMENT_METHOD);
    }

    @Test
    @Transactional
    void getAllInvoicesByPaymentMethodIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paymentMethod in
        defaultInvoiceFiltering(
            "paymentMethod.in=" + DEFAULT_PAYMENT_METHOD + "," + UPDATED_PAYMENT_METHOD,
            "paymentMethod.in=" + UPDATED_PAYMENT_METHOD
        );
    }

    @Test
    @Transactional
    void getAllInvoicesByPaymentMethodIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where paymentMethod is not null
        defaultInvoiceFiltering("paymentMethod.specified=true", "paymentMethod.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where note equals to
        defaultInvoiceFiltering("note.equals=" + DEFAULT_NOTE, "note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllInvoicesByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where note in
        defaultInvoiceFiltering("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE, "note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllInvoicesByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where note is not null
        defaultInvoiceFiltering("note.specified=true", "note.specified=false");
    }

    @Test
    @Transactional
    void getAllInvoicesByNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where note contains
        defaultInvoiceFiltering("note.contains=" + DEFAULT_NOTE, "note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllInvoicesByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList where note does not contain
        defaultInvoiceFiltering("note.doesNotContain=" + UPDATED_NOTE, "note.doesNotContain=" + DEFAULT_NOTE);
    }

    @Test
    @Transactional
    void getAllInvoicesByStatusIsEqualToSomething() throws Exception {
        InvoiceStatus status;
        if (TestUtil.findAll(em, InvoiceStatus.class).isEmpty()) {
            invoiceRepository.saveAndFlush(invoice);
            status = InvoiceStatusResourceIT.createEntity(em);
        } else {
            status = TestUtil.findAll(em, InvoiceStatus.class).get(0);
        }
        em.persist(status);
        em.flush();
        invoice.setStatus(status);
        invoiceRepository.saveAndFlush(invoice);
        Long statusId = status.getId();
        // Get all the invoiceList where status equals to statusId
        defaultInvoiceShouldBeFound("statusId.equals=" + statusId);

        // Get all the invoiceList where status equals to (statusId + 1)
        defaultInvoiceShouldNotBeFound("statusId.equals=" + (statusId + 1));
    }

    @Test
    @Transactional
    void getAllInvoicesByOrderIsEqualToSomething() throws Exception {
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            invoiceRepository.saveAndFlush(invoice);
            order = OrderResourceIT.createEntity(em);
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        em.persist(order);
        em.flush();
        invoice.setOrder(order);
        invoiceRepository.saveAndFlush(invoice);
        UUID orderId = order.getId();
        // Get all the invoiceList where order equals to orderId
        defaultInvoiceShouldBeFound("orderId.equals=" + orderId);

        // Get all the invoiceList where order equals to UUID.randomUUID()
        defaultInvoiceShouldNotBeFound("orderId.equals=" + UUID.randomUUID());
    }

    @Test
    @Transactional
    void getAllInvoicesByRootInvoiceIsEqualToSomething() throws Exception {
        Invoice rootInvoice;
        if (TestUtil.findAll(em, Invoice.class).isEmpty()) {
            invoiceRepository.saveAndFlush(invoice);
            rootInvoice = InvoiceResourceIT.createEntity(em);
        } else {
            rootInvoice = TestUtil.findAll(em, Invoice.class).get(0);
        }
        em.persist(rootInvoice);
        em.flush();
        invoice.setRootInvoice(rootInvoice);
        invoiceRepository.saveAndFlush(invoice);
        UUID rootInvoiceId = rootInvoice.getId();
        // Get all the invoiceList where rootInvoice equals to rootInvoiceId
        defaultInvoiceShouldBeFound("rootInvoiceId.equals=" + rootInvoiceId);

        // Get all the invoiceList where rootInvoice equals to UUID.randomUUID()
        defaultInvoiceShouldNotBeFound("rootInvoiceId.equals=" + UUID.randomUUID());
    }

    private void defaultInvoiceFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultInvoiceShouldBeFound(shouldBeFound);
        defaultInvoiceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInvoiceShouldBeFound(String filter) throws Exception {
        restInvoiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invoice.getId().toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].paymentMethod").value(hasItem(DEFAULT_PAYMENT_METHOD.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));

        // Check, that the count call also returns 1
        restInvoiceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInvoiceShouldNotBeFound(String filter) throws Exception {
        restInvoiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInvoiceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInvoice() throws Exception {
        // Get the invoice
        restInvoiceMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInvoice() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the invoice
        Invoice updatedInvoice = invoiceRepository.findById(invoice.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInvoice are not directly saved in db
        em.detach(updatedInvoice);
        updatedInvoice.totalAmount(UPDATED_TOTAL_AMOUNT).type(UPDATED_TYPE).paymentMethod(UPDATED_PAYMENT_METHOD).note(UPDATED_NOTE);
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(updatedInvoice);

        restInvoiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, invoiceDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceDTO))
            )
            .andExpect(status().isOk());

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInvoiceToMatchAllProperties(updatedInvoice);
    }

    @Test
    @Transactional
    void putNonExistingInvoice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoice.setId(UUID.randomUUID());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInvoiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, invoiceDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInvoice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoice.setId(UUID.randomUUID());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInvoice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoice.setId(UUID.randomUUID());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(invoiceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInvoiceWithPatch() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the invoice using partial update
        Invoice partialUpdatedInvoice = new Invoice();
        partialUpdatedInvoice.setId(invoice.getId());

        partialUpdatedInvoice.note(UPDATED_NOTE);

        restInvoiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInvoice.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInvoice))
            )
            .andExpect(status().isOk());

        // Validate the Invoice in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInvoiceUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedInvoice, invoice), getPersistedInvoice(invoice));
    }

    @Test
    @Transactional
    void fullUpdateInvoiceWithPatch() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the invoice using partial update
        Invoice partialUpdatedInvoice = new Invoice();
        partialUpdatedInvoice.setId(invoice.getId());

        partialUpdatedInvoice.totalAmount(UPDATED_TOTAL_AMOUNT).type(UPDATED_TYPE).paymentMethod(UPDATED_PAYMENT_METHOD).note(UPDATED_NOTE);

        restInvoiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInvoice.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInvoice))
            )
            .andExpect(status().isOk());

        // Validate the Invoice in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInvoiceUpdatableFieldsEquals(partialUpdatedInvoice, getPersistedInvoice(partialUpdatedInvoice));
    }

    @Test
    @Transactional
    void patchNonExistingInvoice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoice.setId(UUID.randomUUID());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInvoiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, invoiceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(invoiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInvoice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoice.setId(UUID.randomUUID());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(invoiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInvoice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        invoice.setId(UUID.randomUUID());

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvoiceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(invoiceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Invoice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInvoice() throws Exception {
        // Initialize the database
        insertedInvoice = invoiceRepository.saveAndFlush(invoice);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the invoice
        restInvoiceMockMvc
            .perform(delete(ENTITY_API_URL_ID, invoice.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return invoiceRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Invoice getPersistedInvoice(Invoice invoice) {
        return invoiceRepository.findById(invoice.getId()).orElseThrow();
    }

    protected void assertPersistedInvoiceToMatchAllProperties(Invoice expectedInvoice) {
        assertInvoiceAllPropertiesEquals(expectedInvoice, getPersistedInvoice(expectedInvoice));
    }

    protected void assertPersistedInvoiceToMatchUpdatableProperties(Invoice expectedInvoice) {
        assertInvoiceAllUpdatablePropertiesEquals(expectedInvoice, getPersistedInvoice(expectedInvoice));
    }
}
