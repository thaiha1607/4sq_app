package com.foursquare.server.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.foursquare.server.domain.ShipmentItem;
import com.foursquare.server.repository.ShipmentItemRepository;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link ShipmentItem} entity.
 */
public interface ShipmentItemSearchRepository extends ElasticsearchRepository<ShipmentItem, UUID>, ShipmentItemSearchRepositoryInternal {}

interface ShipmentItemSearchRepositoryInternal {
    Stream<ShipmentItem> search(String query);

    Stream<ShipmentItem> search(Query query);

    @Async
    void index(ShipmentItem entity);

    @Async
    void deleteFromIndexById(UUID id);
}

class ShipmentItemSearchRepositoryInternalImpl implements ShipmentItemSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ShipmentItemRepository repository;

    ShipmentItemSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ShipmentItemRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<ShipmentItem> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<ShipmentItem> search(Query query) {
        return elasticsearchTemplate.search(query, ShipmentItem.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(ShipmentItem entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(UUID id) {
        elasticsearchTemplate.delete(String.valueOf(id), ShipmentItem.class);
    }
}
