package com.foursquare.server.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.foursquare.server.domain.ShipmentAssignment;
import com.foursquare.server.repository.ShipmentAssignmentRepository;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link ShipmentAssignment} entity.
 */
public interface ShipmentAssignmentSearchRepository
    extends ElasticsearchRepository<ShipmentAssignment, UUID>, ShipmentAssignmentSearchRepositoryInternal {}

interface ShipmentAssignmentSearchRepositoryInternal {
    Stream<ShipmentAssignment> search(String query);

    Stream<ShipmentAssignment> search(Query query);

    @Async
    void index(ShipmentAssignment entity);

    @Async
    void deleteFromIndexById(UUID id);
}

class ShipmentAssignmentSearchRepositoryInternalImpl implements ShipmentAssignmentSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ShipmentAssignmentRepository repository;

    ShipmentAssignmentSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ShipmentAssignmentRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<ShipmentAssignment> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<ShipmentAssignment> search(Query query) {
        return elasticsearchTemplate.search(query, ShipmentAssignment.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(ShipmentAssignment entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(UUID id) {
        elasticsearchTemplate.delete(String.valueOf(id), ShipmentAssignment.class);
    }
}
