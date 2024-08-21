package com.foursquare.server.service;

import com.foursquare.server.domain.OrderStatus;
import com.foursquare.server.repository.OrderStatusRepository;
import com.foursquare.server.service.dto.OrderStatusDTO;
import com.foursquare.server.service.mapper.OrderStatusMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.foursquare.server.domain.OrderStatus}.
 */
@Service
@Transactional
public class OrderStatusService {

    private static final Logger log = LoggerFactory.getLogger(OrderStatusService.class);

    private final OrderStatusRepository orderStatusRepository;

    private final OrderStatusMapper orderStatusMapper;

    public OrderStatusService(OrderStatusRepository orderStatusRepository, OrderStatusMapper orderStatusMapper) {
        this.orderStatusRepository = orderStatusRepository;
        this.orderStatusMapper = orderStatusMapper;
    }

    /**
     * Save a orderStatus.
     *
     * @param orderStatusDTO the entity to save.
     * @return the persisted entity.
     */
    public OrderStatusDTO save(OrderStatusDTO orderStatusDTO) {
        log.debug("Request to save OrderStatus : {}", orderStatusDTO);
        OrderStatus orderStatus = orderStatusMapper.toEntity(orderStatusDTO);
        orderStatus = orderStatusRepository.save(orderStatus);
        return orderStatusMapper.toDto(orderStatus);
    }

    /**
     * Update a orderStatus.
     *
     * @param orderStatusDTO the entity to save.
     * @return the persisted entity.
     */
    public OrderStatusDTO update(OrderStatusDTO orderStatusDTO) {
        log.debug("Request to update OrderStatus : {}", orderStatusDTO);
        OrderStatus orderStatus = orderStatusMapper.toEntity(orderStatusDTO);
        orderStatus.setIsPersisted();
        orderStatus = orderStatusRepository.save(orderStatus);
        return orderStatusMapper.toDto(orderStatus);
    }

    /**
     * Partially update a orderStatus.
     *
     * @param orderStatusDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OrderStatusDTO> partialUpdate(OrderStatusDTO orderStatusDTO) {
        log.debug("Request to partially update OrderStatus : {}", orderStatusDTO);

        return orderStatusRepository
            .findById(orderStatusDTO.getId())
            .map(existingOrderStatus -> {
                orderStatusMapper.partialUpdate(existingOrderStatus, orderStatusDTO);

                return existingOrderStatus;
            })
            .map(orderStatusRepository::save)
            .map(orderStatusMapper::toDto);
    }

    /**
     * Get all the orderStatuses.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<OrderStatusDTO> findAll() {
        log.debug("Request to get all OrderStatuses");
        return orderStatusRepository.findAll().stream().map(orderStatusMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one orderStatus by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OrderStatusDTO> findOne(Long id) {
        log.debug("Request to get OrderStatus : {}", id);
        return orderStatusRepository.findById(id).map(orderStatusMapper::toDto);
    }

    /**
     * Delete the orderStatus by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete OrderStatus : {}", id);
        orderStatusRepository.deleteById(id);
    }
}
