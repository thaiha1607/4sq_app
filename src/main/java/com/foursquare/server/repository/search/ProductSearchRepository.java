package com.foursquare.server.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.foursquare.server.domain.Product;
import com.foursquare.server.repository.ProductRepository;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Product} entity.
 */
public interface ProductSearchRepository extends ElasticsearchRepository<Product, UUID>, ProductSearchRepositoryInternal {}

interface ProductSearchRepositoryInternal {
    Stream<Product> search(String query);

    Stream<Product> search(Query query);

    @Async
    void index(Product entity);

    @Async
    void deleteFromIndexById(UUID id);
}

class ProductSearchRepositoryInternalImpl implements ProductSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ProductRepository repository;

    ProductSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ProductRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Product> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Product> search(Query query) {
        return elasticsearchTemplate.search(query, Product.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Product entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(UUID id) {
        elasticsearchTemplate.delete(String.valueOf(id), Product.class);
    }
}
