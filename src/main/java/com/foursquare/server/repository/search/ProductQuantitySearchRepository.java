package com.foursquare.server.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.foursquare.server.domain.ProductQuantity;
import com.foursquare.server.repository.ProductQuantityRepository;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link ProductQuantity} entity.
 */
public interface ProductQuantitySearchRepository
    extends ElasticsearchRepository<ProductQuantity, UUID>, ProductQuantitySearchRepositoryInternal {}

interface ProductQuantitySearchRepositoryInternal {
    Stream<ProductQuantity> search(String query);

    Stream<ProductQuantity> search(Query query);

    @Async
    void index(ProductQuantity entity);

    @Async
    void deleteFromIndexById(UUID id);
}

class ProductQuantitySearchRepositoryInternalImpl implements ProductQuantitySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ProductQuantityRepository repository;

    ProductQuantitySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ProductQuantityRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<ProductQuantity> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<ProductQuantity> search(Query query) {
        return elasticsearchTemplate.search(query, ProductQuantity.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(ProductQuantity entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(UUID id) {
        elasticsearchTemplate.delete(String.valueOf(id), ProductQuantity.class);
    }
}
