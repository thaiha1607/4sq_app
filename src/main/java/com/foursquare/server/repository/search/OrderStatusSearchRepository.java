package com.foursquare.server.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.foursquare.server.domain.OrderStatus;
import com.foursquare.server.repository.OrderStatusRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link OrderStatus} entity.
 */
public interface OrderStatusSearchRepository extends ElasticsearchRepository<OrderStatus, Long>, OrderStatusSearchRepositoryInternal {}

interface OrderStatusSearchRepositoryInternal {
    Stream<OrderStatus> search(String query);

    Stream<OrderStatus> search(Query query);

    @Async
    void index(OrderStatus entity);

    @Async
    void deleteFromIndexById(Long id);
}

class OrderStatusSearchRepositoryInternalImpl implements OrderStatusSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final OrderStatusRepository repository;

    OrderStatusSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, OrderStatusRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<OrderStatus> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<OrderStatus> search(Query query) {
        return elasticsearchTemplate.search(query, OrderStatus.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(OrderStatus entity) {
        repository.findById(entity.getStatusCode()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), OrderStatus.class);
    }
}
