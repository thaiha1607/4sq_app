package com.foursquare.server.service;

import com.foursquare.server.domain.OrderItem;
import com.foursquare.server.repository.OrderItemRepository;
import com.foursquare.server.repository.search.OrderItemSearchRepository;
import com.foursquare.server.service.dto.OrderItemDTO;
import com.foursquare.server.service.mapper.OrderItemMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.foursquare.server.domain.OrderItem}.
 */
@Service
@Transactional
public class OrderItemService {

    private static final Logger log = LoggerFactory.getLogger(OrderItemService.class);

    private final OrderItemRepository orderItemRepository;

    private final OrderItemMapper orderItemMapper;

    private final OrderItemSearchRepository orderItemSearchRepository;

    public OrderItemService(
        OrderItemRepository orderItemRepository,
        OrderItemMapper orderItemMapper,
        OrderItemSearchRepository orderItemSearchRepository
    ) {
        this.orderItemRepository = orderItemRepository;
        this.orderItemMapper = orderItemMapper;
        this.orderItemSearchRepository = orderItemSearchRepository;
    }

    /**
     * Save a orderItem.
     *
     * @param orderItemDTO the entity to save.
     * @return the persisted entity.
     */
    public OrderItemDTO save(OrderItemDTO orderItemDTO) {
        log.debug("Request to save OrderItem : {}", orderItemDTO);
        OrderItem orderItem = orderItemMapper.toEntity(orderItemDTO);
        orderItem = orderItemRepository.save(orderItem);
        orderItemSearchRepository.index(orderItem);
        return orderItemMapper.toDto(orderItem);
    }

    /**
     * Update a orderItem.
     *
     * @param orderItemDTO the entity to save.
     * @return the persisted entity.
     */
    public OrderItemDTO update(OrderItemDTO orderItemDTO) {
        log.debug("Request to update OrderItem : {}", orderItemDTO);
        OrderItem orderItem = orderItemMapper.toEntity(orderItemDTO);
        orderItem.setIsPersisted();
        orderItem = orderItemRepository.save(orderItem);
        orderItemSearchRepository.index(orderItem);
        return orderItemMapper.toDto(orderItem);
    }

    /**
     * Partially update a orderItem.
     *
     * @param orderItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OrderItemDTO> partialUpdate(OrderItemDTO orderItemDTO) {
        log.debug("Request to partially update OrderItem : {}", orderItemDTO);

        return orderItemRepository
            .findById(orderItemDTO.getId())
            .map(existingOrderItem -> {
                orderItemMapper.partialUpdate(existingOrderItem, orderItemDTO);

                return existingOrderItem;
            })
            .map(orderItemRepository::save)
            .map(savedOrderItem -> {
                orderItemSearchRepository.index(savedOrderItem);
                return savedOrderItem;
            })
            .map(orderItemMapper::toDto);
    }

    /**
     * Get all the orderItems.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<OrderItemDTO> findAll() {
        log.debug("Request to get all OrderItems");
        return orderItemRepository.findAll().stream().map(orderItemMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the orderItems with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<OrderItemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return orderItemRepository.findAllWithEagerRelationships(pageable).map(orderItemMapper::toDto);
    }

    /**
     * Get one orderItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OrderItemDTO> findOne(UUID id) {
        log.debug("Request to get OrderItem : {}", id);
        return orderItemRepository.findOneWithEagerRelationships(id).map(orderItemMapper::toDto);
    }

    /**
     * Delete the orderItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete OrderItem : {}", id);
        orderItemRepository.deleteById(id);
        orderItemSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the orderItem corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<OrderItemDTO> search(String query) {
        log.debug("Request to search OrderItems for query {}", query);
        try {
            return StreamSupport.stream(orderItemSearchRepository.search(query).spliterator(), false).map(orderItemMapper::toDto).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
