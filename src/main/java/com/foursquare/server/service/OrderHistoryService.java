package com.foursquare.server.service;

import com.foursquare.server.domain.OrderHistory;
import com.foursquare.server.repository.OrderHistoryRepository;
import com.foursquare.server.service.dto.OrderHistoryDTO;
import com.foursquare.server.service.mapper.OrderHistoryMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.foursquare.server.domain.OrderHistory}.
 */
@Service
@Transactional
public class OrderHistoryService {

    private static final Logger log = LoggerFactory.getLogger(OrderHistoryService.class);

    private final OrderHistoryRepository orderHistoryRepository;

    private final OrderHistoryMapper orderHistoryMapper;

    public OrderHistoryService(OrderHistoryRepository orderHistoryRepository, OrderHistoryMapper orderHistoryMapper) {
        this.orderHistoryRepository = orderHistoryRepository;
        this.orderHistoryMapper = orderHistoryMapper;
    }

    /**
     * Save a orderHistory.
     *
     * @param orderHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public OrderHistoryDTO save(OrderHistoryDTO orderHistoryDTO) {
        log.debug("Request to save OrderHistory : {}", orderHistoryDTO);
        OrderHistory orderHistory = orderHistoryMapper.toEntity(orderHistoryDTO);
        orderHistory = orderHistoryRepository.save(orderHistory);
        return orderHistoryMapper.toDto(orderHistory);
    }

    /**
     * Update a orderHistory.
     *
     * @param orderHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public OrderHistoryDTO update(OrderHistoryDTO orderHistoryDTO) {
        log.debug("Request to update OrderHistory : {}", orderHistoryDTO);
        OrderHistory orderHistory = orderHistoryMapper.toEntity(orderHistoryDTO);
        orderHistory.setIsPersisted();
        orderHistory = orderHistoryRepository.save(orderHistory);
        return orderHistoryMapper.toDto(orderHistory);
    }

    /**
     * Partially update a orderHistory.
     *
     * @param orderHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OrderHistoryDTO> partialUpdate(OrderHistoryDTO orderHistoryDTO) {
        log.debug("Request to partially update OrderHistory : {}", orderHistoryDTO);

        return orderHistoryRepository
            .findById(orderHistoryDTO.getId())
            .map(existingOrderHistory -> {
                orderHistoryMapper.partialUpdate(existingOrderHistory, orderHistoryDTO);

                return existingOrderHistory;
            })
            .map(orderHistoryRepository::save)
            .map(orderHistoryMapper::toDto);
    }

    /**
     * Get all the orderHistories.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<OrderHistoryDTO> findAll() {
        log.debug("Request to get all OrderHistories");
        return orderHistoryRepository.findAll().stream().map(orderHistoryMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the orderHistories with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<OrderHistoryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return orderHistoryRepository.findAllWithEagerRelationships(pageable).map(orderHistoryMapper::toDto);
    }

    /**
     * Get one orderHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OrderHistoryDTO> findOne(UUID id) {
        log.debug("Request to get OrderHistory : {}", id);
        return orderHistoryRepository.findOneWithEagerRelationships(id).map(orderHistoryMapper::toDto);
    }

    /**
     * Delete the orderHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete OrderHistory : {}", id);
        orderHistoryRepository.deleteById(id);
    }
}
