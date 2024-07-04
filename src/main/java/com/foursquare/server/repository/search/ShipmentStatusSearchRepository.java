package com.foursquare.server.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.foursquare.server.domain.ShipmentStatus;
import com.foursquare.server.repository.ShipmentStatusRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link ShipmentStatus} entity.
 */
public interface ShipmentStatusSearchRepository
    extends ElasticsearchRepository<ShipmentStatus, Long>, ShipmentStatusSearchRepositoryInternal {}

interface ShipmentStatusSearchRepositoryInternal {
    Stream<ShipmentStatus> search(String query);

    Stream<ShipmentStatus> search(Query query);

    @Async
    void index(ShipmentStatus entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ShipmentStatusSearchRepositoryInternalImpl implements ShipmentStatusSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ShipmentStatusRepository repository;

    ShipmentStatusSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ShipmentStatusRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<ShipmentStatus> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<ShipmentStatus> search(Query query) {
        return elasticsearchTemplate.search(query, ShipmentStatus.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(ShipmentStatus entity) {
        repository.findById(entity.getStatusCode()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), ShipmentStatus.class);
    }
}
