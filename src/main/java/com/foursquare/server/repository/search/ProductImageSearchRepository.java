package com.foursquare.server.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.foursquare.server.domain.ProductImage;
import com.foursquare.server.repository.ProductImageRepository;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link ProductImage} entity.
 */
public interface ProductImageSearchRepository extends ElasticsearchRepository<ProductImage, UUID>, ProductImageSearchRepositoryInternal {}

interface ProductImageSearchRepositoryInternal {
    Stream<ProductImage> search(String query);

    Stream<ProductImage> search(Query query);

    @Async
    void index(ProductImage entity);

    @Async
    void deleteFromIndexById(UUID id);
}

class ProductImageSearchRepositoryInternalImpl implements ProductImageSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ProductImageRepository repository;

    ProductImageSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ProductImageRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<ProductImage> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<ProductImage> search(Query query) {
        return elasticsearchTemplate.search(query, ProductImage.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(ProductImage entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(UUID id) {
        elasticsearchTemplate.delete(String.valueOf(id), ProductImage.class);
    }
}
