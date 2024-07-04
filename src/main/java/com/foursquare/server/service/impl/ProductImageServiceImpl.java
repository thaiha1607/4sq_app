package com.foursquare.server.service.impl;

import com.foursquare.server.domain.ProductImage;
import com.foursquare.server.repository.ProductImageRepository;
import com.foursquare.server.repository.search.ProductImageSearchRepository;
import com.foursquare.server.service.ProductImageService;
import com.foursquare.server.service.dto.ProductImageDTO;
import com.foursquare.server.service.mapper.ProductImageMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.foursquare.server.domain.ProductImage}.
 */
@Service
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private static final Logger log = LoggerFactory.getLogger(ProductImageServiceImpl.class);

    private final ProductImageRepository productImageRepository;

    private final ProductImageMapper productImageMapper;

    private final ProductImageSearchRepository productImageSearchRepository;

    public ProductImageServiceImpl(
        ProductImageRepository productImageRepository,
        ProductImageMapper productImageMapper,
        ProductImageSearchRepository productImageSearchRepository
    ) {
        this.productImageRepository = productImageRepository;
        this.productImageMapper = productImageMapper;
        this.productImageSearchRepository = productImageSearchRepository;
    }

    @Override
    public ProductImageDTO save(ProductImageDTO productImageDTO) {
        log.debug("Request to save ProductImage : {}", productImageDTO);
        ProductImage productImage = productImageMapper.toEntity(productImageDTO);
        productImage = productImageRepository.save(productImage);
        productImageSearchRepository.index(productImage);
        return productImageMapper.toDto(productImage);
    }

    @Override
    public ProductImageDTO update(ProductImageDTO productImageDTO) {
        log.debug("Request to update ProductImage : {}", productImageDTO);
        ProductImage productImage = productImageMapper.toEntity(productImageDTO);
        productImage.setIsPersisted();
        productImage = productImageRepository.save(productImage);
        productImageSearchRepository.index(productImage);
        return productImageMapper.toDto(productImage);
    }

    @Override
    public Optional<ProductImageDTO> partialUpdate(ProductImageDTO productImageDTO) {
        log.debug("Request to partially update ProductImage : {}", productImageDTO);

        return productImageRepository
            .findById(productImageDTO.getId())
            .map(existingProductImage -> {
                productImageMapper.partialUpdate(existingProductImage, productImageDTO);

                return existingProductImage;
            })
            .map(productImageRepository::save)
            .map(savedProductImage -> {
                productImageSearchRepository.index(savedProductImage);
                return savedProductImage;
            })
            .map(productImageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageDTO> findAll() {
        log.debug("Request to get all ProductImages");
        return productImageRepository.findAll().stream().map(productImageMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductImageDTO> findOne(UUID id) {
        log.debug("Request to get ProductImage : {}", id);
        return productImageRepository.findById(id).map(productImageMapper::toDto);
    }

    @Override
    public void delete(UUID id) {
        log.debug("Request to delete ProductImage : {}", id);
        productImageRepository.deleteById(id);
        productImageSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageDTO> search(String query) {
        log.debug("Request to search ProductImages for query {}", query);
        try {
            return StreamSupport.stream(productImageSearchRepository.search(query).spliterator(), false)
                .map(productImageMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
